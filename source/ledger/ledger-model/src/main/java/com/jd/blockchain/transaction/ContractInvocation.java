package com.jd.blockchain.transaction;

import java.lang.reflect.Method;

import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueEncoding;

/**
 * ContractInvocation 包装了客户端发起的一次合约方法调用的相关信息，用于在客户端交易处理上下文进行共享处理状态；
 * 
 * @author huanghaiquan
 *
 */
class ContractInvocation extends OperationResultHolder {

	private Method method;

	private ContractType contractType;

	private int operationIndex = -1;

	public ContractInvocation(ContractType contractType, Method method) {
		this.contractType = contractType;
		this.method = method;
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

	@Override
	protected Object decodeResult(BytesValue bytesValue) {
		return BytesValueEncoding.decode(bytesValue, method.getReturnType());
	}

}
