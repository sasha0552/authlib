package com.mojang.authlib.yggdrasil;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;

import javax.annotation.Nullable;
import java.net.Proxy;
import java.net.URL;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public class YggdrasilUserApiService implements UserApiService {
    private static final long BLOCKLIST_REQUEST_COOLDOWN_SECONDS = 120;
    private static final UUID ZERO_UUID = new UUID(0, 0);

    private final URL routePrivileges;
    private final URL routeBlocklist;
    private final URL routeKeyPair;
    private final URL routeAbuseReport;

    private final MinecraftClient minecraftClient;
    private final Environment environment;
    private UserProperties properties = OFFLINE_PROPERTIES;
    @Nullable
    private Instant nextAcceptableBlockRequest;

    @Nullable
    private Set<UUID> blockList;

    public YggdrasilUserApiService(final String accessToken, final Proxy proxy, final Environment env) throws AuthenticationException {
        this.minecraftClient = new MinecraftClient(accessToken, proxy);
        environment = env;
        routePrivileges = HttpAuthenticationService.constantURL(env.servicesHost() + "/player/attributes");
        routeBlocklist = HttpAuthenticationService.constantURL(env.servicesHost() + "/privacy/blocklist");
        routeKeyPair = HttpAuthenticationService.constantURL(env.servicesHost() + "/player/certificates");
        routeAbuseReport = HttpAuthenticationService.constantURL(env.servicesHost() + "/player/report");
        fetchProperties();
    }

    @Override
    public UserProperties properties() {
        return properties;
    }

    @Override
    public TelemetrySession newTelemetrySession(final Executor executor) {
        if (!properties.flag(UserFlag.TELEMETRY_ENABLED)) {
            return TelemetrySession.DISABLED;
        }
        return new YggdrassilTelemetrySession(minecraftClient, environment, executor);
    }

    @Override
    public KeyPairResponse getKeyPair() {
        return minecraftClient.post(routeKeyPair, KeyPairResponse.class);
    }

    @Override
    public boolean isBlockedPlayer(final UUID playerID) {
        if (playerID.equals(ZERO_UUID)) {
            return false;
        }

        if (blockList == null) {
            blockList = fetchBlockList();
            if (blockList == null) {
                return false;
            }
        }

        return blockList.contains(playerID);
    }

    @Override
    public void refreshBlockList() {
        if (blockList == null || canMakeBlockListRequest()) {
            blockList = forceFetchBlockList();
        }
    }

    @Nullable
    private Set<UUID> fetchBlockList() {
        if (!canMakeBlockListRequest()) {
            return null;
        }
        return forceFetchBlockList();
    }

    private boolean canMakeBlockListRequest() {
        return nextAcceptableBlockRequest == null || Instant.now().isAfter(nextAcceptableBlockRequest);
    }

    private Set<UUID> forceFetchBlockList() {
        nextAcceptableBlockRequest = Instant.now().plusSeconds(BLOCKLIST_REQUEST_COOLDOWN_SECONDS);
        try {
            final BlockListResponse response = minecraftClient.get(routeBlocklist, BlockListResponse.class);
            if (response == null) {
                return Set.of();
            }
            return response.blockedProfiles();
        } catch (final MinecraftClientHttpException e) {
            //TODO: Look at the error type and response code. Retry if 5xx
            //TODO: Handle when status is 401 (Unathorized) -> Refresh token/login again.
            return null;
        } catch (final MinecraftClientException e) {
            //Low level error, IO problems or JSON parsing error
            //TODO: Retry if SERVICE_UNAVAILABLE error
            return null;
        }
    }

    private void fetchProperties() throws AuthenticationException {
        try {
            final UserAttributesResponse response = minecraftClient.get(routePrivileges, UserAttributesResponse.class);
            final ImmutableSet.Builder<UserFlag> flags = ImmutableSet.builder();
            final ImmutableMap.Builder<String, BanDetails> bannedScopes = ImmutableMap.builder();

            if (response != null) {
                final UserAttributesResponse.Privileges privileges = response.privileges();
                if (privileges != null) {
                    addFlagIfUserHasPrivilege(privileges.getOnlineChat(), UserFlag.CHAT_ALLOWED, flags);
                    addFlagIfUserHasPrivilege(privileges.getMultiplayerServer(), UserFlag.SERVERS_ALLOWED, flags);
                    addFlagIfUserHasPrivilege(privileges.getMultiplayerRealms(), UserFlag.REALMS_ALLOWED, flags);
                    addFlagIfUserHasPrivilege(privileges.getTelemetry(), UserFlag.TELEMETRY_ENABLED, flags);
                    addFlagIfUserHasPrivilege(privileges.getOptionalTelemetry(), UserFlag.OPTIONAL_TELEMETRY_AVAILABLE, flags);
                }

                final UserAttributesResponse.ProfanityFilterPreferences profanityFilterPreferences = response.profanityFilterPreferences();
                if (profanityFilterPreferences != null && profanityFilterPreferences.enabled()) {
                    flags.add(UserFlag.PROFANITY_FILTER_ENABLED);
                }

                if (response.banStatus() != null) {
                    response.banStatus().bannedScopes().forEach((scopeType, scope) -> {
                        bannedScopes.put(scopeType, new BanDetails(scope.banId(), scope.expires(), scope.reason(), scope.reasonMessage()));
                    });
                }
            }

            properties = new UserProperties(flags.build(), bannedScopes.build());
        } catch (final MinecraftClientHttpException e) {
            //TODO: Handle when status is 401 (Unauthorized) -> Refresh token/login again.
            throw e.toAuthenticationException();
        } catch (final MinecraftClientException e) {
            //Low level error, IO problems or JSON parsing error
            //TODO: Retry if SERVICE_UNAVAILABLE error
            throw e.toAuthenticationException();
        }
    }

    private static void addFlagIfUserHasPrivilege(final boolean privilege, final UserFlag value, final ImmutableSet.Builder<UserFlag> output) {
        if (privilege) {
            output.add(value);
        }
    }

    @Override
    public void reportAbuse(final AbuseReportRequest request) {
        minecraftClient.post(routeAbuseReport, request, Void.class);
    }

    @Override
    public boolean canSendReports() {
        return true;
    }

    @Override
    public AbuseReportLimits getAbuseReportLimits() {
        return AbuseReportLimits.DEFAULTS;
    }
}
