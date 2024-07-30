package com.mojang.authlib.minecraft.report;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ReportChatMessageHeader {
    public ByteBuffer signatureOfPreviousHeader;
    public UUID profileId;
    public ByteBuffer hashOfBody;
    public ByteBuffer signature;

    public ReportChatMessageHeader(final ByteBuffer signatureOfPreviousHeader, final UUID profileId, final ByteBuffer hashOfBody, final ByteBuffer signature) {
        this.signatureOfPreviousHeader = signatureOfPreviousHeader;
        this.profileId = profileId;
        this.hashOfBody = hashOfBody;
        this.signature = signature;
    }
}
