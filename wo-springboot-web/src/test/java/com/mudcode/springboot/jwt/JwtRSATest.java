package com.mudcode.springboot.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class JwtRSATest {

    static {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void token() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");

        String priKeyPem = Files
                .readString(Paths.get(Objects.requireNonNull(this.getClass().getResource("/cert/ctlcode.key")).toURI()));
        priKeyPem = priKeyPem.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(priKeyPem)));

        String pubKeyPem = Files
                .readString(Paths.get(Objects.requireNonNull(this.getClass().getResource("/cert/ctlcode.pem")).toURI()));
        pubKeyPem = pubKeyPem.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(pubKeyPem)));

        Instant instant = Instant.now();
        Date now = Date.from(instant);
        Date expireDate = Date.from(instant.plus(2, ChronoUnit.HOURS));

        String token = Jwts.builder()
                .header()
                .keyId("kid-01") // 用于服务器端定位到关联的密钥对
                .and()
                .id(UUID.randomUUID().toString()) // 更多用于防重放攻击判断
                .subject("hello-world-p01")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(privateKey)
                .compact();

        System.out.println(token);

        JwtParser jwtParser = Jwts.parser().verifyWith(publicKey).build();

        // 验证签名后解析内容
        Jws<Claims> signedClaims = jwtParser.parseSignedClaims(token);
        System.out.println(signedClaims.toString());
    }

}
