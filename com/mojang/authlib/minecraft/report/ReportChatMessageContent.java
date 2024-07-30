package com.mojang.authlib.minecraft.report;

public class ReportChatMessageContent {
    public String plain;
    public String decorated;

    public ReportChatMessageContent(final String plain, final String decorated) {
        this.plain = plain;
        this.decorated = decorated;
    }
}
