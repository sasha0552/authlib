package com.mojang.authlib.minecraft;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;

import javax.annotation.Nullable;
import java.util.Map;
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

    record UserProperties(Set<UserFlag> flags, Map<String, BanDetails> bannedScopes) {
        public boolean flag(final UserFlag flag) {
            return flags.contains(flag);
        }
    }

    UserProperties OFFLINE_PROPERTIES = new UserProperties(Set.of(UserFlag.CHAT_ALLOWED, UserFlag.REALMS_ALLOWED, UserFlag.SERVERS_ALLOWED), Map.of());

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

        @Override
        @Nullable
        public KeyPairResponse getKeyPair() {
            return null;
        }

        @Override
        public void reportAbuse(final AbuseReportRequest request) {
        }

        @Override
        public boolean canSendReports() {
            return false;
        }

        @Override
        public AbuseReportLimits getAbuseReportLimits() {
            return AbuseReportLimits.DEFAULTS;
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

    @Nullable
    KeyPairResponse getKeyPair();

    void reportAbuse(AbuseReportRequest request);

    boolean canSendReports();

    AbuseReportLimits getAbuseReportLimits();
}
