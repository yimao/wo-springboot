package com.mudcode.springboot.common.encoder;

import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.Base64;

class PBKDF2EncoderTest {

    @Test
    void pwd() throws GeneralSecurityException {
        String pwd = "0okm9ijn*UHB";
        byte[] salt = PBKDF2Encoder.generateSalt();
        byte[] bytes = PBKDF2Encoder.encode(pwd.toCharArray(), salt);
        System.out.println("PBKDF2(base64): " + Base64.getEncoder().encodeToString(bytes));
        System.out.println("PBKDF2(hex): " + HexEncoder.toHexDigits(bytes));
    }

}
