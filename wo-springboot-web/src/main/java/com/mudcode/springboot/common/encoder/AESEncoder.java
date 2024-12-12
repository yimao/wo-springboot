package com.mudcode.springboot.common.encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class AESEncoder {

    private static final String ALGORITHM_AES = "AES";

    private static final String ALGORITHM_AES_SPEC = "AES/GCM/NoPadding";

    private static final int TAG_LENGTH_BIT = 128; // 128 bit auth tag length

    private static final int IV_LENGTH_BYTE = 12;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private AESEncoder() {
    }

    public static byte[] encrypt(byte[] secretKey, byte[] rawData) {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SECURE_RANDOM.nextBytes(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, ALGORITHM_AES);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        try {
            Cipher encrypt = Cipher.getInstance(ALGORITHM_AES_SPEC);
            encrypt.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
            byte[] encodeBytes = encrypt.doFinal(rawData);

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encodeBytes.length);
            byteBuffer.put(iv);
            byteBuffer.put(encodeBytes);
            return byteBuffer.array();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] decrypt(byte[] secretKey, byte[] encryptedData) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);
        byte[] encrypted = new byte[byteBuffer.remaining()];
        byteBuffer.get(encrypted);

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, ALGORITHM_AES);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        try {
            Cipher decrypt = Cipher.getInstance(ALGORITHM_AES_SPEC);
            decrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
            return decrypt.doFinal(encrypted);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 32 bytes -> 256 bits
     */
    public static byte[] generateSecretKey() {
        byte[] salt = new byte[32]; // 256 bit
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

}
