package com.mudcode.springboot.common.encoder;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ECCEncoder {

    private static final String ALGORITHM = "EC";

    private static final String ALGORITHM_PROVIDER = "BC";

    private static final String EC_GEN_PARAMETER_SPEC = "secp256k1";

    private static final String ALGORITHM_ECIES_SPEC = "ECIES";

    private static final String ALGORITHM_SIGN = "SHA256withECDSA";

    static {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private ECCEncoder() {
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, ALGORITHM_PROVIDER);
            keyPairGenerator.initialize(new ECGenParameterSpec(EC_GEN_PARAMETER_SPEC));
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

    }

    public static PrivateKey getPKCS8EncodedKey(byte[] encodedKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, ALGORITHM_PROVIDER);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static PublicKey getX509EncodedKey(byte[] encodedKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, ALGORITHM_PROVIDER);
            return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] encrypt(byte[] data, final PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_ECIES_SPEC, ALGORITHM_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] decrypt(byte[] data, final PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_ECIES_SPEC, ALGORITHM_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] sign(byte[] data, final PrivateKey privateKey) {
        try {
            Signature privateKeySignature = Signature.getInstance(ALGORITHM_SIGN, ALGORITHM_PROVIDER);
            privateKeySignature.initSign(privateKey);
            privateKeySignature.update(data);
            return privateKeySignature.sign();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static boolean verifySign(byte[] data, byte[] sign, final PublicKey publicKey) {
        try {
            Signature publicKeySignature = Signature.getInstance(ALGORITHM_SIGN, ALGORITHM_PROVIDER);
            publicKeySignature.initVerify(publicKey);
            publicKeySignature.update(data);
            return publicKeySignature.verify(sign);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}
