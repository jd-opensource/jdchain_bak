package com.jd.blockchain.sdk.service;

import com.jd.blockchain.consensus.ActionMessage;
import com.jd.blockchain.consensus.OrderedAction;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.transaction.TransactionService;

/**
 * 带共识的交易服务；
 * 
 * @author huanghaiquan
 *
 */
public interface ConsensusTransactionService extends TransactionService {

	@OrderedAction(groupIndexer = LedgerGroupIndexer.class, responseConverter = TransactionResponseMessageConverter.class)
	@Override
	TransactionResponse process(
            @ActionMessage(converter = TransactionRequestMessageConverter.class) TransactionRequest txRequest);

}
