package com.mudcode.springboot.common.encoder;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncoder {

    // 可支持的最大数据大小：KEY_SIZE / 8 - 11 = 501 bytes (- 11 bytes if you have padding)
    public static final int KEY_SIZE = 4096;

    private static final String ALGORITHM_RSA = "RSA";

    private static final String ALGORITHM_RSA_SPEC = "RSA/ECB/PKCS1Padding";

    private static final String ALGORITHM_RSA_SIGN = "SHA256withRSA";

    static {
        // openssl generate PEM
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private RSAEncoder() {
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static PrivateKey getPKCS8EncodedKey(byte[] encodedKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static PublicKey getX509EncodedKey(byte[] encodedKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] encrypt(byte[] data, final PublicKey publicKey) {
        try {
            byte[] secretRandomBytes = AESEncoder.generateSecretKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA_SPEC);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] rsaEncryptBytes = cipher.doFinal(secretRandomBytes);
            byte[] aesEncryptBytes = AESEncoder.encrypt(secretRandomBytes, data);

            ByteBuffer byteBuffer = ByteBuffer
                    .allocate(Integer.BYTES + rsaEncryptBytes.length + aesEncryptBytes.length);
            byteBuffer.putInt(rsaEncryptBytes.length);
            byteBuffer.put(rsaEncryptBytes);
            byteBuffer.put(aesEncryptBytes);
            return byteBuffer.array();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] decrypt(byte[] data, final PrivateKey privateKey) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        int length = byteBuffer.getInt();
        byte[] rsaEncryptBytes = new byte[length];
        byteBuffer.get(rsaEncryptBytes);

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA_SPEC);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] secretRandomBytes = cipher.doFinal(rsaEncryptBytes);
            byte[] aesEncryptBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(aesEncryptBytes);

            return AESEncoder.decrypt(secretRandomBytes, aesEncryptBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] sign(byte[] data, final PrivateKey privateKey) {
        try {
            Signature privateKeySignature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            privateKeySignature.initSign(privateKey);
            privateKeySignature.update(data);
            return privateKeySignature.sign();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static boolean verifySign(byte[] data, byte[] sign, final PublicKey publicKey) {
        try {
            Signature publicKeySignature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            publicKeySignature.initVerify(publicKey);
            publicKeySignature.update(data);
            return publicKeySignature.verify(sign);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}
