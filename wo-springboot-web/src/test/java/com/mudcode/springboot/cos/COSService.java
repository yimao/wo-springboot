package com.mudcode.springboot.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.Md5Utils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
public class COSService {

    private static final Logger logger = LoggerFactory.getLogger(COSService.class);

    @Value("${cos.region}")
    private String region; // ap-beijing

    @Value("${cos.accessKeyId}")
    private String accessKeyId;

    @Value("${cos.accessKeySecret}")
    private String accessKeySecret;

    @Value("${cos.bucket}")
    private String bucket; // mudcode-1327595278

    @Value("${cos.basedir}")
    private String basedir;

    private COSClient cosclient;

    @PostConstruct
    public void init() {
        COSCredentials cred = new BasicCOSCredentials(accessKeyId, accessKeySecret);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        this.cosclient = new COSClient(cred, clientConfig);
    }

    @PreDestroy
    public void destroy() {
        if (this.cosclient != null) {
            this.cosclient.shutdown();
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
        String contentMd5 = Md5Utils.md5AsBase64(content);

        InputStream contentInput = new ByteArrayInputStream(content);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        metadata.setContentMD5(contentMd5);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, ofBasedir(path), contentInput, metadata);
        PutObjectResult result = this.cosclient.putObject(putObjectRequest);
        logger.debug("success upload oss, path={}, content size={}, etag={}", path, content.length, result.getETag());
    }

    public byte[] download(String path) throws IOException {
        byte[] content = null;
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, ofBasedir(path));
        try (COSObject cosObject = this.cosclient.getObject(getObjectRequest)) {
            content = IOUtils.toByteArray(cosObject.getObjectContent());
            logger.debug("success download cos, path={}, content size={}, etag={}", path, content.length,
                    cosObject.getObjectMetadata().getETag());
        }
        return content;
    }

    public URL generatePresignedUrl(String path) {
        Date expiration = new Date(System.currentTimeMillis() + 24 * 3600 * 1000);
        return this.cosclient.generatePresignedUrl(bucket, ofBasedir(path), expiration);
    }
}
