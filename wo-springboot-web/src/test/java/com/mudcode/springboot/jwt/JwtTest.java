package com.mudcode.springboot.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class JwtTest {

    private Map<String, String> keyMap;
    private String secretKeyId;

    @BeforeEach
    public void before() {
        this.keyMap = new HashMap<>();
        byte[] secretKeyBytes = new byte[32];
        Random random = new SecureRandom();
        random.nextBytes(secretKeyBytes);

        String secretKey = Base64.getEncoder().encodeToString(secretKeyBytes);
        // secretKey = "onbZm5vXwhyo8wpz6IGjWjshsQvjFAG+676S9KSKy+0=";
        System.out.println(secretKey);

        secretKeyId = "kid-01";
        keyMap.put(secretKeyId, secretKey);
    }

    @Test
    public void token() {
        Instant instant = Instant.now();
        Date now = Date.from(instant);
        Date expireDate = Date.from(instant.plus(10, ChronoUnit.SECONDS));

        String secretKeyEncoded = keyMap.get(secretKeyId);
        SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyEncoded));

        String token = Jwts.builder()
                .header()
                .keyId("kid-01") // 用于服务器端定位到关联的密钥对
                .and()
                .id(UUID.randomUUID().toString()) // 更多用于防重放攻击判断
                .subject("hello-world-p01")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();

        // token = "eyJraWQiOiJraWQtMDEiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0NmRhMzJmNy05Y2Y2LTRiZjUtOTQ5MS00MDFhZmM4OTYyNzciLCJzdWIiOiJoZWxsby13b3JsZC1wMDEiLCJpYXQiOjE3MzE0NzcwNjAsImV4cCI6MTczMTQ3NzA3MH0.LM1zQn0Dj3_CwzFOia1bdgE63BC8_KCMaLQcSoRE6gY";
        System.out.println(token);

        // JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        JwtParser jwtParser = Jwts.parser()
                .keyLocator(new GlobalLocatorAdapter(keyMap))
                .build();

        // 验证签名后解析内容
        Jws<Claims> signedClaims = jwtParser.parseSignedClaims(token);
        System.out.println(signedClaims.toString());
    }

    static class GlobalLocatorAdapter extends LocatorAdapter<Key> {
        private final Map<String, String> keyMap;

        GlobalLocatorAdapter(Map<String, String> keyMap) {
            this.keyMap = keyMap;
        }

        @Override
        protected Key locate(ProtectedHeader header) {
            String keyId = header.getKeyId();
            String secretKeyEncoded = keyMap.get(keyId);
            if (secretKeyEncoded == null) {
                throw new IllegalArgumentException("keyId not found");
            }
            byte[] secretKeyBytes = Base64.getDecoder().decode(secretKeyEncoded);
            return Keys.hmacShaKeyFor(secretKeyBytes);
        }
    }
}
