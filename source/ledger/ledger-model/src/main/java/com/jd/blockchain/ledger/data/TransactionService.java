package com.jd.blockchain.ledger.data;

import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;

public interface TransactionService {
	
	TransactionResponse process(TransactionRequest txRequest);
	
}
