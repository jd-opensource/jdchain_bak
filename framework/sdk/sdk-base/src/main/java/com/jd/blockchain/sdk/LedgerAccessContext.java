package com.jd.blockchain.sdk;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TransactionService;

public interface LedgerAccessContext {
	
	HashDigest getLedgerHash();
	
	CryptoSetting getCryptoSetting();
	
	TransactionService getTransactionService();
	
	BlockchainQueryService getQueryService();
	
	
}
