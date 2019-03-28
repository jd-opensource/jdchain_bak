package com.jd.blockchain.consensus.bftsmart.client;

import bftsmart.reconfiguration.util.TOMConfiguration;
import bftsmart.reconfiguration.views.MemoryBasedViewStorage;
import bftsmart.tom.AsynchServiceProxy;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusConfig;
import com.jd.blockchain.consensus.bftsmart.BftsmartTopology;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.atomic.AtomicInteger;

public class BftsmartPeerProxyFactory extends BasePooledObjectFactory<AsynchServiceProxy> {

    private BftsmartClientSettings bftsmartClientSettings;

    private int gatewayId;

    private AtomicInteger index = new AtomicInteger(1);

    public BftsmartPeerProxyFactory(BftsmartClientSettings bftsmartClientSettings, int gatewayId) {
        this.bftsmartClientSettings = bftsmartClientSettings;
        this.gatewayId = gatewayId;
    }

    @Override
    public AsynchServiceProxy create() throws Exception {

        BftsmartTopology topology = BinarySerializeUtils.deserialize(bftsmartClientSettings.getTopology());

        MemoryBasedViewStorage viewStorage = new MemoryBasedViewStorage(topology.getView());
        TOMConfiguration tomConfiguration = BinarySerializeUtils.deserialize(bftsmartClientSettings.getTomConfig());

        //every proxy client has unique id;
        tomConfiguration.setProcessId(gatewayId + index.getAndIncrement());
        AsynchServiceProxy peerProxy = new AsynchServiceProxy(tomConfiguration, viewStorage);
        return peerProxy;
    }

    @Override
    public PooledObject<AsynchServiceProxy> wrap(AsynchServiceProxy asynchServiceProxy) {
        return new DefaultPooledObject<>(asynchServiceProxy);
    }
}
