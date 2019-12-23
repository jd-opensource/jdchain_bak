package com.jd.blockchain.storage.service.impl.composite;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CompositeConnectionFactory implements DbConnectionFactory {

    // SPI的方式，需要初始化对应的Factory
    private static final Map<String, DbConnectionFactory> connectionFactoryMap = new ConcurrentHashMap<>();

    private static Logger LOGGER = LoggerFactory.getLogger(CompositeConnectionFactory.class);

    public CompositeConnectionFactory() {
        init();
    }

    private void init() {
        // 初始化所有实现类
        Reflections reflections = new Reflections("com.jd.blockchain.storage.service");

        Set<Class<? extends DbConnectionFactory>> connectionSet =
                reflections.getSubTypesOf(DbConnectionFactory.class);

        for (Class<? extends DbConnectionFactory> clazz : connectionSet) {
            if (clazz.equals(CompositeConnectionFactory.class)) {
                continue;
            } else {
                try {
                    // 根据class生成对象
                    DbConnectionFactory dbConnectionFactory = clazz.newInstance();
                    String dbPrefix = dbConnectionFactory.dbPrefix();
                    if (dbPrefix != null && dbPrefix.length() > 0 &&
                            !connectionFactoryMap.containsKey(dbPrefix)) {
                        connectionFactoryMap.put(dbPrefix, dbConnectionFactory);
                    }
                } catch (Exception e) {
                    LOGGER.error("class:{} init error {}", clazz.getName(), e.getMessage());
                }
            }
        }
    }


    @Override
    public DbConnection connect(String dbUri) {
        return connect(dbUri, null);
    }

    @Override
    public DbConnection connect(String dbConnectionString, String password) {
        if (connectionFactoryMap.isEmpty()) {
            throw new IllegalArgumentException("DB connections is empty, please init first!");
        }

        for (Map.Entry<String, DbConnectionFactory> entry : connectionFactoryMap.entrySet()) {
            String prefix = entry.getKey();
            if (dbConnectionString.startsWith(prefix)) {
                return entry.getValue().connect(dbConnectionString, password);
            }
        }

        throw new IllegalArgumentException("Illegal format of composite db connection string!");
    }

    @Override
    public void close() {
        if (!connectionFactoryMap.isEmpty()) {
            for (Map.Entry<String, DbConnectionFactory> entry : connectionFactoryMap.entrySet()) {
                DbConnectionFactory dbConnectionFactory = entry.getValue();
                dbConnectionFactory.close();
            }
        }
    }

    @Override
    public String dbPrefix() {
        return null;
    }

    @Override
    public boolean support(String scheme) {
        if (!connectionFactoryMap.isEmpty()) {
            for (Map.Entry<String, DbConnectionFactory> entry : connectionFactoryMap.entrySet()) {
                DbConnectionFactory dbConnectionFactory = entry.getValue();
                if (dbConnectionFactory.support(scheme)) {
                    return true;
                }
            }
        }
        return false;
    }
}
