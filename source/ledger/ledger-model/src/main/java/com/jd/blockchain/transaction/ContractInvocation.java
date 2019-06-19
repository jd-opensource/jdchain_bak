package com.jd.blockchain.transaction;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueEncoding;

/**
 * ContractInvocation 包装了客户端发起的一次合约方法调用的相关信息，用于在客户端交易处理上下文进行共享处理状态；
 * 
 * @author huanghaiquan
 *
 */
class ContractInvocation implements OperationReturnValueHandler {

	private Method method;

	private ContractType contractType;

	private int operationIndex = -1;

	private CompletableFuture<Object> returnValueFuture;

	public ContractInvocation(ContractType contractType, Method method) {
		this.contractType = contractType;
		this.method = method;
		this.returnValueFuture = new CompletableFuture<Object>();
	}

	public ContractType getContractType() {
		return contractType;
	}

	@Override
	public int getOperationIndex() {
		return operationIndex;
	}

	public void setOperationIndex(int operationIndex) {
		this.operationIndex = operationIndex;
	}

	public Class<?> getReturnType() {
		return method.getReturnType();
	}

	public Future<Object> getReturnValue() {
		return returnValueFuture;
	}

	@Override
	public Object setReturnValue(BytesValue bytesValue) {
		// Resolve BytesValue to an value object with the return type;
		Object returnValue = BytesValueEncoding.decode(bytesValue, method.getReturnType());
		returnValueFuture.complete(returnValue);
		return returnValue;
	}

}
