package com.jd.blockchain.ledger.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.LedgerException;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.impl.handles.ContractCodeDeployOperationHandle;
import com.jd.blockchain.ledger.core.impl.handles.ContractEventSendOperationHandle;
import com.jd.blockchain.ledger.core.impl.handles.DataAccountKVSetOperationHandle;
import com.jd.blockchain.ledger.core.impl.handles.DataAccountRegisterOperationHandle;
import com.jd.blockchain.ledger.core.impl.handles.UserRegisterOperationHandle;

@Component
public class DefaultOperationHandleRegisteration implements OperationHandleRegisteration {

	private List<OperationHandle> opHandles = new ArrayList<>();
	
	
//	private UserRegisterOperationHandle userRegHandle;
//	
//	private DataAccountRegisterOperationHandle dataAccRegHandle;
//	
//	private DataAccountKVSetOperationHandle dataAccKVSetHandle;
//	
//	private ContractCodeDeployOperationHandle contractDplHandle;
//	
//	private ContractEventSendOperationHandle contractEvtSendHandle;
	
	public DefaultOperationHandleRegisteration() {
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
		opHandles.add(new ContractEventSendOperationHandle());
	}
	
//	@PostConstruct
//	private void init() {
//		opHandles.add(dataAccKVSetHandle);
//		opHandles.add(dataAccRegHandle);
//		opHandles.add(userRegHandle);
//		opHandles.add(contractDplHandle);
//		opHandles.add(contractEvtSendHandle);
//	}

	/* (non-Javadoc)
	 * @see com.jd.blockchain.ledger.core.impl.OperationHandleRegisteration#getHandle(java.lang.Class)
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
