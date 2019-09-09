package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.storage.service.VersioningKVEntry;

public interface MerkleDataEntry {
	
	VersioningKVEntry getData();
	
	MerkleProof getProof();
}
