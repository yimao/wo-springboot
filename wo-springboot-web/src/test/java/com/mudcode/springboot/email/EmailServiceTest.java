package com.mudcode.springboot.email;

import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.common.encoder.DigestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class EmailServiceTest extends ApplicationTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void send() throws Exception {
        // String message = UUID.randomUUID().toString();
        byte[] bytes = new byte[8192];
        Random random = new SecureRandom();
        random.nextBytes(bytes);
        String message = Base64.getEncoder().encodeToString(bytes);
        emailService.send("guodongxu@126.com", DigestUtil.sha256Hex(bytes), message);
    }

}
