package com.jd.blockchain.ledger.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.ledger.core.handles.*;
import org.springframework.stereotype.Component;

import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.transaction.ContractCodeDeployOpTemplate;
import com.jd.blockchain.transaction.ContractEventSendOpTemplate;
import com.jd.blockchain.transaction.DataAccountKVSetOpTemplate;
import com.jd.blockchain.transaction.DataAccountRegisterOpTemplate;
import com.jd.blockchain.transaction.LedgerInitOpTemplate;
import com.jd.blockchain.transaction.RolesConfigureOpTemplate;
import com.jd.blockchain.transaction.UserAuthorizeOpTemplate;
import com.jd.blockchain.transaction.UserRegisterOpTemplate;

@Component
public class DefaultOperationHandleRegisteration implements OperationHandleRegisteration {

	private static Map<Class<?>, OperationHandle> DEFAULT_HANDLES = new HashMap<>();

	private Map<Class<?>, OperationHandle> handles = new ConcurrentHashMap<>();

	static {
		registerDefaultHandle(new LedgerInitOperationHandle());

		registerDefaultHandle(new RolesConfigureOperationHandle());

		registerDefaultHandle(new UserAuthorizeOperationHandle());

		registerDefaultHandle(new UserRegisterOperationHandle());

		registerDefaultHandle(new DataAccountKVSetOperationHandle());

		registerDefaultHandle(new DataAccountRegisterOperationHandle());

		registerDefaultHandle(new ContractCodeDeployOperationHandle());

		registerDefaultHandle(new JVMContractEventSendOperationHandle());

		registerDefaultHandle(new ParticipantRegisterOperationHandle());

		registerDefaultHandle(new ParticipantStateUpdateOperationHandle());
	}

	private static void registerDefaultHandle(OperationHandle handle) {
		DEFAULT_HANDLES.put(handle.getOperationType(), handle);
	}

	/**
	 * 注册操作处理器；此方法将覆盖默认的操作处理器配置；
	 * 
	 * @param handle
	 */
	public void registerHandle(OperationHandle handle) {
		List<Class<?>> opTypes = new ArrayList<Class<?>>();
		for (Class<?> opType : handles.keySet()) {
			if (opType.isAssignableFrom(handle.getOperationType())) {
				opTypes.add(opType);
			}
		}

		for (Class<?> opType : opTypes) {
			handles.put(opType, handle);
		}
		handles.put(handle.getOperationType(), handle);
	}

	private OperationHandle getRegisteredHandle(Class<?> operationType) {
		OperationHandle hdl = handles.get(operationType);
		if (hdl == null) {
			hdl = DEFAULT_HANDLES.get(operationType);
			
			//按“操作类型”的继承关系匹配；
			if (hdl == null) {
				for (Class<?> opType : handles.keySet()) {
					if (opType.isAssignableFrom(operationType)) {
						hdl = handles.get(opType);
						break;
					}
				}
			}
			
			if (hdl == null) {
				for (Class<?> opType : DEFAULT_HANDLES.keySet()) {
					if (opType.isAssignableFrom(operationType)) {
						hdl = DEFAULT_HANDLES.get(opType);
						break;
					}
				}
			}
			
			if (hdl != null) {
				handles.put(operationType, hdl);
			}
		}
		return hdl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jd.blockchain.ledger.core.impl.OperationHandleRegisteration#getHandle(
	 * java.lang.Class)
	 */
	@Override
	public OperationHandle getHandle(Class<? extends Operation> operationType) {
		OperationHandle hdl = getRegisteredHandle(operationType);
		if (hdl == null) {
			throw new LedgerException("Unsupported operation type[" + operationType.getName() + "]!");
		}
		return hdl;
	}

	private static class OpHandleStub {

		private Class<? extends Operation> operationType;

		private OperationHandle operationHandle;

	}
}
