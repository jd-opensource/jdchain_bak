package com.jd.blockchain.ledger;

import com.jd.blockchain.utils.Bytes;

public interface MerkleDataNode extends MerkleNode {

	long getSN();

	Bytes getKey();

	long getVersion();

}