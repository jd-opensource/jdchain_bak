package com.jd.blockchain.ump.dao;

public interface DBConnection {

    String dbSchema();

    DBConnection initDbUrl(String dbUrl);

    void put(String key, String value);

    void put(String key, Object value, Class<?> type);

    String get(String key);

    <T> T get(String key, Class<T> type);

    void delete(String key);

    boolean exist(String dbUrl);
}
