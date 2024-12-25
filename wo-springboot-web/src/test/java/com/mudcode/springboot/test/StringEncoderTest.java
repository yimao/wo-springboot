package com.mudcode.springboot.test;

import com.google.common.hash.Hashing;
import com.mudcode.springboot.common.encoder.HexEncoder;
import org.apache.commons.codec.digest.MurmurHash2;
import org.apache.commons.codec.digest.MurmurHash3;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Random;
import java.util.zip.CRC32;

public class StringEncoderTest {

    @Test
    public void testToString() {
        String str = "Hello, World!";
        System.out.println("str length: " + str.length());
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        BigInteger b1 = new BigInteger(1, bytes);
        System.out.println("bi toString(10):" + b1);
        System.out.println("bi toString(2):" + b1.toString(Character.MIN_RADIX)); // 0-1
        System.out.println("bi toString(36):" + b1.toString(Character.MAX_RADIX)); // 0-9,a-z
        System.out.println("bi toString(16):" + b1.toString(16)); // 0-9,a-f
        System.out.println("bi hexCode:" + HexEncoder.toHexDigits(bytes));
        System.out.println("bi base64:" + Base64.getEncoder().encodeToString(bytes));
    }

    @Test
    public void testDecoder() {
        String str = "C02CW166ML7L";
        BigInteger bigInteger = new BigInteger(str, 36);
        System.out.println("bi toString(10):" + bigInteger);
        System.out.println("bi toString(36):" + bigInteger.toString(Character.MAX_RADIX)); // 0-9,a-z
    }

    @Test
    public void testUID() {
        long max = Long.MIN_VALUE;
        System.out.println(max);
        System.out.println(Long.toString(max, 36));
        System.out.println(Long.toUnsignedString(max, 36));
    }

    @Test
    public void testToHexCode() {
        int i = 127;
        byte k = (byte) i;
        System.out.println(k);
        System.out.println(Long.toHexString(k));
        System.out.println(HexFormat.of().toHexDigits(k));
        System.out.println(HexEncoder.toHexDigits(k));
    }

    @Test
    public void testNumberFormat() {
        double d = 0.123456789;

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(2);
        System.out.println(percentFormat.format(d));

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(4);
        System.out.println(numberFormat.format(d));

        DecimalFormat df = new DecimalFormat("#.####");
        System.out.println(df.format(d));
    }

    @Test
    public void testCRC32() {
        CRC32 crc = new CRC32();
        crc.update(ByteBuffer.wrap("Hello, World!".getBytes(StandardCharsets.UTF_8)));
        System.out.println(crc.getValue());
    }

    @Test
    public void testMurmurHash() {
        byte[] bytes = new byte[1024];
        Random random = new Random();
        random.nextBytes(bytes);

        // MurmurHash2
        long hash64 = MurmurHash2.hash64(bytes, bytes.length);
        System.out.println("MurmurHash2.hash64(): " + hash64);

        // MurmurHash3
        long[] hash128 = MurmurHash3.hash128x64(bytes);
        System.out.println("MurmurHash3.hash128x64(): " + Arrays.toString(hash128));

        // google guava hashing hash128
        System.out.println("Hashing.murmur3_128(): " + Hashing.murmur3_128().hashBytes(bytes).asLong());
    }

}
