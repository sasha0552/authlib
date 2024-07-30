package com.mojang.authlib.yggdrasil;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.client.MinecraftClient;
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
        routePrivileges = HttpAuthenticationService.constantURL(env.getServicesHost() + "/player/attributes");
        routeBlocklist = HttpAuthenticationService.constantURL(env.getServicesHost() + "/privacy/blocklist");
        routeKeyPair = HttpAuthenticationService.constantURL(env.getServicesHost() + "/player/certificates");
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
            return response.getBlockedProfiles();
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

            final UserAttributesResponse.Privileges privileges = response.getPrivileges();
            if (privileges != null) {
                addFlagIfUserHasPrivilege(privileges.getOnlineChat(), UserFlag.CHAT_ALLOWED, flags);
                addFlagIfUserHasPrivilege(privileges.getMultiplayerServer(), UserFlag.SERVERS_ALLOWED, flags);
                addFlagIfUserHasPrivilege(privileges.getMultiplayerRealms(), UserFlag.REALMS_ALLOWED, flags);
                addFlagIfUserHasPrivilege(privileges.getTelemetry(), UserFlag.TELEMETRY_ENABLED, flags);
            }

            final UserAttributesResponse.ProfanityFilterPreferences profanityFilterPreferences = response.getProfanityFilterPreferences();
            if (profanityFilterPreferences != null && profanityFilterPreferences.isEnabled()) {
                flags.add(UserFlag.PROFANITY_FILTER_ENABLED);
            }

            properties = new UserProperties(flags.build());
        } catch (MinecraftClientHttpException e) {
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
}
