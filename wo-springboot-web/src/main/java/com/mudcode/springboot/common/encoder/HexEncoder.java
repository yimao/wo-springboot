package com.mudcode.springboot.common.encoder;

/**
 * @see java.util.HexFormat
 */
public class HexEncoder {

    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f'};

    private HexEncoder() {
    }

    public static String toHexDigits(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            hex[i * 2] = toHighHexDigit(b);
            hex[i * 2 + 1] = toLowHexDigit(b);
        }
        return new String(hex);
    }

    public static String toHexDigits(byte value) {
        char[] rep = new char[2];
        rep[0] = toHighHexDigit(value);
        rep[1] = toLowHexDigit(value);
        return new String(rep);
    }

    public static String toHexDigits(int value) {
        char[] rep = new char[8];
        rep[0] = toHighHexDigit((byte) (value >> 24));
        rep[1] = toLowHexDigit((byte) (value >> 24));
        rep[2] = toHighHexDigit((byte) (value >> 16));
        rep[3] = toLowHexDigit((byte) (value >> 16));
        rep[4] = toHighHexDigit((byte) (value >> 8));
        rep[5] = toLowHexDigit((byte) (value >> 8));
        rep[6] = toHighHexDigit((byte) value);
        rep[7] = toLowHexDigit((byte) value);
        return new String(rep);
    }

    public static String toHexDigits(long value) {
        char[] rep = new char[16];
        rep[0] = toHighHexDigit((byte) (value >>> 56));
        rep[1] = toLowHexDigit((byte) (value >>> 56));
        rep[2] = toHighHexDigit((byte) (value >>> 48));
        rep[3] = toLowHexDigit((byte) (value >>> 48));
        rep[4] = toHighHexDigit((byte) (value >>> 40));
        rep[5] = toLowHexDigit((byte) (value >>> 40));
        rep[6] = toHighHexDigit((byte) (value >>> 32));
        rep[7] = toLowHexDigit((byte) (value >>> 32));
        rep[8] = toHighHexDigit((byte) (value >>> 24));
        rep[9] = toLowHexDigit((byte) (value >>> 24));
        rep[10] = toHighHexDigit((byte) (value >>> 16));
        rep[11] = toLowHexDigit((byte) (value >>> 16));
        rep[12] = toHighHexDigit((byte) (value >>> 8));
        rep[13] = toLowHexDigit((byte) (value >>> 8));
        rep[14] = toHighHexDigit((byte) value);
        rep[15] = toLowHexDigit((byte) value);

        return new String(rep);
    }

    private static char toHighHexDigit(int value) {
        return HEX_CHARS[(value >> 4) & 0xf];
    }

    private static char toLowHexDigit(int value) {
        return HEX_CHARS[value & 0xf];
    }

}
