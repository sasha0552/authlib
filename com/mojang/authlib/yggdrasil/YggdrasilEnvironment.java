package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public enum YggdrasilEnvironment {
    PROD(
        "https://authserver.mojang.com",
        "https://api.mojang.com",
        "https://sessionserver.mojang.com",
        "https://api.minecraftservices.com"
    ),
    STAGING(
        "https://yggdrasil-auth-staging.mojang.com",
        "https://api-staging.mojang.com",
        "https://yggdrasil-auth-session-staging.mojang.zone",
        "https://api-staging.minecraftservices.com"
    );

    private final Environment environment;

    YggdrasilEnvironment(final String authHost, final String accountsHost, final String sessionHost, final String servicesHost) {
        this.environment = Environment.create(authHost, accountsHost, sessionHost, servicesHost, name());
    }

    public Environment getEnvironment() {
        return environment;
    }

    public static Optional<Environment> fromString(@Nullable String value) {
        return Stream
            .of(YggdrasilEnvironment.values())
            .filter(env -> value != null && value.equalsIgnoreCase(env.name()))
            .findFirst()
            .map(YggdrasilEnvironment::getEnvironment);
    }
}
