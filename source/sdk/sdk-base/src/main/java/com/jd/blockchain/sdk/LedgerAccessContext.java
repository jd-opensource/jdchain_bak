package com.jd.blockchain.sdk;

import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.data.TransactionService;

public interface LedgerAccessContext {
	
	HashDigest getLedgerHash();
	
	CryptoSetting getCryptoSetting();
	
	TransactionService getTransactionService();
	
	BlockchainQueryService getQueryService();
	
	
}
