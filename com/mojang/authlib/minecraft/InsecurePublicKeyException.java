package com.mojang.authlib.minecraft;

public class InsecurePublicKeyException extends RuntimeException {
    public InsecurePublicKeyException(final String message) {
        super(message);
    }

    public static class MissingException extends InsecurePublicKeyException {
        public MissingException() {
            super("No public key information found");
        }
    }

    public static class InvalidException extends InsecurePublicKeyException {
        public InvalidException(final String message) {
            super(message);
        }
    }
}
