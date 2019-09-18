package com.jd.blockchain.ump.dao;

import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DBConnectionProvider {

    private static final Map<String, DBConnection> dbConnections = new ConcurrentHashMap<>();

    static {
        init();
    }

    public static DBConnection dbConnection(String dbUrl) {
        String dbSchema = dbSchema(dbUrl);
        if (!dbConnections.containsKey(dbSchema)) {
            throw new IllegalStateException(
                    String.format("Can not find DBConnection by {%s} !", dbUrl));
        }

        DBConnection dbConnection = dbConnections.get(dbSchema);
        return dbConnection.initDbUrl(dbUrl);
    }


    private static String dbSchema(String dbUrl) {
        // rocksdb:///home/xxx  -> rocksdb
        return dbUrl.split("://")[0];

    }

    private static void init() {
        // 初始化所有实现类
        Reflections reflections = new Reflections("com.jd.blockchain.ump.dao");

        Set<Class<? extends DBConnection>> dbConnectionSet =
                reflections.getSubTypesOf(DBConnection.class);

        for (Class<? extends DBConnection> clazz : dbConnectionSet) {

            if (!clazz.isInterface() && !clazz.equals(UmpDaoHandler.class)) {
                try {
                    // 根据class生成对象
                    DBConnection dbConnection = clazz.newInstance();
                    String dbSchema = dbConnection.dbSchema();
                    if (dbSchema != null && dbSchema.length() > 0) {
                        dbConnections.put(dbSchema, dbConnection);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }
}
