package com.jd.blockchain.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.ledger.BytesValueEncoding;
import com.jd.blockchain.ledger.BytesValueList;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.IllegalDataException;

public class ContractInvocationHandler implements InvocationHandler {

	private Bytes contractAddress;

	private ContractType contractType;

	private ContractEventSendOperationBuilder sendOpBuilder;

	private int proxyHashCode;

	public ContractInvocationHandler(Bytes contractAddress, ContractType contractType,
			ContractEventSendOperationBuilder sendOpBuilder) {
		this.contractAddress = contractAddress;
		if (contractType == null) {
			throw new IllegalDataException("contractType == null, no invoke really.");
		}
		this.contractType = contractType;
		this.sendOpBuilder = sendOpBuilder;
		this.proxyHashCode = Arrays.deepHashCode(new Object[] { this, contractAddress, contractType, sendOpBuilder });
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 判断是否是常规方法调用
		if (method.getName().equals("hashCode")) {
			// 该处需要使用当前代理类的HashCode
			return proxyHashCode;
		}
		if (method.getName().equals("toString")) {
			// 该处使用当前代理类的toString
			return this.toString();
		}

		String event = contractType.getEvent(method);
		if (event == null) {
			// 该方法不是合约可执行的方法
			throw new ContractException(
					String.format("The invoking method [%s] is not annotated as event handle method by @ContractEvent!",
							method.toString()));
		}
		// 序列化调用参数；
		Class<?>[] argTypes = method.getParameterTypes();
		BytesValueList argBytes = BytesValueEncoding.encodeArray(args, argTypes);

		// 定义合约调用操作；
		ContractEventSendOpTemplate opTemplate = (ContractEventSendOpTemplate) sendOpBuilder.send(contractAddress,
				event, argBytes);

		// 加入合约调用的额外信息；
		ContractInvocation invocation = new ContractInvocation(contractType, method);

		// 传递给定义操作的上下文，以便在生成交易时，同步操作在交易中的索引位置；
		opTemplate.setInvocation(invocation);

		// 传递给通过代理对象调用合约方法的调用者，以便可以同步操作在交易中的索引位置以及操作的返回值；
		ContractInvocationStub.set(invocation);

		// 返回类型的默认值
		return BytesValueEncoding.getDefaultValue(method.getReturnType());
	}

}
