package com.jd.blockchain.ump.service.consensus.providers;

import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.service.consensus.ConsensusProvider;

import java.util.List;
import java.util.Properties;

public class MsgQueueConsensusProvider implements ConsensusProvider {

    private static final String MSGQUEUE_PROVIDER = "com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider";

    private static final String MSGQUEUE_CONFIG_FILE = "mq.default.config";

    private Properties msgQueueProps;

    @Override
    public String provider() {
        return MSGQUEUE_PROVIDER;
    }

    @Override
    public String configFilePath() {
        return MSGQUEUE_CONFIG_FILE;
    }

    @Override
    public void setConfig(Properties properties) {
        this.msgQueueProps = properties;
    }

    @Override
    public Properties getConfig() {
        return msgQueueProps;
    }

    @Override
    public byte[] handleSharedConfigs(List<PeerLocalConfig> sharedConfigs) {
        return new byte[0];
    }
}
