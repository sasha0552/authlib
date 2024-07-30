package com.mojang.authlib.yggdrasil.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class KeyPairResponse extends Response {
    private KeyPair keyPair;
    @Nullable
    @SerializedName("publicKeySignature")
    private ByteBuffer legacyPublicKeySignature;
    @SerializedName("publicKeySignatureV2")
    private ByteBuffer publicKeySignature;
    private String expiresAt;
    private String refreshedAfter;

    public String getPrivateKey() {
        return keyPair.privateKey;
    }

    public String getPublicKey() {
        return keyPair.publicKey;
    }

    @Nullable
    public ByteBuffer getLegacyPublicKeySignature() {
        return legacyPublicKeySignature;
    }

    public ByteBuffer getPublicKeySignature() {
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
