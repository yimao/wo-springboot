package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptionAlgorithmImpl {

    public String getEncryptMessage(SecretKey deskey, String message)
            throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException {
        String algorithm = deskey.getAlgorithm();
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(1, deskey);
        return parseByte2HexStr(cipher.doFinal(message.getBytes()));
    }

    public String getEncryptMessage(String message) throws BadPaddingException, IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed("EF7D937B1B094FC180E40A0A84D2AC7096DFE3198BC8B90340C1605ACDAD602F".getBytes());
        generator.init(128, secureRandom);
        SecretKey keygen = generator.generateKey();
        Cipher cipher = Cipher.getInstance(keygen.getAlgorithm());
        cipher.init(1, keygen);
        return parseByte2HexStr(cipher.doFinal(message.getBytes()));
    }

    public String getDecryptMessage(SecretKey deskey, String message) throws BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        String algorithm = deskey.getAlgorithm();
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(2, deskey);
        return new String(cipher.doFinal(parseHexStr2Byte(message)));
    }

    public String getDecryptMessage(String message) throws BadPaddingException, IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed("EF7D937B1B094FC180E40A0A84D2AC7096DFE3198BC8B90340C1605ACDAD602F".getBytes());
        generator.init(128, secureRandom);
        SecretKey keygen = generator.generateKey();
        Cipher cipher = Cipher.getInstance(keygen.getAlgorithm());
        cipher.init(2, keygen);
        return new String(cipher.doFinal(parseHexStr2Byte(message)));
    }

    public String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1)
                hex = '0' + hex;
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    @Test
    public void testDecryptMessage() throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException,
            NoSuchPaddingException, NoSuchAlgorithmException {
        String message = "Hello World!";
        String encryptedMessage = getEncryptMessage(message);
        System.out.println("Encrypted Message: " + encryptedMessage);
        String decryptedMessage = getDecryptMessage(encryptedMessage);
        System.out.println("Decrypted Message: " + decryptedMessage);
    }

}
