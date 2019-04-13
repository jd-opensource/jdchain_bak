package com.jd.blockchain.peer;

import com.jd.blockchain.consensus.service.NodeServer;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;

import java.util.List;

public interface LedgerBindingConfigAware {
	
	void setConfig(LedgerBindingConfig config);

	NodeServer setConfig(LedgerBindingConfig config, HashDigest ledgerHash);
}
