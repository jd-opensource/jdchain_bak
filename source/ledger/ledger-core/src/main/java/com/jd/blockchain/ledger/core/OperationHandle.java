package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;


public interface OperationHandle {

	/**
	 * 是否支持指定类型的操作；
	 * 
	 * @param operationType
	 * @return
	 */
	boolean support(Class<?> operationType);

	/**
	 * 同步解析和执行操作；
	 *
	 *
	 * @param op
	 *            操作实例；
	 * @param newBlockDataset
	 *            需要修改的新区块的数据集；
	 * @param requestContext
	 *            交易请求上下文；
	 * @param previousBlockDataset
	 *            新区块的前一个区块的数据集；即未提交新区块之前的经过共识的账本最新数据集；
	 *
	 * @return 操作执行结果
	 */
	BytesValue process(Operation op, LedgerDataSet newBlockDataset, TransactionRequestContext requestContext,
			LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService);

//	/**
//	 * 异步解析和执行操作；
//	 * TODO 未来规划实现
//	 *
//	 *
//	 * @param op
//	 *            操作实例；
//	 * @param newBlockDataset
//	 *            需要修改的新区块的数据集；
//	 * @param requestContext
//	 *            交易请求上下文；
//	 * @param previousBlockDataset
//	 *            新区块的前一个区块的数据集；即未提交新区块之前的经过共识的账本最新数据集；
//	 *
//	 * @return 操作执行结果
//	 */
//	AsyncFuture<byte[]> asyncProcess(Operation op, LedgerDataSet newBlockDataset, TransactionRequestContext requestContext,
//									 LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService);
}
