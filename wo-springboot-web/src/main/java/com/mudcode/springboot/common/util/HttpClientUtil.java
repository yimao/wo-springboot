package com.mudcode.springboot.common.util;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {

    private static final int DEFAULT_TIMEOUT_MS = (int) TimeUnit.SECONDS.toMillis(5);

    private String username;

    private String password;

    private CloseableHttpClient httpClient;

    public HttpClientUtil() {
    }

    public HttpClientUtil(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private static void addHeader(HttpMessage message, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            Header[] h = new BasicHeader[headers.size()];
            int index = 0;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    h[index++] = new BasicHeader(entry.getKey(), value);
                }
            }
            message.setHeaders(h);
        }
    }

    public void init() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        int timeout = DEFAULT_TIMEOUT_MS;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();
        HttpClientBuilder builder = HttpClients.custom()
                .setSSLContext(SSLContexts.custom().loadTrustMaterial(TrustAllStrategy.INSTANCE).build())
                .setSSLHostnameVerifier((s, sslSession) -> true)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new StandardHttpRequestRetryHandler())
                .setMaxConnPerRoute(100)
                .setMaxConnTotal(200)
                .evictIdleConnections(180, TimeUnit.SECONDS);
        if (username != null && password != null) {
            CredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
            basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setDefaultCredentialsProvider(basicCredentialsProvider);
        }
        httpClient = builder.build();
    }

    public void close() {
        if (httpClient != null) {
            try {
                this.httpClient.close();
            } catch (IOException e) {
                // ignored
            }
        }
    }

    public CloseableHttpResponse execute(final HttpUriRequest request) throws IOException {
        return this.httpClient.execute(request);
    }

    public String get(String url, Map<String, String> headers, Map<String, String> params)
            throws IOException, URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
        }
        HttpGet httpGet = new HttpGet(builder.build());
        addHeader(httpGet, headers);
        return entityToString(httpGet);
    }

    public String post(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> parameters = new ArrayList<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);
        addHeader(httpPost, headers);
        return entityToString(httpPost);
    }

    public String postJsonBody(String url, Map<String, String> headers, String jsonBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        addHeader(httpPost, headers);
        return entityToString(httpPost);
    }

    private String entityToString(HttpUriRequest request) throws IOException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            String entityContent = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (HttpStatus.SC_OK == statusLine.getStatusCode()) {
                return entityContent;
            } else {
                throw new IOException(
                        "HTTP Response ERROR: StatusLine = '" + statusLine + "', EntityContent = " + entityContent);
            }
        }
    }

}
