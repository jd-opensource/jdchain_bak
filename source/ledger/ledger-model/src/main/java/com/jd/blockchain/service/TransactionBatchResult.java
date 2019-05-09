package com.jd.blockchain.service;

import java.util.Iterator;

import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.TransactionResponse;

public interface TransactionBatchResult {
	
	LedgerBlock getBlock();
	
	Iterator<TransactionResponse> getResponses();
	
}
