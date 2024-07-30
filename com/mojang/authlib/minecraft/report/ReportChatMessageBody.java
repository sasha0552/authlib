package com.mojang.authlib.minecraft.report;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ReportChatMessageBody {
    public Instant timestamp;
    public long salt;
    public List<LastSeenSignature> lastSeenSignatures;
    public ReportChatMessageContent message;

    public ReportChatMessageBody(final Instant timestamp, final long salt, final List<LastSeenSignature> lastSeenSignatures, final ReportChatMessageContent message) {
        this.timestamp = timestamp;
        this.salt = salt;
        this.lastSeenSignatures = lastSeenSignatures;
        this.message = message;
    }

    public static class LastSeenSignature {
        public UUID profileId;
        public ByteBuffer lastSignature;

        public LastSeenSignature(final UUID profileId, final ByteBuffer lastSignature) {
            this.profileId = profileId;
            this.lastSignature = lastSignature;
        }
    }
}
