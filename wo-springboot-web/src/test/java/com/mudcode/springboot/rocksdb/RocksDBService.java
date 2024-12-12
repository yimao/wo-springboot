package com.mudcode.springboot.rocksdb;

import com.mudcode.springboot.common.util.ExecutorServiceUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Service
public class RocksDBService {

    private static final Logger logger = LoggerFactory.getLogger(RocksDBService.class);

    private RocksDB rocksDB;

    private String dbPath = "./rdb0";

    // a list which will hold the handles for the column families once the db is opened
    private List<ColumnFamilyHandle> columnFamilyHandleList;

    private ColumnFamilyOptions columnFamilyOptions;

    private DBOptions dbOptions;

    private ColumnFamilyHandle metaColumnFamilyHandle;

    private ColumnFamilyHandle dataColumnFamilyHandle;

    private Metadata metadata;

    private BlockingQueue<Entity> blockingQueue;

    private int localQueueSize = 100000;

    private ExecutorService flushExecutorService;

    private ScheduledExecutorService scheduledExecutorService;

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public void setLocalQueueSize(int localQueueSize) {
        this.localQueueSize = localQueueSize;
    }

    @PostConstruct
    public void init() throws RocksDBException, IOException {
        Path dataDbPath = Path.of(this.dbPath);
        Files.createDirectories(dataDbPath);

        /*
         * @see
         * https://github.com/facebook/rocksdb/wiki/RocksJava-Basics#opening-a-database-with-column-families
         */
        RocksDB.loadLibrary();
        this.columnFamilyHandleList = new ArrayList<>();
        List<ColumnFamilyDescriptor> cfDescriptors = createColumnFamilyDescriptors(dataDbPath);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.dbOptions = new DBOptions()
                .setIncreaseParallelism(availableProcessors)
                .setMaxBackgroundJobs(availableProcessors)
                .setMaxTotalWalSize(64 * 1024 * 1024) // 64MB
                .setDbWriteBufferSize(64 * 1024 * 1024) // 64MB
                .setCompactionReadaheadSize(2 * 1024 * 1024) // 2MB
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true)
                .setAtomicFlush(true)
        ;

        this.rocksDB = RocksDB.open(dbOptions, dataDbPath.toString(), cfDescriptors, columnFamilyHandleList);

        initDataColumnFamilyHandle();
        initMetadata();

