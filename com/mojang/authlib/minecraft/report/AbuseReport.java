package com.mojang.authlib.minecraft.report;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;

public record AbuseReport(
    @SerializedName("opinionComments")
    String opinionComments,
    @SerializedName("reason")
    String reason,
    @SerializedName("evidence")
    ReportEvidence evidence,
    @SerializedName("reportedEntity")
    ReportedEntity reportedEntity,
    @SerializedName("createdTime")
    Instant createdTime
) {
}
