/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.config.MsgQueueServerConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 上午11:32
 * Description:
 */
package com.jd.blockchain.consensus.mq.config;

import com.jd.blockchain.consensus.mq.settings.MsgQueueBlockSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueConsensusSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueNodeSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueServerSettings;

/**
 * peer节点配置
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueServerConfig implements MsgQueueServerSettings {

    private MsgQueueBlockSettings blockSettings;

    private MsgQueueConsensusSettings consensusSettings;

    private MsgQueueNodeSettings nodeSettings;

    private String realmName;

    public MsgQueueServerConfig setRealmName(String realmName) {
        this.realmName = realmName;
        return this;
    }

    public MsgQueueServerConfig setBlockSettings(MsgQueueBlockSettings blockSettings) {
        this.blockSettings = blockSettings;
        return this;
    }

    public MsgQueueServerConfig setConsensusSettings(MsgQueueConsensusSettings consensusSettings) {
        this.consensusSettings = consensusSettings;
        return setBlockSettings(consensusSettings.getBlockSettings());
    }

    public MsgQueueServerConfig setNodeSettings(MsgQueueNodeSettings nodeSettings) {
        this.nodeSettings = nodeSettings;
        return this;
    }

    @Override
    public String getRealmName() {
        return this.realmName;
    }

    @Override
    public MsgQueueNodeSettings getReplicaSettings() {
        return nodeSettings;
    }

    @Override
    public MsgQueueBlockSettings getBlockSettings() {
        return blockSettings;
    }

    @Override
    public MsgQueueConsensusSettings getConsensusSettings() {
        return consensusSettings;
    }
}