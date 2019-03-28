/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.config.MsgQueueClientConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 下午2:23
 * Description:
 */
package com.jd.blockchain.consensus.mq.config;

import com.jd.blockchain.consensus.mq.settings.MsgQueueClientSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueConsensusSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueNetworkSettings;
import com.jd.blockchain.crypto.PubKey;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueClientConfig implements MsgQueueClientSettings {

    private int id;

    private PubKey pubKey;

    private MsgQueueConsensusSettings consensusSettings;

    public MsgQueueClientConfig setId(int id) {
        this.id = id;
        return this;
    }

    public MsgQueueClientConfig setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
        return this;
    }

    public MsgQueueClientConfig setConsensusSettings(MsgQueueConsensusSettings consensusSettings) {
        this.consensusSettings = consensusSettings;
        return this;
    }

    @Override
    public int getClientId() {
        return this.id;
    }

    @Override
    public PubKey getClientPubKey() {
        return this.pubKey;
    }

    @Override
    public MsgQueueConsensusSettings getConsensusSettings() {
        return this.consensusSettings;
    }

    @Override
    public MsgQueueNetworkSettings getMsgQueueNetworkSettings() {
        return this.consensusSettings.getNetworkSettings();
    }
}