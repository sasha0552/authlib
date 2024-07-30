package com.mojang.authlib.minecraft;

import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;

public abstract class BaseMinecraftSessionService implements MinecraftSessionService {
    private final AuthenticationService authenticationService;

    protected BaseMinecraftSessionService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile profile) {
        return fillProfileProperties(profile, false);
    }
}
