package com.mudcode.springboot.common.encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @see org.apache.commons.codec.digest.DigestUtils
 */
public class DigestUtil {

    private DigestUtil() {
    }

    public static byte[] md5(final byte[] data) {
        return getDigest("MD5").digest(data);
    }

    public static String md5Hex(final byte[] data) {
        return HexEncoder.toHexDigits(md5(data));
    }

    public static byte[] sha256(final byte[] data) {
        return getDigest("SHA-256").digest(data);
    }

    public static String sha256Hex(final byte[] data) {
        return HexEncoder.toHexDigits(sha256(data));
    }

    private static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
