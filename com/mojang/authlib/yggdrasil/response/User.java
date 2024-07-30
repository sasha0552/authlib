package com.mojang.authlib.yggdrasil.response;

import java.util.List;

public class User {
    private String id;
    private List<Property> properties;

    public String getId() {
        return id;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public class Property {
        private String name;
        private String value;

        public String getKey() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
