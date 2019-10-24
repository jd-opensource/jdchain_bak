package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.utils.Dataset;

public interface LedgerAccount {
	
	BlockchainIdentity getID();
	
	Dataset<String, TypedValue> getDataset();
	
}
