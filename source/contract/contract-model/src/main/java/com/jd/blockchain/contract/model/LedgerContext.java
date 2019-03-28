package com.jd.blockchain.contract.model;

import com.jd.blockchain.ledger.data.DataAccountOperator;
import com.jd.blockchain.ledger.data.UserOperator;
import com.jd.blockchain.sdk.BlockchainQueryService;

public interface LedgerContext extends BlockchainQueryService, UserOperator, DataAccountOperator{
	
	
	
}
