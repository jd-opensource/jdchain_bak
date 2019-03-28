package com.jd.blockchain.ledger.core;

import com.jd.blockchain.utils.Bytes;

public interface MerkleDataNode extends MerkleNode {

	long getSN();

	Bytes getKey();

	long getVersion();

}