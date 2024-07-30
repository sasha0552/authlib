package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.minecraft.report.AbuseReport;

import java.util.UUID;

public class AbuseReportRequest {
    public UUID id;
    public AbuseReport report;
    public ClientInfo clientInfo;
    public ThirdPartyServerInfo thirdPartyServerInfo;
    public RealmInfo realmInfo;

    public AbuseReportRequest(final UUID id, final AbuseReport report, final ClientInfo clientInfo, final ThirdPartyServerInfo thirdPartyServerInfo, final RealmInfo realmInfo) {
        this.id = id;
        this.report = report;
        this.clientInfo = clientInfo;
        this.thirdPartyServerInfo = thirdPartyServerInfo;
        this.realmInfo = realmInfo;
    }

    public static class ClientInfo {
        public String clientVersion;

        public ClientInfo(final String clientVersion) {
            this.clientVersion = clientVersion;
        }
    }

    public static class ThirdPartyServerInfo {
        public String address;

        public ThirdPartyServerInfo(final String address) {
            this.address = address;
        }
    }

    public static class RealmInfo {
        public String realmId;
        public int slotId;

        public RealmInfo(final String realmId, final int slotId) {
            this.realmId = realmId;
            this.slotId = slotId;
        }
    }
}
