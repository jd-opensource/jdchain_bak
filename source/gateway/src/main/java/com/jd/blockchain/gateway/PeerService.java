package com.jd.blockchain.gateway;

import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TransactionService;

public interface PeerService {
	
	BlockchainQueryService getQueryService();
	
	TransactionService getTransactionService();
	
}
