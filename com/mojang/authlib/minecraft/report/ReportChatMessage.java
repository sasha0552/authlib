package com.mojang.authlib.minecraft.report;

import java.time.Instant;
import java.util.UUID;

public class ReportChatMessage {
    public UUID profileId;
    public Instant timestamp;
    public long salt;
    public String signature;
    public String message;
    public String overriddenMessage;
    public boolean messageReported;

    public ReportChatMessage(final UUID profileId, final Instant timestamp, final long salt, final String signature, final String message, final String overriddenMessage, final boolean messageReported) {
        this.profileId = profileId;
        this.timestamp = timestamp;
        this.salt = salt;
        this.signature = signature;
        this.message = message;
        this.overriddenMessage = overriddenMessage;
        this.messageReported = messageReported;
    }
}
