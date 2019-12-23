package com.jd.blockchain.service;

import com.jd.blockchain.crypto.HashDigest;

public interface TransactionEngine {
	
	TransactionBatchProcess createNextBatch(HashDigest ledgerHash);
	
	TransactionBatchProcess getBatch(HashDigest ledgerHash);
	
}
