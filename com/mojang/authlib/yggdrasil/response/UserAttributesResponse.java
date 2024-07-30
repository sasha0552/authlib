package com.mojang.authlib.yggdrasil.response;

import javax.annotation.Nullable;

public class UserAttributesResponse extends Response {
    @Nullable
    private Privileges privileges;

    @Nullable
    private ProfanityFilterPreferences profanityFilterPreferences;

    @Nullable
    public Privileges getPrivileges() {
        return privileges;
    }

    @Nullable
    public ProfanityFilterPreferences getProfanityFilterPreferences() {
        return profanityFilterPreferences;
    }

    public static class Privileges {
        @Nullable
        private Privilege onlineChat;
        @Nullable
        private Privilege multiplayerServer;
        @Nullable
        private Privilege multiplayerRealms;
        @Nullable
        private Privilege telemetry;

        public boolean getOnlineChat() {
            return onlineChat != null && onlineChat.enabled;
        }

        public boolean getMultiplayerServer() {
            return multiplayerServer != null && multiplayerServer.enabled;
        }

        public boolean getMultiplayerRealms() {
            return multiplayerRealms != null && multiplayerRealms.enabled;
        }

        public boolean getTelemetry() {
            return telemetry != null && telemetry.enabled;
        }

        public class Privilege {
            private boolean enabled;
        }
    }

    public static class ProfanityFilterPreferences {
        private boolean profanityFilterOn;

        public boolean isEnabled() {
            return profanityFilterOn;
        }
    }
}
