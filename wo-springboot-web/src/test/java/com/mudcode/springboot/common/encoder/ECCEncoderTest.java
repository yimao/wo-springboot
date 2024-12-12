package com.mudcode.springboot.common.encoder;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

class ECCEncoderTest {

    private static final Logger logger = LoggerFactory.getLogger(ECCEncoderTest.class);

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private byte[] rawDataBytes;

    @BeforeEach
    void before() throws Exception {
        KeyPair keyPair = ECCEncoder.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        logger.info("private key: \n{}", Base64.getMimeEncoder().encodeToString(privateKey.getEncoded()));
        logger.info("key: \n{}", Base64.getMimeEncoder().encodeToString(publicKey.getEncoded()));

        SecureRandom secureRandom = new SecureRandom();
        rawDataBytes = new byte[8192]; // 8k
        secureRandom.nextBytes(rawDataBytes);
    }

    @Test
    void encrypt() throws Exception {
        byte[] encodeBytes = ECCEncoder.encrypt(rawDataBytes, publicKey);
        byte[] check = ECCEncoder.decrypt(encodeBytes, privateKey);
        Assertions.assertArrayEquals(rawDataBytes, check);

        logger.info("encode({}): \n{}", encodeBytes.length, Base64.getMimeEncoder().encodeToString(encodeBytes));
    }

    @Test
    void sign() throws Exception {
        byte[] sign = ECCEncoder.sign(rawDataBytes, privateKey);
        boolean check = ECCEncoder.verifySign(rawDataBytes, sign, publicKey);
        Assertions.assertTrue(check);

        logger.info("sign({}): {}", sign.length, Hex.encodeHexString(sign));
    }

}
