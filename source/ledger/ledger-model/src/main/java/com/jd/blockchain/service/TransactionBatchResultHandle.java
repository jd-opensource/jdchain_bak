package com.jd.blockchain.service;

import com.jd.blockchain.ledger.TransactionState;

public interface TransactionBatchResultHandle extends TransactionBatchResult{
	
	void commit();
	
	void cancel(TransactionState errorResult);
}
