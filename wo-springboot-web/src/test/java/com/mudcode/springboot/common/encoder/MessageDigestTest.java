package com.mudcode.springboot.common.encoder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class MessageDigestTest {

    private final String TEST_DATA = "维基百科，自由的百科全书";

    @Test
    void testMd5() {
        String md5Hex = DigestUtils.md5Hex(TEST_DATA);
        String md5Hex2 = DigestUtil.md5Hex(TEST_DATA.getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(md5Hex, md5Hex2);
        System.out.println(md5Hex);
    }

    @Test
    void testSha256() {
        String sha256Hex = DigestUtils.sha256Hex(TEST_DATA);
        String sha256Hex2 = DigestUtil.sha256Hex(TEST_DATA.getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(sha256Hex, sha256Hex2);
        System.out.println(sha256Hex);
    }

    @Test
    void testHMacSha256() {
        String salt = "HelloWorld";
        String hMacSha256Hex = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, salt).hmacHex(TEST_DATA);
        System.out.println(hMacSha256Hex);
    }

}
