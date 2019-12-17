package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.Operation;

/**
 * 在交易处理过程中，提供对多种交易操作处理器互相调用的机制；
 * 
 * @author huanghaiquan
 *
 */
public interface OperationHandleContext {

	void handle(Operation operation);

}
