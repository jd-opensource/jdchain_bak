package com.jd.blockchain.sdk;

public interface TransactionalScope {
	
	void startNewTransaction(Runnable runnable);
	
}
