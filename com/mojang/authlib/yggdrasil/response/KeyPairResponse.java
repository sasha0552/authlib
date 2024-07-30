package com.mojang.authlib.yggdrasil.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

public class KeyPairResponse extends Response {
    private KeyPair keyPair;
    @Nullable
    @SerializedName("publicKeySignature")
    private String legacyPublicKeySignature;
    @SerializedName("publicKeySignatureV2")
    private String publicKeySignature;
    private String expiresAt;
    private String refreshedAfter;

    public String getPrivateKey() {
        return keyPair.privateKey;
    }

    public String getPublicKey() {
        return keyPair.publicKey;
    }

    @Nullable
    public String getLegacyPublicKeySignature() {
        return legacyPublicKeySignature;
    }

    public String getPublicKeySignature() {
        return publicKeySignature;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public String getRefreshedAfter() {
        return refreshedAfter;
    }

    private static final class KeyPair {
        private String privateKey;
        private String publicKey;
    }
}
