package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum YggdrasilEnvironment implements Environment {

    PROD(
        "https://authserver.mojang.com",
        "https://api.mojang.com",
        "https://sessionserver.mojang.com"
    ),
    STAGING(
        "https://yggdrasil-auth-staging.mojang.com",
        "https://api-staging.mojang.com",
        "https://yggdrasil-auth-session-staging.mojang.zone"
    );

    private final String authHost;
    private final String accountsHost;
    private final String sessionHost;

    YggdrasilEnvironment(final String authHost, final String accountsHost, final String sessionHost) {
        this.authHost = authHost;
        this.accountsHost = accountsHost;
        this.sessionHost = sessionHost;
    }

    public String getAuthHost() {
        return authHost;
    }

    public String getAccountsHost() {
        return accountsHost;
    }

    public String getSessionHost() {
        return sessionHost;
    }

    public String getName() {
        return this.name();
    }

    @Override
    public String asString() {
        return new StringJoiner(", ", "", "")
                   .add("authHost='" + authHost + "'")
                   .add("accountsHost='" + accountsHost + "'")
                   .add("sessionHost='" + sessionHost + "'")
                   .add("name='" + getName() + "'")
                   .toString();
    }

    public static Optional<YggdrasilEnvironment> fromString(@Nullable String value) {
        return Stream
                   .of(YggdrasilEnvironment.values())
                   .filter(env -> value != null && value.equalsIgnoreCase(env.name()))
                   .findFirst();
    }
}
