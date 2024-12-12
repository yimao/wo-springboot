package com.mudcode.springboot.controller;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@RestController
public class RandomBytesController {

    private Random random;

    @PostConstruct
    public void init() {
        this.random = new SecureRandom();
    }

    @GetMapping(value = "/randomBytes")
    public ResponseEntity<?> randomBytes() {
        byte[] bytes = new byte[32];
        this.random.nextBytes(bytes);
        return ResponseEntity.ok(new RandomBytes(bytes));
    }

    @Data
    private static class RandomBytes {

        private final byte[] bytes;

        private final String base64;

        private final String hex;

        private final String sha256;

        public RandomBytes(byte[] bytes) {
            this.bytes = bytes;
            this.base64 = Base64.getEncoder().encodeToString(bytes);
            this.hex = Hex.encodeHexString(bytes);
            this.sha256 = DigestUtils.sha256Hex(bytes);
        }

    }

}
