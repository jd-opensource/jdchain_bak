package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.CryptoAlgorithm;

public interface CryptoProviderInfo {
	
	String getName();
	
	CryptoAlgorithm[] getAlgorithms();
	
	
}
