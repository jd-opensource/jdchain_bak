package com.jd.blockchain.contract;

import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.DataAccountOperator;
import com.jd.blockchain.transaction.UserOperator;

public interface LedgerContext extends BlockchainQueryService, UserOperator, DataAccountOperator{
	
	
	
}
