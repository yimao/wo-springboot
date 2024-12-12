package com.mudcode.springboot.common.encoder;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

class RSAEncoderTest {

    private static final Logger logger = LoggerFactory.getLogger(RSAEncoderTest.class);

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private byte[] rawDataBytes;

    @BeforeEach
    void before() throws Exception {
        KeyPair keyPair = RSAEncoder.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        logger.info("private key: \n{}", Base64.getMimeEncoder().encodeToString(privateKey.getEncoded()));
        logger.info("public key: \n{}", Base64.getMimeEncoder().encodeToString(publicKey.getEncoded()));

        SecureRandom secureRandom = new SecureRandom();
        rawDataBytes = new byte[8192]; // 8K
        secureRandom.nextBytes(rawDataBytes);
    }

    @Test
    void encrypt() throws Exception {
        byte[] encodeBytes = RSAEncoder.encrypt(rawDataBytes, publicKey);
        byte[] check = RSAEncoder.decrypt(encodeBytes, privateKey);
        Assertions.assertArrayEquals(rawDataBytes, check);

        logger.info("\n{}", Base64.getMimeEncoder().encodeToString(encodeBytes));
    }

    @Test
    void sign() throws Exception {
        byte[] sign = RSAEncoder.sign(rawDataBytes, privateKey);
        Assertions.assertTrue(RSAEncoder.verifySign(rawDataBytes, sign, publicKey));
        logger.info(Hex.encodeHexString(sign));
    }

    @Test
    void rawRsaTest() throws Exception {
        // 可支持的最大数据大小：KEY_SIZE / 8 - 11 = 245 bytes (- 11 bytes if you have padding)
        int maxSourceDataSize = RSAEncoder.KEY_SIZE / 8 - 11;

        byte[] bytes = new byte[maxSourceDataSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        Cipher encode = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encode.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] rsaEncryptBytes = encode.doFinal(bytes);
        logger.info("RSA encode({}): {}", rsaEncryptBytes.length,
                Base64.getMimeEncoder().encodeToString(rsaEncryptBytes));

        Cipher decode = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decode.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] rsaDecryptBytes = decode.doFinal(rsaEncryptBytes);
        Assertions.assertArrayEquals(bytes, rsaDecryptBytes);
    }

    @Test
    void rawRsaTest2() throws Exception {
        // 可支持的最大数据大小：KEY_SIZE / 8 - 11 = 245 bytes (- 11 bytes if you have padding)
        int maxSourceDataSize = RSAEncoder.KEY_SIZE / 8 - 11;

        byte[] bytes = new byte[maxSourceDataSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        Cipher encode = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encode.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] rsaEncryptBytes = encode.doFinal(bytes);
        logger.info("RSA encode({}): {}", rsaEncryptBytes.length,
                Base64.getMimeEncoder().encodeToString(rsaEncryptBytes));

        Cipher decode = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decode.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] rsaDecryptBytes = decode.doFinal(rsaEncryptBytes);
        Assertions.assertArrayEquals(bytes, rsaDecryptBytes);
    }

}
