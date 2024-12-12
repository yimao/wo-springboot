package com.mudcode.springboot.zookeeper;

import com.mudcode.springboot.common.encoder.HexEncoder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ZookeeperTest {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperTest.class);
    private CuratorFramework zkClient;

    @BeforeEach
    public void before() {

        String host = "zk-6:2181,zk-7:2181,zk-8:2181";
        String namespace = "yimao";

        int sessionTimeoutMs = 60 * 1000;
        int connectionTimeout = 2 * sessionTimeoutMs;
        int requestTimeout = 2 * connectionTimeout;

        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(host)
                .namespace(namespace)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeout)
                .waitForShutdownTimeoutMs(1000)
                .retryPolicy(new RetryForever(500))
                // .authorization("digest", "tingyun:nEtben@2_19".getBytes())
                .build();

        this.zkClient.start();
        boolean connected = false;
        try {
            connected = this.zkClient.blockUntilConnected(requestTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        if (!connected) {
            throw new IllegalStateException("zookeeper not connected");
        }

        // session id change callback
        this.zkClient.getConnectionStateListenable()
                .addListener(new SessionChangeConnectionStateListener());
    }

    @AfterEach
    public void after() throws InterruptedException {
        this.zkClient.close();
        this.zkClient = null;
    }

    @Test
    public void testSessionId() throws Exception {
        long sessionId = this.zkClient.getZookeeperClient().getZooKeeper().getSessionId();
        logger.info("zookeeper session id: {}", HexEncoder.toHexDigits(sessionId));
    }

    @Test
    public void testPathAndData() throws Exception {
        String nodePath = "/" + UUID.randomUUID().toString();
        // create path
        this.zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(nodePath, nodePath.getBytes());

        // exists
        Stat stat = this.zkClient.checkExists().forPath(nodePath);
        if (stat != null) {
            logger.info("node exists: {}", new Date(stat.getCtime()));
        }

        // get/set data
        this.zkClient.setData().forPath(nodePath, "hello".getBytes());
        byte[] dataUpdate = this.zkClient.getData().forPath(nodePath);
        logger.info("data update: {}", new String(dataUpdate));

        // list path
        List<String> pathList = this.zkClient.getChildren().forPath("/");
        logger.info("list path: {}", pathList);

        // delete path
        this.zkClient.delete().deletingChildrenIfNeeded().forPath(nodePath);
    }

    @Test
    public void testGetListPathData() throws Exception {
        String path = "/Tenant";
        Map<String, String> agreeIdMap = new HashMap<>();

        for (String childPath : this.zkClient.getChildren().forPath(path)) {
            byte[] data = this.zkClient.getData().forPath(path + "/" + childPath);
            agreeIdMap.put(childPath, new String(data, StandardCharsets.UTF_8));
        }

        logger.info("agreement id map: {}", agreeIdMap);
    }

    public static class SessionChangeConnectionStateListener implements ConnectionStateListener {
        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            long sessionId = 0;
            try {
                sessionId = client.getZookeeperClient().getZooKeeper().getSessionId();
            } catch (Exception e) {
                logger.error("getZookeeper error: {}", e.getMessage());
            }
            logger.warn("zk connection state change: {} - {}", HexEncoder.toHexDigits(sessionId), newState.toString());
            if (newState == ConnectionState.RECONNECTED) {
                // TODO register watcher or create temp node
            }
        }
    }
}
