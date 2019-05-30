package com.jd.blockchain.mocker.handler;

import com.jd.blockchain.ledger.core.LedgerException;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.impl.OperationHandleRegisteration;
import com.jd.blockchain.ledger.core.impl.handles.*;

import java.util.ArrayList;
import java.util.List;

public class MockerOperationHandleRegister implements OperationHandleRegisteration {

	private List<OperationHandle> opHandles = new ArrayList<>();
	
	public MockerOperationHandleRegister() {
		initDefaultHandles();
	}

	/**
	 * 针对不采用bean依赖注入的方式来处理;
	 */
	private void initDefaultHandles(){
		opHandles.add(new DataAccountKVSetOperationHandle());
		opHandles.add(new DataAccountRegisterOperationHandle());
		opHandles.add(new UserRegisterOperationHandle());
		opHandles.add(new ContractCodeDeployOperationHandle());
//		opHandles.add(new ContractEventSendOperationHandle());
	}

	public List<OperationHandle> getOpHandles() {
		return opHandles;
	}

	public void registerHandler(OperationHandle operationHandle) {
		opHandles.add(operationHandle);
	}

	public void removeHandler(OperationHandle operationHandle) {
		opHandles.remove(operationHandle);
	}

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
