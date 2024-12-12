package com.mudcode.springboot.minio;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
public class MinIOService {

    private static final Logger logger = LoggerFactory.getLogger(MinIOService.class);

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKeyId}")
    private String accessKeyId;

    @Value("${minio.accessKeySecret}")
    private String accessKeySecret;

    @Value("${minio.bucket}")
    private String bucket;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                .endpoint(this.endpoint)
                .credentials(this.accessKeyId, this.accessKeySecret)
                .build();
    }

    @PreDestroy
    public void destroy() {
    }

    private String ofBasedir(String path) {
        return path;
    }

    public void upload(String path, byte[] content) throws IOException {
        try {
            ObjectWriteResponse objectWriteResponse = this.minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .stream(new ByteArrayInputStream(content), content.length, -1)
                    .object(ofBasedir(path))
                    .build());
            logger.debug("success upload minio, path={}, content size={}, etag={}", path, content.length,
                    objectWriteResponse.etag());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public byte[] download(String path) throws IOException {
        try {
            GetObjectResponse getObjectResponse = this.minioClient
                    .getObject(GetObjectArgs.builder().bucket(bucket).object(ofBasedir(path)).build());
            byte[] content = getObjectResponse.readAllBytes();
            logger.debug("success download minio, path={}, content size={}", path, content.length);
            return content;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public URL generatePresignedUrl(String path) throws IOException {
        try {
            return new URL(this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .object(ofBasedir(path))
                    .expiry(24, TimeUnit.HOURS)
                    .method(Method.GET)
                    .build()));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
