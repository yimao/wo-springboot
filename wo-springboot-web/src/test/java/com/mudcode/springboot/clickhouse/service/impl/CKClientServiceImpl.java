package com.mudcode.springboot.clickhouse.service.impl;

import com.mudcode.springboot.clickhouse.CKProperties;
import com.mudcode.springboot.clickhouse.CKResp;
import com.mudcode.springboot.clickhouse.service.CKClientService;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CKClientServiceImpl implements CKClientService {

    private static final Logger logger = LoggerFactory.getLogger(CKClientServiceImpl.class);

    private static final Logger sqlErrorLogger = LoggerFactory.getLogger("ck_sql_error");

    private static final Logger executeRequestLogger = LoggerFactory.getLogger("ck_execute_request");

    @Autowired
    private CKProperties clickhouseProperties;

    private List<URI> endpointURIs;

    private CloseableHttpClient httpClient;

    private AtomicInteger roundRobinOffset;

    @PostConstruct
    public void init() throws Exception {
        this.httpClient = create();

        endpointURIs = new ArrayList<>();
        clickhouseProperties.getEndpoints()
                .stream()
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEachOrdered(endpoint -> {
                    URI uri = URI.create(endpoint);
                    endpointURIs.add(uri);
                });
        this.roundRobinOffset = new AtomicInteger(0);
    }

    private CloseableHttpClient create() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(clickhouseProperties.getConnectionTimeout())
                .setSocketTimeout(clickhouseProperties.getSocketTimeout())
                .setConnectionRequestTimeout(clickhouseProperties.getConnectionRequestTimeout())
                .build();
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(TrustAllStrategy.INSTANCE).build();
        HttpClientBuilder builder = HttpClients.custom()
                .setSSLContext(sslContext)
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(100)
                .setRetryHandler(new StandardHttpRequestRetryHandler());
        return builder.build();
    }

    @PreDestroy
    public void destroy() {
        if (this.httpClient != null) {
            try {
                this.httpClient.close();
            } catch (Exception ignored) {
                // ignored
            }
        }
    }

    public CKResp insert(String sql) {
        return execute(roundRobinWriteableEndpoint(), sql);
    }

    public CKResp query(String sql) {
        return execute(roundRobinWriteableEndpoint(), sql);
    }

    public List<CKResp> ddl(String sql) {
        List<CKResp> resultEntities = new ArrayList<>();
        this.endpointURIs.forEach(endpoint -> resultEntities.add(execute(endpoint, sql)));
        return resultEntities;
    }

    private CKResp execute(URI endpoint, String sql) {
        CKResp ckResp = CKResp.builder().status(-1).endpoint(endpoint.toString()).build();

        try {
            long t0 = System.currentTimeMillis();
            URIBuilder builder = new URIBuilder(endpoint);
            this.clickhouseProperties.getPops().forEach((k, v) -> builder.setParameter(k.toString(), v.toString()));
            builder.setParameter("database", this.clickhouseProperties.getDatabase());
            if (clickhouseProperties.getUsername() != null && clickhouseProperties.getPassword() != null) {
                builder.setParameter("user", this.clickhouseProperties.getUsername());
                builder.setParameter("password", this.clickhouseProperties.getPassword());
            }
            HttpPost httpPost = new HttpPost(builder.build());

            StringEntity entity = new StringEntity(sql, ContentType.TEXT_PLAIN);
            httpPost.setEntity(entity);
            if (executeRequestLogger.isTraceEnabled()) {
                executeRequestLogger.trace("Execute Request: {}, SQL: {}", endpoint, sql);
            }
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                StatusLine statusLine = response.getStatusLine();
                ckResp.setStatus(statusLine.getStatusCode());
                String resp = EntityUtils.toString(response.getEntity());
                ckResp.setMessage(resp);
                resp = resp.replace("\n", " ");
                resp = resp.replace("\t", " ");
                String ckSummary = Optional.ofNullable(response.getHeaders("X-ClickHouse-Summary"))
                        .map(Arrays::stream)
                        .map(headerStream -> headerStream.map(Header::getValue).collect(Collectors.joining(", ")))
                        .orElse(null);
                long t1 = System.currentTimeMillis();
                Duration duration = Duration.ofMillis(t1 - t0);
                if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                    String sqlContent = sql;
                    int index = sql.indexOf("\n");
                    if (index > 0) {
                        sqlContent = sql.substring(0, index);
                    }
                    logger.error("Bad Request: {}, {}, {}, sql='{}...'", endpoint, statusLine, resp, sqlContent);
                    sqlErrorLogger.error("\n=== === ===\n{}\n{}\n=== === ===", resp, sql);
                } else {
                    logger.info("Success Request: {}, {}, duration={}, summary='{}', resp='{}'", endpoint, statusLine,
                            duration, ckSummary, resp);
                }
            }
        } catch (Exception e) {
            logger.error("Error Request: {}, {}", endpoint, e.getMessage(), e);
            ckResp.setMessage(e.getMessage());
        }
        return ckResp;
    }

    private URI roundRobinWriteableEndpoint() {
        int current = this.roundRobinOffset.getAndIncrement();
        if (current > 100000000) { // 一个小目标？
            this.roundRobinOffset.getAndSet(0);
        }
        int size = this.endpointURIs.size();
        return this.endpointURIs.get(current % size);
    }

}
