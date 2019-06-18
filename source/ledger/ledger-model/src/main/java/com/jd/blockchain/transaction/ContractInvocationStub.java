package com.jd.blockchain.transaction;

/**
 * 用于在上下文中传递合约调用返回值的工具类；
 * 
 * @author huanghaiquan
 *
 */
class ContractInvocationStub {

	private static ThreadLocal<ContractInvocation> stub = new ThreadLocal<ContractInvocation>();

	private ContractInvocationStub() {
	}

	public static void set(ContractInvocation invocation) {
		if (invocation == null) {
			throw new IllegalArgumentException("Null stub value!");
		}
		stub.set(invocation);
	}

	public static ContractInvocation take() {
		ContractInvocation subValue = stub.get();
		if (subValue == null) {
			throw new IllegalStateException(
					"The latest invocation of contract has not been stubbed! It may be caused by the wrong call sequence from the upper layer!");
		}
		stub.remove();
		return subValue;
	}

}
