/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.bft.BftsmartConsensusClientPool
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/10/30 下午6:50
 * Description:
 */
package com.jd.blockchain.consensus.bftsmart.client;

import bftsmart.tom.AsynchServiceProxy;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class BftsmartPeerProxyPool extends GenericObjectPool<AsynchServiceProxy> {

    public BftsmartPeerProxyPool(PooledObjectFactory<AsynchServiceProxy> factory) {
        this(factory, null);
    }

    public BftsmartPeerProxyPool(PooledObjectFactory<AsynchServiceProxy> factory, GenericObjectPoolConfig config) {
        super(factory, config == null ? new BftsmartPeerProxyPoolConfig() : config);
    }
}