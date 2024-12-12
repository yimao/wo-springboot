package com.mudcode.springboot.common.encoder;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BCryptPasswordEncoderTest {

    @Test
    void pwd() {
        String pwd = "0okm9ijn*UHB";
        // htpasswd: $2y
        BCryptPasswordEncoder encoder2y = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 10);
        System.out.println(encoder2y.encode(pwd));
    }

    @Test
    void check() {
        String pwd = "$2y$05$dJt.w2k6Rw5ZLTRn6tZHR.7Xkgi1TEYFdMrpeh3sdYfC99OGDRAHG";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.matches("0okm9ijn*UHB", pwd));
    }

}
