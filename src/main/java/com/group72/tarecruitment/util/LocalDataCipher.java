package com.group72.tarecruitment.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class LocalDataCipher {
    private static final String SECRET_PROPERTY = "ta.data.secret";
    private static final String SECRET_ENV = "TA_DATA_SECRET";
    private static final String FALLBACK_SECRET = "group72-sprint1-local-data-secret";
    private static final String ENCRYPTED_PREFIX = "ENC$1";
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_NONCE_BYTES = 12;
    private static final int AES_KEY_BYTES = 16;

    private LocalDataCipher() {
    }

    public static boolean isEncryptedPayload(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return false;
        }
        String text = new String(bytes, StandardCharsets.UTF_8).trim();
        return text.startsWith(ENCRYPTED_PREFIX + ":");
    }

    public static byte[] encrypt(byte[] plainBytes) {
        try {
            byte[] nonce = new byte[GCM_NONCE_BYTES];
            new SecureRandom().nextBytes(nonce);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, buildSecretKey(), new GCMParameterSpec(GCM_TAG_BITS, nonce));
            byte[] cipherBytes = cipher.doFinal(plainBytes);

            String payload = ENCRYPTED_PREFIX
                    + ":"
                    + Base64.getEncoder().encodeToString(nonce)
                    + ":"
                    + Base64.getEncoder().encodeToString(cipherBytes);
            return payload.getBytes(StandardCharsets.UTF_8);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Failed to encrypt local data file.", exception);
        }
    }

    public static byte[] decrypt(byte[] encryptedBytes) {
        try {
            String payload = new String(encryptedBytes, StandardCharsets.UTF_8).trim();
            String[] parts = payload.split(":", 3);
            if (parts.length != 3 || !ENCRYPTED_PREFIX.equals(parts[0])) {
                throw new IllegalStateException("Unsupported encrypted local data format.");
            }

            byte[] nonce = Base64.getDecoder().decode(parts[1]);
            byte[] cipherBytes = Base64.getDecoder().decode(parts[2]);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, buildSecretKey(), new GCMParameterSpec(GCM_TAG_BITS, nonce));
            return cipher.doFinal(cipherBytes);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Failed to decrypt local data file.", exception);
        }
    }

    private static SecretKeySpec buildSecretKey() {
        try {
            String configuredSecret = System.getProperty(SECRET_PROPERTY);
            if (configuredSecret == null || configuredSecret.isBlank()) {
                configuredSecret = System.getenv(SECRET_ENV);
            }
            if (configuredSecret == null || configuredSecret.isBlank()) {
                configuredSecret = FALLBACK_SECRET;
            }

            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(configuredSecret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(Arrays.copyOf(digest, AES_KEY_BYTES), "AES");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Failed to initialize local data encryption key.", exception);
        }
    }
}
