package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public enum YggdrasilEnvironment {
    PROD(
        "https://api.mojang.com",
        "https://sessionserver.mojang.com",
        "https://api.minecraftservices.com"
    ),
    STAGING(
        "https://api-staging.mojang.com",
        "https://yggdrasil-auth-session-staging.mojang.zone",
        "https://api-staging.minecraftservices.com"
    );

    private final Environment environment;

    YggdrasilEnvironment(final String accountsHost, final String sessionHost, final String servicesHost) {
        this.environment = new Environment(accountsHost, sessionHost, servicesHost, name());
    }

    public Environment getEnvironment() {
        return environment;
    }

    public static Optional<Environment> fromString(@Nullable final String value) {
        return Stream
            .of(YggdrasilEnvironment.values())
            .filter(env -> value != null && value.equalsIgnoreCase(env.name()))
            .findFirst()
            .map(YggdrasilEnvironment::getEnvironment);
    }
}
