package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.Operation;

public interface OperationHandle {

	/**
	 * 是否支持指定类型的操作；
	 * 
	 * @param operationType
	 * @return
	 */
	Class<?> getOperationType();

	/**
	 * 同步解析和执行操作；
	 *
	 *
	 * @param op                   操作实例；
	 * @param newBlockDataset      需要修改的新区块的数据集；
	 * @param requestContext       交易请求上下文；
	 * @param previousBlockDataset 新区块的前一个区块的数据集；即未提交新区块之前的经过共识的账本最新数据集；注：此数据集是只读的；
	 *
	 * @param handleContext        操作上下文；`
	 * @param ledgerService
	 * @return
	 */
	BytesValue process(Operation op, LedgerDataset newBlockDataset, TransactionRequestExtension requestContext,
			LedgerQuery ledger, OperationHandleContext handleContext);

}
