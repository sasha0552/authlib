package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import com.mojang.authlib.yggdrasil.response.PrivilegesResponse;

import javax.annotation.Nullable;
import java.net.URL;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class YggdrasilSocialInteractionsService implements SocialInteractionsService {
    private static final long BLOCKLIST_REQUEST_COOLDOWN_SECONDS = 120;
    private static final UUID ZERO_UUID = new UUID(0, 0);

    private final URL routePrivileges;
    private final URL routeBlocklist;

    private final YggdrasilAuthenticationService authenticationService;
    private final String accessToken;
    private boolean serversAllowed;
    private boolean realmsAllowed;
    private boolean chatAllowed;
    @Nullable
    private Instant nextAcceptableBlockRequest;

    @Nullable
    private Set<UUID> blockList;

    public YggdrasilSocialInteractionsService(final YggdrasilAuthenticationService authenticationService, final String accessToken, final Environment env) throws AuthenticationException {
        this.authenticationService = authenticationService;
        this.accessToken = accessToken;
        routePrivileges = HttpAuthenticationService.constantURL(env.getServicesHost() + "/privileges");
        routeBlocklist = HttpAuthenticationService.constantURL(env.getServicesHost() + "/privacy/blocklist");
        checkPrivileges();
    }

    @Override
    public boolean serversAllowed() {
        return serversAllowed;
    }

    @Override
    public boolean realmsAllowed() {
        return realmsAllowed;
    }

    @Override
    public boolean chatAllowed() {
        return chatAllowed;
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

    @Nullable
    private Set<UUID> fetchBlockList() {
        if (nextAcceptableBlockRequest != null && nextAcceptableBlockRequest.isAfter(Instant.now())) {
            return null;
        }
        nextAcceptableBlockRequest = Instant.now().plusSeconds(BLOCKLIST_REQUEST_COOLDOWN_SECONDS);
        try {
            final BlockListResponse response = authenticationService.makeRequest(routeBlocklist, null, BlockListResponse.class, "Bearer " + accessToken);
            if (response == null) {
                return null;
            }

            return response.getBlockedProfiles();
        }
        catch (final AuthenticationException e) {
            return null;
        }
    }

    private void checkPrivileges() throws AuthenticationException {
        final PrivilegesResponse response = authenticationService.makeRequest(routePrivileges, null, PrivilegesResponse.class, "Bearer " + accessToken);
        if (response == null) {
            // Treat getting an invalid or empty response the same as the service being down
            throw new AuthenticationUnavailableException();
        }
        chatAllowed = response.getPrivileges().getOnlineChat().isEnabled();
        serversAllowed = response.getPrivileges().getMultiplayerServer().isEnabled();
        realmsAllowed = response.getPrivileges().getMultiplayerRealms().isEnabled();
    }
}
