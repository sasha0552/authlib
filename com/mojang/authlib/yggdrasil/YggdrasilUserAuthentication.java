package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.HttpUserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.yggdrasil.request.AuthenticationRequest;
import com.mojang.authlib.yggdrasil.request.RefreshRequest;
import com.mojang.authlib.yggdrasil.response.AuthenticationResponse;
import com.mojang.authlib.yggdrasil.response.RefreshResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class YggdrasilUserAuthentication extends HttpUserAuthentication {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BASE_URL = "https://authserver.mojang.com/";
    private static final URL ROUTE_AUTHENTICATE = HttpAuthenticationService.constantURL(BASE_URL + "authenticate");
    private static final URL ROUTE_REFRESH = HttpAuthenticationService.constantURL(BASE_URL + "refresh");
    private static final URL ROUTE_VALIDATE = HttpAuthenticationService.constantURL(BASE_URL + "validate");
    private static final URL ROUTE_INVALIDATE = HttpAuthenticationService.constantURL(BASE_URL + "invalidate");
    private static final URL ROUTE_SIGNOUT = HttpAuthenticationService.constantURL(BASE_URL + "signout");

    private static final String STORAGE_KEY_ACCESS_TOKEN = "accessToken";

    private final Agent agent;
    private GameProfile[] profiles;
    private String accessToken;
    private boolean isOnline;

    public YggdrasilUserAuthentication(YggdrasilAuthenticationService authenticationService, Agent agent) {
        super(authenticationService);
        this.agent = agent;
    }

    @Override
    public boolean canLogIn() {
        return !canPlayOnline() && StringUtils.isNotBlank(getUsername()) && (StringUtils.isNotBlank(getPassword()) || StringUtils.isNotBlank(getAuthenticatedToken()));
    }

    @Override
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }

        if (StringUtils.isNotBlank(getAuthenticatedToken())) {
            logInWithToken();
        } else if (StringUtils.isNotBlank(getPassword())) {
            logInWithPassword();
        } else {
            throw new InvalidCredentialsException("Invalid password");
        }
    }

    protected void logInWithPassword() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        LOGGER.info("Logging in with username & password");

        AuthenticationRequest request = new AuthenticationRequest(this, getUsername(), getPassword());
        AuthenticationResponse response = getAuthenticationService().makeRequest(ROUTE_AUTHENTICATE, request, AuthenticationResponse.class);

        if (!response.getClientToken().equals(getAuthenticationService().getClientToken())) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }

        if (response.getUser() != null && response.getUser().getId() != null) {
            setUserid(response.getUser().getId());
        } else {
            setUserid(getUsername());
        }

        isOnline = true;
        accessToken = response.getAccessToken();
        profiles = response.getAvailableProfiles();
        setSelectedProfile(response.getSelectedProfile());
    }

    protected void logInWithToken() throws AuthenticationException {
        if (StringUtils.isBlank(getUserID())) {
            if (StringUtils.isBlank(getUsername())) {
                setUserid(getUsername());
            } else {
                throw new InvalidCredentialsException("Invalid uuid & username");
            }
        }
        if (StringUtils.isBlank(getAuthenticatedToken())) {
            throw new InvalidCredentialsException("Invalid access token");
        }

        LOGGER.info("Logging in with access token");

        RefreshRequest request = new RefreshRequest(this);
        RefreshResponse response = getAuthenticationService().makeRequest(ROUTE_REFRESH, request, RefreshResponse.class);

        if (!response.getClientToken().equals(getAuthenticationService().getClientToken())) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }

        isOnline = true;
        accessToken = response.getAccessToken();
        profiles = response.getAvailableProfiles();
        setSelectedProfile(response.getSelectedProfile());
    }

    @Override
    public void logOut() {
        super.logOut();

        accessToken = null;
        profiles = null;
        isOnline = false;
    }

    @Override
    public GameProfile[] getAvailableProfiles() {
        return profiles;
    }

    @Override
    public boolean isLoggedIn() {
        return StringUtils.isNotBlank(accessToken);
    }

    @Override
    public boolean canPlayOnline() {
        return isLoggedIn() && getSelectedProfile() != null && isOnline;
    }

    @Override
    public void selectGameProfile(GameProfile profile) throws AuthenticationException {
        if (!isLoggedIn()) {
            throw new AuthenticationException("Cannot change game profile whilst not logged in");
        }
        if (getSelectedProfile() != null) {
            throw new AuthenticationException("Cannot change game profile. You must log out and back in.");
        }
        if (profile == null || !ArrayUtils.contains(profiles, profile)) {
            throw new IllegalArgumentException("Invalid profile '" + profile + "'");
        }

        RefreshRequest request = new RefreshRequest(this, profile);
        RefreshResponse response = getAuthenticationService().makeRequest(ROUTE_REFRESH, request, RefreshResponse.class);

        if (!response.getClientToken().equals(getAuthenticationService().getClientToken())) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }

        isOnline = true;
        accessToken = response.getAccessToken();
        setSelectedProfile(response.getSelectedProfile());
    }

    @Override
    public void loadFromStorage(Map<String, String> credentials) {
        super.loadFromStorage(credentials);

        accessToken = credentials.get(STORAGE_KEY_ACCESS_TOKEN);
    }

    @Override
    public Map<String, String> saveForStorage() {
        Map<String, String> result = super.saveForStorage();

        if (StringUtils.isNotBlank(getAuthenticatedToken())) {
            result.put(STORAGE_KEY_ACCESS_TOKEN, getAuthenticatedToken());
        }

        return result;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getSessionToken() {
        if (isLoggedIn() && getSelectedProfile() != null && canPlayOnline()) {
            return String.format("token:%s:%s", getAuthenticatedToken(), getSelectedProfile().getId());
        } else {
            return null;
        }
    }

    @Override
    public String getAuthenticatedToken() {
        return accessToken;
    }

    public Agent getAgent() {
        return agent;
    }

    @Override
    public String toString() {
        return "YggdrasilAuthenticationService{" +
                "agent=" + agent +
                ", profiles=" + Arrays.toString(profiles) +
                ", selectedProfile=" + getSelectedProfile() +
                ", username='" + getUsername() + '\''+
                ", isLoggedIn=" + isLoggedIn() +
                ", canPlayOnline=" + canPlayOnline() +
                ", accessToken='" + accessToken + '\'' +
                ", clientToken='" + getAuthenticationService().getClientToken() + '\'' +
                '}';
    }

    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService) super.getAuthenticationService();
    }
}
