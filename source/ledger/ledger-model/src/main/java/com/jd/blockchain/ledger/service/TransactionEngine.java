package com.jd.blockchain.ledger.service;

import com.jd.blockchain.crypto.hash.HashDigest;

public interface TransactionEngine {
	
	TransactionBatchProcess createNextBatch(HashDigest ledgerHash);
	
	TransactionBatchProcess getBatch(HashDigest ledgerHash);
	
}
