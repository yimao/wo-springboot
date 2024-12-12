package com.mudcode.springboot.common.encoder;

public class ByteEncoder {

    private ByteEncoder() {
    }

    public static byte[] toByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) i;
        return result;
    }

    public static byte[] toByteArray(long l) {
        byte[] result = new byte[8];
        result[0] = (byte) (l >> 56);
        result[1] = (byte) (l >> 48);
        result[2] = (byte) (l >> 40);
        result[3] = (byte) (l >> 32);
        result[4] = (byte) (l >> 24);
        result[5] = (byte) (l >> 16);
        result[6] = (byte) (l >> 8);
        result[7] = (byte) l;
        return result;
    }

    public static byte[] toByteArray(short s) {
        byte[] result = new byte[2];
        result[0] = (byte) (s >> 8);
        result[1] = (byte) s;
        return result;
    }

    public static byte[] toByteArray(char c) {
        byte[] result = new byte[2];
        result[0] = (byte) (c >> 8);
        result[1] = (byte) c;
        return result;
    }

    public static byte[] toByteArray(float f) {
        return toByteArray(Float.floatToIntBits(f));
    }

    public static byte[] toByteArray(double d) {
        return toByteArray(Double.doubleToLongBits(d));
    }

    public static byte[] toByteArray(boolean b) {
        return new byte[]{(byte) (b ? 1 : 0)};
    }

}
