package com.jd.blockchain.ump.service.consensus;

import com.jd.blockchain.ump.model.config.PeerLocalConfig;

import java.util.List;

public interface ConsensusService {

    String initConsensusConf(String consensusProvider, List<PeerLocalConfig> sharedConfigs);
}
