package com.mojang.authlib;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseUserAuthentication implements UserAuthentication {
    protected static final String STORAGE_KEY_PROFILE_NAME = "displayName";
    protected static final String STORAGE_KEY_PROFILE_ID = "uuid";
    protected static final String STORAGE_KEY_USER_NAME = "username";
    protected static final String STORAGE_KEY_USER_ID = "userid";

    private final AuthenticationService authenticationService;
    private final Map<String, Collection<String>> userProperties = new HashMap<String, Collection<String>>();
    private String userid;
    private String username;
    private String password;
    private GameProfile selectedProfile;

    protected BaseUserAuthentication(AuthenticationService authenticationService) {
        Validate.notNull(authenticationService);
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean canLogIn() {
        return !canPlayOnline() && StringUtils.isNotBlank(getUsername()) && StringUtils.isNotBlank(getPassword());
    }

    @Override
    public void logOut() {
        password = null;
        userid = null;
        setSelectedProfile(null);
        getModifiableUserProperties().clear();
    }

    @Override
    public boolean isLoggedIn() {
        return getSelectedProfile() != null;
    }

    @Override
    public void setUsername(String username) {
        if (isLoggedIn() && canPlayOnline()) {
            throw new IllegalStateException("Cannot change username whilst logged in & online");
        }

        this.username = username;
    }

    @Override
    public void setPassword(String password) {
        if (isLoggedIn() && canPlayOnline() && StringUtils.isNotBlank(password)) {
            throw new IllegalStateException("Cannot set password whilst logged in & online");
        }

        this.password = password;
    }

    protected String getUsername() {
        return username;
    }

    protected String getPassword() {
        return password;
    }

    @Override
    public void loadFromStorage(Map<String, String> credentials) {
        logOut();

        setUsername(credentials.get(STORAGE_KEY_USER_NAME));

        if (credentials.containsKey(STORAGE_KEY_USER_ID)) {
            userid = credentials.get(STORAGE_KEY_USER_ID);
        } else {
            userid = username;
        }

        if (credentials.containsKey(STORAGE_KEY_PROFILE_NAME) && credentials.containsKey(STORAGE_KEY_PROFILE_ID)) {
            setSelectedProfile(new GameProfile(credentials.get(STORAGE_KEY_PROFILE_ID), credentials.get(STORAGE_KEY_PROFILE_NAME)));
        }
    }

    @Override
    public Map<String, String> saveForStorage() {
        Map<String, String> result = new HashMap<String, String>();

        if (getUsername() != null) {
            result.put(STORAGE_KEY_USER_NAME, getUsername());
        }
        if (getUserID() != null) {
            result.put(STORAGE_KEY_USER_ID, getUserID());
        } else if (getUsername() != null) {
            result.put(STORAGE_KEY_USER_NAME, getUsername());
        }

        if (getSelectedProfile() != null) {
            result.put(STORAGE_KEY_PROFILE_NAME, getSelectedProfile().getName());
            result.put(STORAGE_KEY_PROFILE_ID, getSelectedProfile().getId());
        }

        return result;
    }

    protected void setSelectedProfile(GameProfile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    @Override
    public GameProfile getSelectedProfile() {
        return selectedProfile;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append(getClass().getSimpleName());
        result.append("{");

        if (isLoggedIn()) {
            result.append("Logged in as ");
            result.append(getUsername());

            if (getSelectedProfile() != null) {
                result.append(" / ");
                result.append(getSelectedProfile());
                result.append(" - ");

                if (canPlayOnline()) {
                    result.append("Online");
                } else {
                    result.append("Offline");
                }
            }
        } else {
            result.append("Not logged in");
        }

        result.append("}");

        return result.toString();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    @Override
    public String getUserID() {
        return userid;
    }

    @Override
    public Map<String, Collection<String>> getUserProperties() {
        if (isLoggedIn()) {
            return Collections.unmodifiableMap(getModifiableUserProperties());
        } else {
            return Collections.unmodifiableMap(new HashMap<String, Collection<String>>());
        }
    }

    protected Map<String, Collection<String>> getModifiableUserProperties() {
        return userProperties;
    }

    protected void setUserid(String userid) {
        this.userid = userid;
    }
}
