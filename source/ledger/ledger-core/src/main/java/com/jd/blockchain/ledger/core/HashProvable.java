package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.HashProof;
import com.jd.blockchain.utils.Bytes;

public interface HashProvable {
	
	HashProof getProof(Bytes key);
	
}
