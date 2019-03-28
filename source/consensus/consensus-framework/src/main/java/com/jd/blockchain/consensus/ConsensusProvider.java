package com.jd.blockchain.consensus;

import com.jd.blockchain.consensus.client.ConsensusClientProvider;
import com.jd.blockchain.consensus.service.ConsensusServiceProvider;

public interface ConsensusProvider extends ConsensusClientProvider, ConsensusServiceProvider {

	String getName();

	SettingsFactory getSettingsFactory();

}
