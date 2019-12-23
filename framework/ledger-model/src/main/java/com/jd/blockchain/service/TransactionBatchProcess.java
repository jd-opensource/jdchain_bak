package com.jd.blockchain.service;

import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;

public interface TransactionBatchProcess {

	/**
	 * 安排执行指定的交易请求；
	 * 
	 * <p>
	 * 
	 * 注意：此方法并不表示此交易请求立即得到完整执行，并理解获得最终有效的结果；
	 * 
	 * 方法返回的 {@link TransactionResponse} 只是一个代理对象，其最终的值需要在整个批处理结果被成功地提交或者取消后才能确定。
	 * 
	 * @param request
	 *            交易请求；
	 * @return 交易执行回复；
	 */
	TransactionResponse schedule(TransactionRequest request);

	/**
	 * 完成本次批量执行；生成待提交的结果；
	 * 
	 * @return
	 */
	TransactionBatchResultHandle prepare();

	TransactionBatchResult cancel(TransactionState errorResult);

	long blockHeight();

}