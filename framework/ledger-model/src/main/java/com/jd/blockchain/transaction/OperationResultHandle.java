package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BytesValue;

/**
 * 操作返回值处理器；
 * 
 * @author huanghaiquan
 *
 */
interface OperationResultHandle {

	/**
	 * 操作的索引位置；
	 * 
	 * @return
	 */
	int getOperationIndex();

	/**
	 * 正常地完成；
	 * 
	 * @param returnBytesValue
	 * @return
	 */
	Object complete(BytesValue returnBytesValue);

	/**
	 * 以异常方式完成；
	 * 
	 * @param error
	 */
	void complete(Throwable error);

}