        this.blockingQueue = new ArrayBlockingQueue<>(localQueueSize);
        this.flushExecutorService = ExecutorServiceUtil.newThreadPoolExecutor(availableProcessors, availableProcessors, "rdb-flush");
        this.scheduledExecutorService = ExecutorServiceUtil.newSingleThreadScheduledExecutor("rdb-flush-trigger");
        this.scheduledExecutorService.scheduleWithFixedDelay(this::flushLocalQueue, 1, 1, TimeUnit.SECONDS);
    }

    @NotNull
    private List<ColumnFamilyDescriptor> createColumnFamilyDescriptors(Path dataDbPath) throws RocksDBException {
        this.columnFamilyOptions = new ColumnFamilyOptions().optimizeUniversalStyleCompaction();
        List<ColumnFamilyDescriptor> cfDescriptors = Collections.emptyList();
        Options options = new Options();
        options.setCreateIfMissing(true);
        options.setAtomicFlush(true);
        List<byte[]> loadedCFDs = RocksDB.listColumnFamilies(options, dataDbPath.toString());
        if (loadedCFDs.isEmpty()) {
            ColumnFamilyDescriptor defaultCfDescriptor = new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY,
                    columnFamilyOptions);
            ColumnFamilyDescriptor metaCfDescriptor = new ColumnFamilyDescriptor("rdb-meta".getBytes(),
                    columnFamilyOptions);
            ColumnFamilyDescriptor dataCfDescriptor = new ColumnFamilyDescriptor("rdb-data".getBytes(),
                    columnFamilyOptions);
            // list of column family descriptors, first entry must always be default
            // column family
            cfDescriptors = Arrays.asList(defaultCfDescriptor, metaCfDescriptor, dataCfDescriptor);
        } else {
            cfDescriptors = new ArrayList<>();
            for (byte[] cfName : loadedCFDs) {
                cfDescriptors.add(new ColumnFamilyDescriptor(cfName, columnFamilyOptions));
            }
        }
        return cfDescriptors;
    }

    private void initDataColumnFamilyHandle() {
        this.columnFamilyHandleList.forEach(columnFamilyHandle -> {
            try {
                byte[] cfhName = columnFamilyHandle.getName();
                if (Arrays.equals(cfhName, "rdb-meta".getBytes())) {
                    this.metaColumnFamilyHandle = columnFamilyHandle;
                } else if (Arrays.equals(cfhName, "rdb-data".getBytes())) {
                    this.dataColumnFamilyHandle = columnFamilyHandle;
                } else if (Arrays.equals(cfhName, RocksDB.DEFAULT_COLUMN_FAMILY)) {
                    // 默认的 cfh 不做处理
                    logger.debug("ignored default column family");
                } else {
                    logger.warn("unknown column family handle: {}", new String(cfhName));
                }
            } catch (Exception e) {
                logger.error("Init ColumnFamilyHandle error: {}", e.getMessage(), e);
            }
        });
        Objects.requireNonNull(this.metaColumnFamilyHandle);
        Objects.requireNonNull(this.dataColumnFamilyHandle);
    }

    private void initMetadata() throws RocksDBException {
        byte[] metadataBytes = this.rocksDB.get(this.metaColumnFamilyHandle, Metadata.DB_KEY);
        if (metadataBytes != null) {
            this.metadata = Metadata.parseFrom(metadataBytes);
        } else {
            this.metadata = new Metadata();
            long currentTimeMillis = System.currentTimeMillis();
            this.metadata.setCreateTimestamp(currentTimeMillis);
            this.metadata.setLastUpdateTimestamp(currentTimeMillis);
            this.rocksDB.put(this.metaColumnFamilyHandle, Metadata.DB_KEY, Metadata.toByteArray(this.metadata));
        }
    }

    @PreDestroy
    public void destroy() {
        ExecutorServiceUtil.stop(scheduledExecutorService, 10);
        ExecutorServiceUtil.stop(flushExecutorService, 10);
        this.flushLocalQueue();

        this.columnFamilyHandleList.forEach(this::closeQuietly);
        closeQuietly(this.columnFamilyOptions);
        closeQuietly(this.dbOptions);
        closeQuietly(this.rocksDB);
    }

    private void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void save(byte[] key, byte[] value) throws Exception {
        Entity entity = Entity.builder().operation(Entity.Operation.PUT).key(key).value(value).build();
        triggerFlushLocalQueueIfNecessary();
        while (!this.blockingQueue.offer(entity)) {
            this.flushExecutorService.submit(this::flushLocalQueue);
        }
    }

    public void delete(byte[] key) throws Exception {
        Entity entity = Entity.builder().operation(Entity.Operation.DEL).key(key).build();
        triggerFlushLocalQueueIfNecessary();
        while (!this.blockingQueue.offer(entity)) {
            this.flushExecutorService.submit(this::flushLocalQueue);
        }
    }

    private void triggerFlushLocalQueueIfNecessary() {
        long remainingCapacity = this.blockingQueue.remainingCapacity();
        if (this.localQueueSize - remainingCapacity > this.localQueueSize * 0.8) {
            this.flushExecutorService.submit(this::flushLocalQueue);
        }
    }

    public byte[] get(byte[] key) throws Exception {
        return this.rocksDB.get(this.dataColumnFamilyHandle, key);
    }

    public void list(BiConsumer<byte[], byte[]> consumer) {
        try (RocksIterator iterator = this.rocksDB.newIterator(this.dataColumnFamilyHandle)) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                byte[] key = iterator.key();
                byte[] value = iterator.value();
                consumer.accept(key, value);
            }
        }
    }

    public long lastUpdateTimestamp() {
        if (this.metadata != null) {
            return this.metadata.getLastUpdateTimestamp();
        }
        return 0;
    }

    public long createTimestamp() {
        if (this.metadata != null) {
            return this.metadata.getCreateTimestamp();
        }
        return 0;
    }

    private synchronized void flushLocalQueue() {
        List<Entity> dataEntities = new ArrayList<>();
        this.blockingQueue.drainTo(dataEntities);
        if (dataEntities.isEmpty()) {
            return;
        }
        try (WriteOptions writeOptions = new WriteOptions(); WriteBatch writeBatch = new WriteBatch()) {
            writeOptions.setDisableWAL(true);
            writeOptions.setIgnoreMissingColumnFamilies(true);
            for (Entity entity : dataEntities) {
                switch (entity.getOperation()) {
                    case PUT:
                        writeBatch.put(this.dataColumnFamilyHandle, entity.getKey(), entity.getValue());
                        break;
                    case DEL:
                        writeBatch.delete(this.dataColumnFamilyHandle, entity.getKey());
                        break;
                    default:
                        logger.error("unknown operation:{}", entity.getOperation());
                }
            }
            this.rocksDB.write(writeOptions, writeBatch);
        } catch (Exception e) {
            logger.error("rocksdb write batch error: {}", e.getMessage(), e);
        }
        updateMetadata();
    }

    private void updateMetadata() {
        long currentTimeMillis = System.currentTimeMillis();
        this.metadata.setLastUpdateTimestamp(currentTimeMillis);
        try {
            this.rocksDB.put(this.metaColumnFamilyHandle, Metadata.DB_KEY, Metadata.toByteArray(this.metadata));
        } catch (Exception e) {
            logger.error("rocksdb put error: {}", e.getMessage(), e);
        }
    }

}
