package com.jd.blockchain.ump.service.consensus;

import com.jd.blockchain.ump.model.config.PeerLocalConfig;

import java.util.List;
import java.util.Properties;

public interface ConsensusProvider {

    String NEXT_LINE = "\r\n";

    String provider();

    String configFilePath();

    void setConfig(Properties properties);

    Properties getConfig();

    byte[] handleSharedConfigs(List<PeerLocalConfig> sharedConfigs);
}
