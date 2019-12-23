package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.Account;
import com.jd.blockchain.ledger.MerkleSnapshot;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.utils.Dataset;

public interface CompositeAccount extends Account, MerkleSnapshot, HashProvable{

	Dataset<String, TypedValue> getHeaders();
	
}
