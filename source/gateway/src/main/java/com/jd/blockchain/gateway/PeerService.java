package com.jd.blockchain.gateway;

import com.jd.blockchain.ledger.data.TransactionService;
import com.jd.blockchain.sdk.BlockchainQueryService;

public interface PeerService {
	
	BlockchainQueryService getQueryService();
	
	TransactionService getTransactionService();
	
}
