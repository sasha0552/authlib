package com.mojang.authlib;

import org.apache.commons.lang3.StringUtils;

public class GameProfile {
    private final String id;
    private final String name;

    /**
     * Constructs a new Game Profile with the specified ID and name.
     * <p />
     * Either ID or name may be null/empty, but at least one must be filled.
     *
     * @param id Unique ID of the profile
     * @param name Display name of the profile
     * @throws java.lang.IllegalArgumentException Both ID and name are either null or empty
     */
    public GameProfile(String id, String name) {
        if (StringUtils.isBlank(id) && StringUtils.isBlank(name)) throw new IllegalArgumentException("Name and ID cannot both be blank");

        this.id = id;
        this.name = name;
    }

    /**
     * Gets the unique ID of this game profile.
     * <p />
     * This may be null for partial profile data if constructed manually.
     *
     * @return ID of the profile
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the display name of this game profile.
     * <p />
     * This may be null for partial profile data if constructed manually.
     *
     * @return Name of the profile
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if this profile is complete.
     * <p />
     * A complete profile has no empty fields. Partial profiles may be constructed manually and used as input to methods.
     *
     * @return True if this profile is complete (as opposed to partial)
     */
    public boolean isComplete() {
        return StringUtils.isNotBlank(getId()) && StringUtils.isNotBlank(getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameProfile that = (GameProfile) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GameProfile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
