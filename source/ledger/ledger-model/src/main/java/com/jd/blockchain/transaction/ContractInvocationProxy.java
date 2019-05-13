package com.jd.blockchain.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

public class ContractInvocationProxy implements InvocationHandler {

	// private String contractMessage;

	private Bytes contractAddress;

	private ContractType contractType;

	private ContractEventSendOperationBuilder sendOpBuilder;

	public ContractInvocationProxy(Bytes contractAddress, ContractType contractType,
			ContractEventSendOperationBuilder sendOpBuilder) {
		this.contractAddress = contractAddress;
		this.contractType = contractType;
		this.sendOpBuilder = sendOpBuilder;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if(contractType == null){
			return "contractType == null, no invoke really.";
		}

		String event = contractType.getEvent(method);
		if (event == null) {
			// 适配 Object 对象的方法；
			// toString 方法；
			return String.format("[%s]-%s", contractAddress, contractType.toString());

			// hashCode 方法；
		}
		// 合约方法；
		byte[] argBytes = serializeArgs(args);
		sendOpBuilder.send(contractAddress, event, argBytes);

		// TODO: 暂时未考虑有返回值的情况；
		return null;
	}

	private byte[] serializeArgs(Object[] args) {
		// TODO 根据方法参数的定义序列化参数；
		return BinarySerializeUtils.serialize(args);
	}
}
