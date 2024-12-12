package com.mudcode.springboot.common.encoder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

class AESEncoderTest {

    @Test
    void testAes() throws Exception {
        Random random = new SecureRandom();
        byte[] rawDataBytes = new byte[8192];
        random.nextBytes(rawDataBytes);

        byte[] secretKeyBytes = AESEncoder.generateSecretKey();
        System.out.println(HexEncoder.toHexDigits(secretKeyBytes));

        byte[] encodeBytes = AESEncoder.encrypt(secretKeyBytes, rawDataBytes);
        System.out.println(Base64.getMimeEncoder().encodeToString(encodeBytes));

        byte[] decodeBytes = AESEncoder.decrypt(secretKeyBytes, encodeBytes);
        Assertions.assertArrayEquals(rawDataBytes, decodeBytes);
    }

}
