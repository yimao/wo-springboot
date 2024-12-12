package com.mudcode.springboot.common.encoder;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class PBKDF2Encoder {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int DEFAULT_KEY_ITERATIONS = 65537;

    private static final int DEFAULT_KEY_LENGTH = 256; // bit

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PBKDF2Encoder() {
    }

    public static byte[] encode(char[] password, byte[] salt) {
        try {
            KeySpec pbeKeySpec = new PBEKeySpec(password, salt, DEFAULT_KEY_ITERATIONS, DEFAULT_KEY_LENGTH);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            return secretKey.getEncoded();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[32];// 256 bit
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

}
