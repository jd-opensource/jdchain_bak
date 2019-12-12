/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.config.MsgQueueConsensusConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 上午11:26
 * Description:
 */
package com.jd.blockchain.consensus.mq.config;

import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.mq.settings.*;
import com.jd.blockchain.crypto.PubKey;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置消息队列的信息
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueConsensusConfig implements MsgQueueConsensusSettings {

    private List<NodeSettings> nodeSettingsList = new ArrayList<>();

    private MsgQueueNetworkSettings networkSettings;

    private MsgQueueBlockSettings blockSettings;

    public MsgQueueConsensusConfig addNodeSettings(MsgQueueNodeSettings nodeSettings) {
        nodeSettingsList.add(nodeSettings);
        return this;
    }

    public MsgQueueConsensusConfig setNetworkSettings(MsgQueueNetworkSettings networkSettings) {
        this.networkSettings = networkSettings;
        return this;
    }

    public MsgQueueConsensusConfig setBlockSettings(MsgQueueBlockSettings blockSettings) {
        this.blockSettings = blockSettings;
        return this;
    }

    @Override
    public NodeSettings[] getNodes() {
        return nodeSettingsList.toArray(new NodeSettings[nodeSettingsList.size()]);
    }

    @Override
    public MsgQueueNetworkSettings getNetworkSettings() {
        return networkSettings;
    }

    @Override
    public MsgQueueBlockSettings getBlockSettings() {
        return blockSettings;
    }
}