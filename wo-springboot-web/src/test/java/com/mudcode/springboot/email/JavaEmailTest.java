package com.mudcode.springboot.email;

import com.mudcode.springboot.common.encoder.DigestUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;
import java.util.Random;

public class JavaEmailTest {

    @Test
    public void sendEmail() throws MessagingException {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        String host = "smtpdm.aliyun.com";
        int port = 465;
        String username = "yimao@mail.mudcode.com";
        String password = "64Wpzrpm5FXezrpq";

        String toAddress = "guodongxu@126.com";
        // String message = UUID.randomUUID().toString();
        byte[] bytes = new byte[8192];
        Random random = new SecureRandom();
        random.nextBytes(bytes);
        String message = Base64.getEncoder().encodeToString(bytes);

        sender.setDefaultEncoding("UTF-8");
        sender.setProtocol("smtp");
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);

        // https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
        Properties properties = new Properties();
        int timeout = 5000; // 5s
        properties.put("mail.smtp.timeout", timeout);
        properties.put("mail.smtp.writetimeout", timeout);
        properties.put("mail.smtp.connectiontimeout", timeout);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.starttls.required", true);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        sender.setJavaMailProperties(properties);

        // test debug
        sender.getSession().setDebug(true);
        sender.testConnection();

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setFrom(username);
        messageHelper.setTo(toAddress);
        messageHelper.setSubject(DigestUtil.sha256Hex(bytes));
        messageHelper.setText(message, false);

        sender.send(mimeMessage);
    }

}
