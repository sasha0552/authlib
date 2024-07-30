package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.net.URL;

public class YggdrasilAuthenticationService extends HttpAuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(YggdrasilAuthenticationService.class);

    private final Environment environment;
    private final ServicesKeySet servicesKeySet;

    public YggdrasilAuthenticationService(final Proxy proxy) {
        this(proxy, determineEnvironment());
    }

    public YggdrasilAuthenticationService(final Proxy proxy, final Environment environment) {
        super(proxy);
        this.environment = environment;
        LOGGER.info("Environment: {}", environment);

        final MinecraftClient client = MinecraftClient.unauthenticated(proxy);
        final URL publicKeySetUrl = HttpAuthenticationService.constantURL(environment.servicesHost() + "/publickeys");
        servicesKeySet = YggdrasilServicesKeyInfo.get(publicKeySetUrl, client);
    }

    private static Environment determineEnvironment() {
        return EnvironmentParser
                   .getEnvironmentFromProperties()
                   .orElse(YggdrasilEnvironment.PROD.getEnvironment());
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return new YggdrasilMinecraftSessionService(servicesKeySet, getProxy(), environment);
    }

    @Override
    public GameProfileRepository createProfileRepository() {
        return new YggdrasilGameProfileRepository(getProxy(), environment);
    }

    public UserApiService createUserApiService(final String accessToken) throws AuthenticationException {
        return new YggdrasilUserApiService(accessToken, getProxy(), environment);
    }

    public ServicesKeySet getServicesKeySet() {
        return servicesKeySet;
    }
}
