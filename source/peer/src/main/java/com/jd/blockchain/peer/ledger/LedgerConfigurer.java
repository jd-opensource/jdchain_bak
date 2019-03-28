package com.jd.blockchain.peer.ledger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jd.blockchain.ledger.core.impl.DefaultOperationHandleRegisteration;
import com.jd.blockchain.ledger.core.impl.LedgerManager;
import com.jd.blockchain.ledger.core.impl.OperationHandleRegisteration;
import com.jd.blockchain.ledger.core.impl.TransactionEngineImpl;
import com.jd.blockchain.ledger.service.TransactionEngine;

@Configuration
public class LedgerConfigurer {

	@Bean
	public LedgerManager ledgerManager() {
		return new LedgerManager();
	}
	
	@Bean
	public TransactionEngine transactionEngine() {
		return new TransactionEngineImpl();
	}
	
	@Bean
	public OperationHandleRegisteration operationHandleRegisteration() {
		return new DefaultOperationHandleRegisteration();
	}
}
