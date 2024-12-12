package com.mudcode.springboot.common.encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * @see com.google.crypto.tink.subtle.Hkdf
 */
public class HKDFEncoder {

    private static final String macAlgorithm = "HmacSha256";

    private HKDFEncoder() {
    }

    public static byte[] computeHkdfWithHmacSha256(final byte[] password, final byte[] salt, final byte[] info,
                                                   final int keySizeInBytes) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(macAlgorithm);
        if (keySizeInBytes > 255 * mac.getMacLength()) {
            throw new GeneralSecurityException("size too large");
        }
        if (salt == null || salt.length == 0) {
            // According to RFC 5869, Section 2.2 the salt is optional. If no salt is
            // provided
            // then HKDF uses a salt that is an array of zeros of the same length as the
            // hash digest.
            mac.init(new SecretKeySpec(new byte[mac.getMacLength()], macAlgorithm));
        } else {
            mac.init(new SecretKeySpec(salt, macAlgorithm));
        }
        byte[] prk = mac.doFinal(password);
        byte[] result = new byte[keySizeInBytes];
        int ctr = 1;
        int pos = 0;
        mac.init(new SecretKeySpec(prk, macAlgorithm));
        byte[] digest = new byte[0];
        while (true) {
            mac.update(digest);
            mac.update(info);
            mac.update((byte) ctr);
            digest = mac.doFinal();
            if (pos + digest.length < keySizeInBytes) {
                System.arraycopy(digest, 0, result, pos, digest.length);
                pos += digest.length;
                ctr++;
            } else {
                System.arraycopy(digest, 0, result, pos, keySizeInBytes - pos);
                break;
            }
        }
        return result;
    }

}
