package com.jd.blockchain.ledger;

import com.jd.blockchain.utils.Dataset;

public interface Account {
	
	BlockchainIdentity getID();
	
	Dataset<String, TypedValue> getDataset();
	
}
