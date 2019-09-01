package com.jd.blockchain.ledger.core.handles;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.MultiIdsPolicy;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.SecurityContext;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionRequestExtension;

/**
 * 执行直接账本操作的处理类；
 * 
 * @author huanghaiquan
 *
 * @param <T>
 */
public abstract class AbstractLedgerOperationHandle<T extends Operation> implements OperationHandle {

	static {
		DataContractRegistry.register(BytesValue.class);
	}

	private final Class<T> SUPPORTED_OPERATION_TYPE;

	public AbstractLedgerOperationHandle(Class<T> supportedOperationType) {
		this.SUPPORTED_OPERATION_TYPE = supportedOperationType;
	}

//	@Override
//	public final boolean support(Class<?> operationType) {
//		return SUPPORTED_OPERATION_TYPE.isAssignableFrom(operationType);
//	}
	
	@Override
	public Class<?> getOperationType() {
		return SUPPORTED_OPERATION_TYPE;
	}

	@Override
	public final BytesValue process(Operation op, LedgerDataset newBlockDataset,
			TransactionRequestExtension requestContext, LedgerDataset previousBlockDataset,
			OperationHandleContext handleContext, LedgerService ledgerService) {
		// 权限校验；
		SecurityPolicy securityPolicy = SecurityContext.getContextUsersPolicy();
		securityPolicy.checkEndpoints(TransactionPermission.DIRECT_OPERATION, MultiIdsPolicy.AT_LEAST_ONE);

		// 操作账本；
		@SuppressWarnings("unchecked")
		T concretedOp = (T) op;
		doProcess(concretedOp, newBlockDataset, requestContext, previousBlockDataset, handleContext, ledgerService);

		// 账本操作没有返回值；
		return null;
	}

	/**
	 * @param op
	 * @param newBlockDataset
	 * @param requestContext
	 * @param previousBlockDataset
	 * @param handleContext
	 * @param ledgerService
	 */
	protected abstract void doProcess(T op, LedgerDataset newBlockDataset, TransactionRequestExtension requestContext,
			LedgerDataset previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService);
}
