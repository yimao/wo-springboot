package com.mudcode.springboot.es;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

class AbstractBaseClient {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public RestHighLevelClient buildClient(ESProperties configuration) throws Exception {
        List<String> hosts = configuration.getHosts();
        int hostSize = hosts.size();
        if (hostSize == 0) {
            throw new NullPointerException("elastic search host can't be empty");
        }
        HttpHost[] hostArray = new HttpHost[hostSize];
        for (int i = 0; i < hostSize; i++) {
            hostArray[i] = HttpHost.create(hosts.get(i));
        }
        int timeout = (int) TimeUnit.SECONDS.toMillis(10);
        RestClientBuilder builder = RestClient.builder(hostArray)
                .setRequestConfigCallback(requestConfigBuilder -> RequestConfig.custom()
                        .setSocketTimeout(timeout)
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    try {
                        httpClientBuilder.setMaxConnTotal(200)
                                .setMaxConnPerRoute(100)
                                .setSSLContext(SSLContexts.custom().loadTrustMaterial(TrustAllStrategy.INSTANCE).build())
                                .setSSLHostnameVerifier((s, sslSession) -> true);
                    } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                        throw new RuntimeException(e);
                    }
                    String username = configuration.getUsername();
                    String password = configuration.getPassword();
                    if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY,
                                new UsernamePasswordCredentials(username, password));
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider).disableAuthCaching();
                    }
                    return httpClientBuilder;
                })
                .setFailureListener(new RestClient.FailureListener() {
                    @Override
                    public void onFailure(Node node) {
                        logger.error("es node failure: {}", node.toString());
                    }
                });
        RestHighLevelClient highLevelClient = new RestHighLevelClient(builder);
        boolean flag = highLevelClient.ping(RequestOptions.DEFAULT);
        if (!flag) {
            throw new IllegalArgumentException("elasticsearch ping failed");
        }
        logger.info("Initialized es client, cluster name: {}",
                highLevelClient.info(RequestOptions.DEFAULT).getClusterName());
        return highLevelClient;
    }

    public void closeClient(RestHighLevelClient highLevelClient) {
        if (highLevelClient != null) {
            try {
                highLevelClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
