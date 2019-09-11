package com.jd.blockchain.ledger.core.handles;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.TransactionRequestExtension;

public class LedgerInitOperationHandle implements OperationHandle {

	@Override
	public Class<?> getOperationType() {
		return LedgerInitOperation.class;
	}

	@Override
	public BytesValue process(Operation op, LedgerDataset newBlockDataset, TransactionRequestExtension requestContext,
			LedgerQuery ledger,OperationHandleContext handleContext) {
		// 对初始化操作不需要做任何处理；
		return null;
	}

}
