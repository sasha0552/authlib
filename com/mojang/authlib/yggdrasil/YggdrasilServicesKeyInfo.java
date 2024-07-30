package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class YggdrasilServicesKeyInfo implements ServicesKeyInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(YggdrasilServicesKeyInfo.class);

    // Don't expose those directly to avoid library clients inlining it
    private static final int KEY_SIZE_BITS = 4096;
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    private final PublicKey publicKey;

    private YggdrasilServicesKeyInfo(final PublicKey publicKey) {
        this.publicKey = publicKey;
        final String algorithm = publicKey.getAlgorithm();
        if (!algorithm.equals(KEY_ALGORITHM)) {
            throw new IllegalArgumentException("Expected " + KEY_ALGORITHM + " key, got " + algorithm);
        }
    }

    public static ServicesKeyInfo createFromResources() {
        try {
            final byte[] keyBytes = YggdrasilServicesKeyInfo.class.getResourceAsStream("/yggdrasil_session_pubkey.der").readAllBytes();
            final X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            final KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            final PublicKey publicKey = keyFactory.generatePublic(spec);
            return new YggdrasilServicesKeyInfo(publicKey);
        } catch (final Exception e) {
            throw new AssertionError("Missing/invalid yggdrasil public key!", e);
        }
    }

    @Override
    public Signature signature() {
        try {
            final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            return signature;
        } catch (final NoSuchAlgorithmException | InvalidKeyException e) {
            throw new AssertionError("Failed to create signature", e);
        }
    }

    @Override
    public int keyBitCount() {
        return KEY_SIZE_BITS;
    }

    @Override
    public boolean validateProperty(final Property property) {
        final Signature signature = signature();
        final byte[] expected = Base64.getDecoder().decode(property.getSignature());

        try {
            signature.update(property.getValue().getBytes());
            return signature.verify(expected);
        } catch (final SignatureException e) {
            LOGGER.error("Failed to verify signature on property {}", property, e);
        }
        return false;
    }
}
