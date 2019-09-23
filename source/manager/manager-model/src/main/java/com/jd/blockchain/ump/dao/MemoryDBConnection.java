package com.jd.blockchain.ump.dao;

import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryDBConnection implements DBConnection {

    private static final String MEMORY_SCHEMA = "memory";

    private final Map<String, String> memory = new ConcurrentHashMap<>();

    @Override
    public String dbSchema() {
        return MEMORY_SCHEMA;
    }

    @Override
    public DBConnection initDbUrl(String dbUrl) {
        return this;
    }

    @Override
    public void put(String key, String value) {
        memory.put(key, value);
    }

    @Override
    public void put(String key, Object value, Class<?> type) {
        String json = JSON.toJSONString(value);
        put(key, json);
    }

    @Override
    public String get(String key) {
        return memory.get(key);
    }

    @Override
    public boolean exist(String dbUrl) {
        if (dbUrl.startsWith(MEMORY_SCHEMA)) {
            return true;
        }
        return false;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        String record = memory.get(key);
        return JSON.parseObject(record,type);
    }

    @Override
    public void delete(String key) {
        memory.remove(key);
    }
}
