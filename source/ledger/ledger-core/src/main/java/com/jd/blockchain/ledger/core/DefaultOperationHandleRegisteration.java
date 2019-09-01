package com.jd.blockchain.ledger.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.core.handles.ContractCodeDeployOperationHandle;
import com.jd.blockchain.ledger.core.handles.DataAccountKVSetOperationHandle;
import com.jd.blockchain.ledger.core.handles.DataAccountRegisterOperationHandle;
import com.jd.blockchain.ledger.core.handles.JVMContractEventSendOperationHandle;
import com.jd.blockchain.ledger.core.handles.RolesConfigureOperationHandle;
import com.jd.blockchain.ledger.core.handles.UserAuthorizeOperationHandle;
import com.jd.blockchain.ledger.core.handles.UserRegisterOperationHandle;

@Component
public class DefaultOperationHandleRegisteration implements OperationHandleRegisteration {

	private static Map<Class<?>, OperationHandle> DEFAULT_HANDLES = new HashMap<>();

	private Map<Class<?>, OperationHandle> handles = new ConcurrentHashMap<>();

	private Map<Class<?>, OperationHandle> cacheMapping = new ConcurrentHashMap<>();

	static {
		addDefaultHandle(new RolesConfigureOperationHandle());
		addDefaultHandle(new UserAuthorizeOperationHandle());
		addDefaultHandle(new DataAccountKVSetOperationHandle());
		addDefaultHandle(new DataAccountRegisterOperationHandle());
		addDefaultHandle(new UserRegisterOperationHandle());
		addDefaultHandle(new ContractCodeDeployOperationHandle());
		addDefaultHandle(new JVMContractEventSendOperationHandle());
	}

	private static void addDefaultHandle(OperationHandle handle) {
		DEFAULT_HANDLES.put(handle.getOperationType(), handle);
	}

	/**
	 * 以最高优先级插入一个操作处理器；
	 * 
	 * @param handle
	 */
	public void registerHandle(OperationHandle handle) {
		handles.put(handle.getOperationType(), handle);
	}

	private OperationHandle getRegisteredHandle(Class<?> operationType) {
		OperationHandle hdl = handles.get(operationType);
		if (hdl == null) {
			for (Entry<Class<?>, OperationHandle> entry : handles.entrySet()) {
				if (entry.getKey().isAssignableFrom(operationType)) {
					hdl = entry.getValue();
				}
			}
		}
		return hdl;
	}

	private OperationHandle getDefaultHandle(Class<?> operationType) {
		OperationHandle hdl = DEFAULT_HANDLES.get(operationType);
		if (hdl == null) {
			for (Entry<Class<?>, OperationHandle> entry : DEFAULT_HANDLES.entrySet()) {
				if (entry.getKey().isAssignableFrom(operationType)) {
					hdl = entry.getValue();
				}
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
	public OperationHandle getHandle(Class<?> operationType) {
		OperationHandle hdl = cacheMapping.get(operationType);
		if (hdl != null) {
			return hdl;
		}
		hdl = getRegisteredHandle(operationType);
		if (hdl == null) {
			hdl = getDefaultHandle(operationType);
			if (hdl == null) {
				throw new LedgerException("Unsupported operation type[" + operationType.getName() + "]!");
			}
		}
		cacheMapping.put(operationType, hdl);
		return hdl;
	}

}
