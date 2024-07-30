package com.mojang.authlib.minecraft;

import java.util.UUID;
import java.util.concurrent.Executor;

public interface UserApiService {
    UserApiService OFFLINE = new UserApiService() {
        @Override
        public boolean serversAllowed() {
            return true;
        }

        @Override
        public boolean realmsAllowed() {
            return true;
        }

        @Override
        public boolean chatAllowed() {
            return true;
        }

        @Override
        public boolean telemetryAllowed() {
            return false;
        }

        @Override
        public boolean isBlockedPlayer(final UUID playerID) {
            return false;
        }

        @Override
        public TelemetrySession newTelemetrySession(final Executor executor) {
            return TelemetrySession.DISABLED;
        }
    };

    /**
     * Checks if the user is allowed to play on multiplayer servers.
     *
     * @return True if the user is allowed to play on multiplayer servers
     */
    boolean serversAllowed();

    /**
     * Checks if the user is allowed to play on realms.
     *
     * @return True if the user is allowed to play on realms
     */
    boolean realmsAllowed();

    /**
     * Checks if the user is allowed to access chat in online games.
     *
     * @return True if the user is allowed to chat
     */
    boolean chatAllowed();

    /**
     * Check if the user is allowed to send telemetry.
     *
     * @return True if the user is allowed to send telemetry
     */
    boolean telemetryAllowed();

    /**
     * Check if a player is on the block list.
     *
     * @param playerID A valid player UUID
     * @return True if communications from the player should be blocked
     */
    boolean isBlockedPlayer(UUID playerID);

    /**
     * Create fresh telemetry session.
     *
     * @param executor - executor used for sending operations
     */
    TelemetrySession newTelemetrySession(Executor executor);
}
