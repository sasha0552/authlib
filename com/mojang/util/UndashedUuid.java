package com.mojang.util;

import java.util.UUID;

public class UndashedUuid {
    public static UUID fromString(final String string) {
        if (string.indexOf('-') != -1) {
            throw new IllegalArgumentException("Invalid undashed UUID string: " + string);
        }
        return fromStringLenient(string);
    }

    public static UUID fromStringLenient(final String string) {
        return UUID.fromString(string.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    public static String toString(final UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
