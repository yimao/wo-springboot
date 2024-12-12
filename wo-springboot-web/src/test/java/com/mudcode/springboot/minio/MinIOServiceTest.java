package com.mudcode.springboot.minio;

import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.common.encoder.HexEncoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

class MinIOServiceTest extends ApplicationTest {

    @Autowired
    private MinIOService minIOService;

    @Test
    void upload() throws IOException {
        byte[] content = new byte[8192];
        Random random = new Random();
        random.nextBytes(content);

        byte[] sha256 = DigestUtils.sha256(content);
        String sha256hex = HexEncoder.toHexDigits(sha256);
        String path = HexEncoder.toHexDigits(sha256[0]) + "/" + sha256hex + ".bin";
        minIOService.upload(path, content);
        byte[] check = minIOService.download(path);
        Assertions.assertArrayEquals(content, check);
    }

    @Test
    void preSignURL() throws IOException {
        String path = "static/favicon.ico";
        try (InputStream is = this.getClass().getResourceAsStream("/static/favicon.ico")) {
            byte[] content = Objects.requireNonNull(is).readAllBytes();
            minIOService.upload(path, content);
        }
        URL url = this.minIOService.generatePresignedUrl(path);
        logger.info(url.toString());
    }

}
