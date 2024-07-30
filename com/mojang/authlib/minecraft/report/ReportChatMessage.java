package com.mojang.authlib.minecraft.report;

public class ReportChatMessage {
    public ReportChatMessageHeader header;
    public ReportChatMessageBody body;
    public String overriddenMessage;
    public boolean messageReported;

    public ReportChatMessage(final ReportChatMessageHeader header, final ReportChatMessageBody body, final String overriddenMessage, final boolean messageReported) {
        this.header = header;
        this.body = body;
        this.overriddenMessage = overriddenMessage;
        this.messageReported = messageReported;
    }
}
