package com.jd.blockchain.mocker.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValueEncoding;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.mocker.MockerNodeContext;
import com.jd.blockchain.mocker.handler.MockerContractExeHandle;
import com.jd.blockchain.transaction.TxBuilder;

public class ContractProxy<T> implements InvocationHandler {

	private BlockchainIdentity identity;

	private MockerNodeContext mockerNodeContext;

	private T instance;

	private MockerContractExeHandle operationHandle;

	public ContractProxy(BlockchainIdentity identity, MockerNodeContext mockerNodeContext, T instance,
			MockerContractExeHandle operationHandle) {
		this.identity = identity;
		this.mockerNodeContext = mockerNodeContext;
		this.instance = instance;
		this.operationHandle = operationHandle;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 实际执行时，首先判断执行的是否是添加注解的方法
		if (!isExecuteContractMethod(method)) {
			return method.invoke(instance, args);
		}

		// 首先发送一次执行的请求
		TxBuilder txBuilder = mockerNodeContext.txBuilder();

		Class<?> contractInft = null;

		Class<?>[] instanceInfts = instance.getClass().getInterfaces();

		for (Class<?> instanceInft : instanceInfts) {
			if (instanceInft.isAnnotationPresent(Contract.class)) {
				contractInft = instanceInft;
				break;
			}
		}

		if (contractInft == null) {
			throw new IllegalStateException(
					"This object does not implement the interface for the @Contract annotation !!!");
		}

		// 生成代理类
		Object proxyInstance = txBuilder.contract(identity.getAddress().toBase58(), contractInft);
		// 代理方式执行一次
		method.invoke(proxyInstance, args);

		TransactionRequest txRequest = mockerNodeContext.txRequest(txBuilder);

		// 放入到Map中
		HashDigest txHash = txRequest.getTransactionContent().getHash();
		operationHandle.registerExecutorProxy(txHash, new ExecutorProxy(instance, method, args));

		// 提交该请求至整个区块链系统
		OperationResult[] operationResults = mockerNodeContext.txProcess(txRequest);
		if (operationResults == null || operationResults.length == 0) {
			return null;
		}
		OperationResult opResult = operationResults[0];

		// 处理返回值
		return BytesValueEncoding.decode(opResult.getResult(), method.getReturnType());
	}

	private boolean isExecuteContractMethod(Method method) {
		Annotation annotation = method.getAnnotation(ContractEvent.class);
		return annotation != null;
	}
}
