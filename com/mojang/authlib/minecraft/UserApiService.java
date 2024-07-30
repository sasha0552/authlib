package com.mojang.authlib.minecraft;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public interface UserApiService {
    enum UserFlag {
        /**
         * Is the user allowed to play on 3rd-party multiplayer servers?
         */
        SERVERS_ALLOWED,
        /**
         * Is the user allowed to play on Realms?
         */
        REALMS_ALLOWED,
        /**
         * Is the user allowed to access chat in online games?
         */
        CHAT_ALLOWED,
        /**
         * Is telemetry enabled for this user?
         */
        TELEMETRY_ENABLED,
        /**
         * Is chat profanity filter enabled for this user?
         */
        PROFANITY_FILTER_ENABLED,
    }

    record UserProperties(Set<UserFlag> flags) {
        public boolean flag(final UserFlag flag) {
            return flags.contains(flag);
        }
    }

    UserProperties OFFLINE_PROPERTIES = new UserProperties(Set.of(UserFlag.CHAT_ALLOWED, UserFlag.REALMS_ALLOWED, UserFlag.SERVERS_ALLOWED));

    UserApiService OFFLINE = new UserApiService() {
        @Override
        public UserProperties properties() {
            return OFFLINE_PROPERTIES;
        }

        @Override
        public boolean isBlockedPlayer(final UUID playerID) {
            return false;
        }

        @Override
        public void refreshBlockList() {
        }

        @Override
        public TelemetrySession newTelemetrySession(final Executor executor) {
            return TelemetrySession.DISABLED;
        }
    };

    UserProperties properties();

    /**
     * Check if a player is on the block list.
     * Note: might block
     *
     * @param playerID A valid player UUID
     * @return True if communications from the player should be blocked
     */
    boolean isBlockedPlayer(UUID playerID);

    /*
     * Fetch block list if not present or old enough.
     * Note: might block
     */
    void refreshBlockList();

    /**
     * Create fresh telemetry session.
     *
     * @param executor - executor used for sending operations
     */
    TelemetrySession newTelemetrySession(Executor executor);
}
