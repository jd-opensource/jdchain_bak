package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.TransactionReturnMessage;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.TransactionRequest;

/**
 * 事务上下文；
 * 
 * @author huanghaiquan
 *
 */
public interface LedgerTransactionContext {

	/**
	 * 账本数据；
	 * 
	 * @return
	 */
	LedgerDataSet getDataSet();

	/**
	 * 交易请求；
	 * 
	 * @return
	 */
	TransactionRequest getRequestTX();

	/**
	 * 提交对账本数据的修改，以指定的交易状态提交交易；
	 * 
	 * @param txResult
	 * @param returnMessage
	 *
	 * @return
	 */
	LedgerTransaction commit(TransactionState txResult, OperationResult... opResults);
	

	/**
	 * 抛弃对账本数据的修改，以指定的交易状态提交交易；<br>
	 * 
	 * 通常来说，当在开启事务之后，修改账本或者尝试提交交易（{@link #commit(TransactionState, TransactionReturnMessage)}）时发生错误，都应该抛弃数据，通过此方法记录一个表示错误状态的交易；
	 * 
	 * @param txResult
	 * @return
	 */
	LedgerTransaction discardAndCommit(TransactionState txResult);

	/**
	 * 回滚事务，抛弃本次事务的所有数据更新；
	 */
	void rollback();
}
