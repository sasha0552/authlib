package com.mojang.authlib.minecraft;

import java.util.function.Consumer;

public interface TelemetrySession {
    TelemetrySession DISABLED = new TelemetrySession() {
        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public TelemetryPropertyContainer globalProperties() {
            return TelemetryEvent.EMPTY;
        }

        @Override
        public void eventSetupFunction(final Consumer<TelemetryPropertyContainer> event) {
        }

        @Override
        public TelemetryEvent createNewEvent(final String type) {
            return TelemetryEvent.EMPTY;
        }
    };

    boolean isEnabled();

    TelemetryPropertyContainer globalProperties();

    void eventSetupFunction(final Consumer<TelemetryPropertyContainer> eventSetupFunction);

    TelemetryEvent createNewEvent(String type);
}
