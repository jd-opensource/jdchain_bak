/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/18 下午2:50
 * Description:
 */
package com.jd.blockchain.consensus.mq;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.SettingsFactory;
import com.jd.blockchain.consensus.client.ClientFactory;
import com.jd.blockchain.consensus.mq.client.MsgQueueClientFactory;
import com.jd.blockchain.consensus.mq.config.MsgQueueSettingsFactory;
import com.jd.blockchain.consensus.mq.server.MsgQueueNodeServerFactory;
import com.jd.blockchain.consensus.service.NodeServerFactory;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/18
 * @since 1.0.0
 */

public class MsgQueueConsensusProvider implements ConsensusProvider {

    public static final String NAME = MsgQueueConsensusProvider.class.getName();

    private static MsgQueueSettingsFactory settingsFactory = new MsgQueueSettingsFactory();

    private static MsgQueueClientFactory clientFactory = new MsgQueueClientFactory();

    private static MsgQueueNodeServerFactory nodeServerFactory = new MsgQueueNodeServerFactory();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public SettingsFactory getSettingsFactory() {
        return settingsFactory;
    }

    @Override
    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    @Override
    public NodeServerFactory getServerFactory() {
        return nodeServerFactory;
    }
}