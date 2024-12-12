package com.mudcode.springboot.common.encoder;

import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

class AnyEncoderTest {

    @Test
    void encodeTypeTest() {
        byte[] bytes = new byte[1536];
        Random random = new Random();
        random.nextBytes(bytes);

        System.out.println("BCD:" + BinaryCodec.toAsciiString(bytes));
        System.out.println("Hex:" + Hex.encodeHexString(bytes));
        System.out.println("Hex(HexEncoder):" + HexEncoder.toHexDigits(bytes));
        System.out.println("Base64(JDK):" + Base64.getEncoder().encodeToString(bytes));
        System.out.println("Base64(JDK-URLSafe):" + Base64.getUrlEncoder().encodeToString(bytes));
    }

    @Test
    void emptyByteArrayTest() {
        byte[] bytes = new byte[]{0};
        System.out.println(HexEncoder.toHexDigits(bytes));
        System.out.println(Base64.getEncoder().encodeToString(bytes));
        System.out.println(Base64.getEncoder().withoutPadding().encodeToString(bytes));
    }

    @Test
    void base64Test() {
        String str = "NBS_LICENSE_SALT#CZBank";
        String base64 = Base64.getEncoder().withoutPadding().encodeToString(str.getBytes(StandardCharsets.UTF_8));
        System.out.println(base64);
        System.out.println(new String(Base64.getDecoder().decode(base64)));
    }

    @Test
    void randomTest() {
        int count = 12;
        System.out.println("randomInt(): " + RandomUtils.secure().randomInt());
        System.out.println("nextNumeric(count): " + RandomStringUtils.secure().nextNumeric(count));
        System.out.println("nextAlphabetic(count): " + RandomStringUtils.secure().nextAlphabetic(count));
        System.out.println("nextAlphanumeric(count): " + RandomStringUtils.secure().nextAlphanumeric(count));
    }

}
