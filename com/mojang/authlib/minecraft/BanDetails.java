package com.mojang.authlib.minecraft;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.UUID;

public record BanDetails(UUID id, @Nullable Instant expires, @Nullable String reason) {
    public static final String MULTIPLAYER_SCOPE = "MULTIPLAYER";
}
