/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.config.MsgQueueClientIncomingConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 上午11:50
 * Description:
 */
package com.jd.blockchain.consensus.mq.config;

import com.jd.blockchain.consensus.ClientIncomingSettings;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider;
import com.jd.blockchain.consensus.mq.settings.MsgQueueClientIncomingSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueConsensusSettings;
import com.jd.blockchain.crypto.PubKey;

import java.lang.reflect.Method;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueClientIncomingConfig implements MsgQueueClientIncomingSettings {

    private int clientId;

    private PubKey pubKey;

    private MsgQueueConsensusSettings consensusSettings;

    public MsgQueueClientIncomingConfig setConsensusSettings(MsgQueueConsensusSettings consensusSettings) {
        this.consensusSettings = consensusSettings;
        return this;
    }

    public MsgQueueClientIncomingConfig setClientId(int clientId) {
        this.clientId = clientId;
        return this;
    }

    public MsgQueueClientIncomingConfig setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
        return this;
    }

    @Override
    public int getClientId() {
        return this.clientId;
    }

    @Override
    public String getProviderName() {
        return MsgQueueConsensusProvider.NAME;
    }

    @Override
    public MsgQueueConsensusSettings getConsensusSettings() {
        return this.consensusSettings;
    }

    @Override
    public PubKey getPubKey() {
        return pubKey;
    }
}