package com.mudcode.springboot.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Service
public class OSSService {

    private static final Logger logger = LoggerFactory.getLogger(OSSService.class);

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${oss.bucket}")
    private String bucket;

    @Value("${oss.basedir}")
    private String basedir;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSocketTimeout(10000);
        conf.setConnectionTimeout(10000);
        conf.setIdleConnectionTime(10000);
        conf.setProtocol(Protocol.HTTPS);

        this.ossClient = new OSSClientBuilder().build(this.endpoint, this.accessKeyId, this.accessKeySecret, conf);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    private String ofBasedir(String path) {
        if (path.startsWith("/")) {
            return this.basedir + path;
        } else {
            return this.basedir + "/" + path;
        }
    }

    public void upload(String path, byte[] content) {
        PutObjectResult result = ossClient.putObject(bucket, ofBasedir(path), new ByteArrayInputStream(content));
        logger.debug("success upload oss, path={}, content size={}, etag={}", path, content.length, result.getETag());
    }

    public byte[] download(String path) throws IOException {
        byte[] content = null;
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, ofBasedir(path));
        try (OSSObject ossObject = ossClient.getObject(getObjectRequest)) {
            content = IOUtils.toByteArray(ossObject.getObjectContent());
            logger.debug("success download oss, path={}, content size={}, etag={}", path, content.length,
                    ossObject.getObjectMetadata().getETag());
        }
        return content;
    }

    public URL generatePresignedUrl(String path) {
        Date expiration = new Date(System.currentTimeMillis() + 24 * 3600 * 1000);
        return ossClient.generatePresignedUrl(bucket, ofBasedir(path), expiration);
    }

}