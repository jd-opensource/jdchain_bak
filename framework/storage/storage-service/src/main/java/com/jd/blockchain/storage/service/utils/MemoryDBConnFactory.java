/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.storage.service.utils.MemoryDBConnFactory
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/25 上午10:52
 * Description:
 */
package com.jd.blockchain.storage.service.utils;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/25
 * @since 1.0.0
 */

public class MemoryDBConnFactory implements DbConnectionFactory {

    private  Map<String, DbConnection> memMap = new ConcurrentHashMap<>();

    @Override
    public String dbPrefix() {
        return "memory://";
    }

    @Override
    public boolean support(String scheme) {
        return true;
    }

    @Override
    public synchronized DbConnection connect(String dbConnectionString) {
        DbConnection mem = memMap.get(dbConnectionString);
        if (mem == null) {
            mem = new MemoryDBConn();
            memMap.put(dbConnectionString, mem);
        }
        return mem;
    }

    @Override
    public synchronized DbConnection connect(String dbConnectionString, String password) {
        return connect(dbConnectionString);
    }

    @Override
    public void close() {
        memMap.clear();
    }
}