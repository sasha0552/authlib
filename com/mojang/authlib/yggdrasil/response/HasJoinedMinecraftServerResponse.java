package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;

public class HasJoinedMinecraftServerResponse extends Response {
    private String id;
    private PropertyMap properties;

    public String getId() {
        return id;
    }

    public PropertyMap getProperties() {
        return properties;
    }
}
