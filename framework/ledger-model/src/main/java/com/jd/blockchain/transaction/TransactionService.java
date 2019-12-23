package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;

public interface TransactionService {
	
	TransactionResponse process(TransactionRequest txRequest);
	
}
