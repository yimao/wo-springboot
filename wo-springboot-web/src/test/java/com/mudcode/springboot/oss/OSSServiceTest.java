package com.mudcode.springboot.oss;

import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.common.encoder.HexEncoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

class OSSServiceTest extends ApplicationTest {

    @Autowired
    private OSSService ossService;

    @Test
    void upload() throws IOException {
        byte[] content = new byte[8192];
        Random random = new Random();
        random.nextBytes(content);

        byte[] sha256 = DigestUtils.sha256(content);
        String sha256hex = HexEncoder.toHexDigits(sha256);
        String path = HexEncoder.toHexDigits(sha256[0]) + "/" + sha256hex + ".bin";
        ossService.upload(path, content);
        byte[] check = ossService.download(path);
        Assertions.assertArrayEquals(content, check);
    }

    @Test
    void batch() throws IOException {
        long t0 = System.currentTimeMillis();
        int i = 2000;
        while (i > 0) {
            upload();
            i--;
        }
        long t1 = System.currentTimeMillis();
        logger.info("cost: {}ms", t1 - t0);
    }

    @Test
    void preSignURL() throws IOException {
        String path = "static/favicon.ico";
        try (InputStream is = this.getClass().getResourceAsStream("/static/favicon.ico")) {
            byte[] content = Objects.requireNonNull(is).readAllBytes();
            ossService.upload(path, content);
        }
        URL url = this.ossService.generatePresignedUrl(path);
        logger.info(url.toString());
    }

    @Test
    void uploadFile() throws Throwable {
        String path = "static/robots.txt";
        URL url = this.getClass().getResource("/static/robots.txt");
        File file = new File(Objects.requireNonNull(url).getFile());
        ossService.upload(path, file);
    }
}
