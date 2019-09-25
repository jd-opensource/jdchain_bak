package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.VersioningKVEntry;

public interface MerkleDataEntry {
	
	VersioningKVEntry<Bytes, byte[]> getData();
	
	MerkleProof getProof();
}
