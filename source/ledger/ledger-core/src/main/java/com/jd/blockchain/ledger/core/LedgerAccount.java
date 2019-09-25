package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.VersioningMap;

public interface LedgerAccount {
	
	BlockchainIdentity getID();
	
	VersioningMap<Bytes, BytesValue> getDataset();
	
}
