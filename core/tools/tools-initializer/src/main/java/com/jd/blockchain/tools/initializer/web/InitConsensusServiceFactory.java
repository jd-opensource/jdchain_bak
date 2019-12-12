package com.jd.blockchain.tools.initializer.web;

import com.jd.blockchain.utils.net.NetworkAddress;

public interface InitConsensusServiceFactory {
	
	public LedgerInitConsensusService connect(NetworkAddress endpointAddress);
	
}
