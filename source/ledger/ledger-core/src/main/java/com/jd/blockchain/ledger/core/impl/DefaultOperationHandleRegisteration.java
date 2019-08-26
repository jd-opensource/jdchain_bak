package com.jd.blockchain.ledger.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.ledger.core.impl.handles.*;
import org.springframework.stereotype.Component;

import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.core.OperationHandle;

@Component
public class DefaultOperationHandleRegisteration implements OperationHandleRegisteration {

	private List<OperationHandle> opHandles = new ArrayList<>();

	public DefaultOperationHandleRegisteration() {
		initDefaultHandles();
	}

	/**
	 * 针对不采用bean依赖注入的方式来处理;
	 */
	private void initDefaultHandles() {
		opHandles.add(new DataAccountKVSetOperationHandle());
		opHandles.add(new DataAccountRegisterOperationHandle());
		opHandles.add(new UserRegisterOperationHandle());
		opHandles.add(new ParticipantRegisterOperationHandle());
		opHandles.add(new ContractCodeDeployOperationHandle());
		opHandles.add(new JVMContractEventSendOperationHandle());
	}

	/**
	 * 以最高优先级插入一个操作处理器；
	 * 
	 * @param handle
	 */
	public void insertAsTopPriority(OperationHandle handle) {
		opHandles.remove(handle);
		opHandles.add(0, handle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jd.blockchain.ledger.core.impl.OperationHandleRegisteration#getHandle(
	 * java.lang.Class)
	 */
	@Override
	public OperationHandle getHandle(Class<?> operationType) {
		for (OperationHandle handle : opHandles) {
			if (handle.support(operationType)) {
				return handle;
			}
		}
		throw new LedgerException("Unsupported operation type[" + operationType.getName() + "]!");
	}

}
