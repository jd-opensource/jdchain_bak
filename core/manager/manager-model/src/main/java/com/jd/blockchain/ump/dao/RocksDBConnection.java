package com.jd.blockchain.ump.dao;


import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RocksDBConnection implements DBConnection {

    public static final String SCHEMA = "rocksdb";

    public static final String PROTOCOL_SPLIT = "://";

    public static final String ROCKSDB_PROTOCOL = SCHEMA + PROTOCOL_SPLIT;

    static {
        RocksDB.loadLibrary();
    }

    private RocksDB rocksDB;

    @Override
    public String dbSchema() {
        return SCHEMA;
    }

    @Override
    public DBConnection initDbUrl(String dbUrl) {
        if (!dbUrl.startsWith(dbSchema())) {
            throw new IllegalStateException(String.format("Unsupport DBConnection by URL {%s} !!!", dbUrl));
        }
        String dbSavePath = dbUrl.split(PROTOCOL_SPLIT)[1];
        initDBConnection(dbSavePath);
        return this;
    }

    @Override
    public void put(String key, String value) {
        if (this.rocksDB == null) {
            throw new IllegalStateException("Rocksdb is NULL, Please initDbUrl first !!!");
        }
        try {
            this.rocksDB.put(key.getBytes(UTF_8), value.getBytes(UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void put(String key, Object value, Class<?> type) {
        // 使用JSON序列化
        String json = JSON.toJSONString(value);
        put(key, json);
    }

    @Override
    public String get(String key) {
        try {
            byte[] value = this.rocksDB.get(key.getBytes(UTF_8));
            if (value != null && value.length > 0) {
                return new String(value, UTF_8);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        try {
            byte[] value = this.rocksDB.get(key.getBytes(UTF_8));
            if (value != null && value.length > 0) {
                String strObj = new String(value, UTF_8);
                return JSON.parseObject(strObj,type);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    @Override
    public void delete(String key) {
        try {
            byte[] value = this.rocksDB.get(key.getBytes(UTF_8));
            if (value != null && value.length > 0) {
                this.rocksDB.delete(value);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean exist(String dbUrl) {
        // 首先该dbUrl是Rocksdb
        if (dbUrl.startsWith(ROCKSDB_PROTOCOL)) {
            // 判断File是否存在，并且是文件夹
            File dbPath = new File(dbUrl.substring(ROCKSDB_PROTOCOL.length()));
            if (dbPath.exists() && dbPath.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    private void initDBConnection(String dbUrl) {
        try {
            File dbPath = new File(dbUrl);
            File dbParentPath = dbPath.getParentFile();
            if (!dbParentPath.exists()) {
                FileUtils.forceMkdir(dbParentPath);
            }
            this.rocksDB = RocksDB.open(initOptions(), dbUrl);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Options initOptions() {
        final Filter bloomFilter = new BloomFilter(32);
        final BlockBasedTableConfig tableOptions = new BlockBasedTableConfig()
                .setFilter(bloomFilter)
                .setBlockSize(4 * SizeUnit.KB)
                .setBlockSizeDeviation(10)
                .setBlockCacheSize(64 * SizeUnit.GB)
                .setNoBlockCache(false)
                .setCacheIndexAndFilterBlocks(true)
                .setBlockRestartInterval(16)
                ;
        final List<CompressionType> compressionLevels = new ArrayList<>();
        compressionLevels.add(CompressionType.NO_COMPRESSION); // 0-1
        compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 1-2
        compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 2-3
        compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 3-4
        compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 4-5
        compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 5-6
        compressionLevels.add(CompressionType.SNAPPY_COMPRESSION); // 6-7

        Options options = new Options()
                .setAllowConcurrentMemtableWrite(true)
                .setEnableWriteThreadAdaptiveYield(true)
                .setCreateIfMissing(true)
                .setMaxWriteBufferNumber(3)
                .setTableFormatConfig(tableOptions)
                .setMaxBackgroundCompactions(10)
                .setMaxBackgroundFlushes(4)
                .setBloomLocality(10)
                .setMinWriteBufferNumberToMerge(4)
                .setCompressionPerLevel(compressionLevels)
                .setNumLevels(7)
                .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
                .setCompactionStyle(CompactionStyle.UNIVERSAL)
                .setMemTableConfig(new SkipListMemTableConfig())
                ;
        return options;
    }

    public static void main(String[] args) {
        String path = "rocksdb:///zhangsan/lisi";
        System.out.println(path.substring(ROCKSDB_PROTOCOL.length()));
    }
}
