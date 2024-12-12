package com.mudcode.springboot.common.encoder;

import com.google.crypto.tink.subtle.Hkdf;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

class HKDFEncoderTest {

    @Test
    void computeHkdfWithHmacSha256() throws GeneralSecurityException {
        Random random = new Random();

        byte[] pwd = new byte[32];
        random.nextBytes(pwd);

        byte[] salt = new byte[32];
        random.nextBytes(salt);

        byte[] r1 = HKDFEncoder.computeHkdfWithHmacSha256(pwd, salt, null, 32);
        System.out.println(Base64.getMimeEncoder().encodeToString(r1));

        byte[] r2 = Hkdf.computeHkdf("HmacSha256", pwd, salt, null, 32);

        byte[] r3 = new byte[32];
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA256Digest());
        HKDFParameters params = new HKDFParameters(pwd, salt, null);
        hkdf.init(params);
        hkdf.generateBytes(r3, 0, r3.length);

        Assertions.assertTrue(Arrays.equals(r1, r2) && Arrays.equals(r1, r3));
    }

}
