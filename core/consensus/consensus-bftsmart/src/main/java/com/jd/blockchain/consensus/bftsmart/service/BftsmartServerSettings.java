package com.jd.blockchain.consensus.bftsmart.service;

import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
import com.jd.blockchain.consensus.service.ServerSettings;

public interface BftsmartServerSettings extends ServerSettings {

    BftsmartConsensusSettings getConsensusSettings();

}
