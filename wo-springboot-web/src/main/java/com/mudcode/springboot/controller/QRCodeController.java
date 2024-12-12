package com.mudcode.springboot.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Controller
public class QRCodeController {

    private SecretKeySpec secretKeySpec;

    @PostConstruct
    public void init() {
        Random random = new SecureRandom();
        byte[] secretKeyBytes = new byte[32]; // 256bit
        random.nextBytes(secretKeyBytes);
        this.secretKeySpec = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    @GetMapping(value = "/qrcode.png")
    public void captcha(HttpServletResponse response) throws IOException, WriterException {
        // Set to expire far in the past.
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");

        // return a png
        response.setContentType(MediaType.IMAGE_PNG_VALUE);

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MINUTE, 10);
        Date expire = calendar.getTime();

        String token = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject("check_data")
                .setIssuer("check_data")
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(expire)
                .signWith(secretKeySpec, SignatureAlgorithm.HS256)
                .compact();

        Writer writer = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);

        int wh = 200;
        BitMatrix bitMatrix = writer.encode(token, BarcodeFormat.QR_CODE, wh, wh, hints);
        try (ServletOutputStream out = response.getOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
            out.flush();
        }
    }

}
