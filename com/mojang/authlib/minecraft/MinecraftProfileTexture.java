package com.mojang.authlib.minecraft;

import com.google.common.collect.Maps;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.util.Map;

public class MinecraftProfileTexture {
    public enum Type {
        SKIN,
        CAPE,
        ;
    }

    private final String url;
    private final Map<String, String> metadata;

    public MinecraftProfileTexture(String url, Map<String, String> metadata) {
        this.url = url;
        this.metadata = metadata;
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public String getMetadata(String key) {
        if (metadata == null) {
            return null;
        }
        return metadata.get(key);
    }

    public String getHash() {
        return FilenameUtils.getBaseName(url);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("url", url)
                .append("hash", getHash())
                .toString();
    }
}
