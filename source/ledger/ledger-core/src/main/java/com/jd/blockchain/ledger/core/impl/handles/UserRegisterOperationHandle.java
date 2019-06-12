package com.jd.blockchain.ledger.core.impl.handles;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.TransactionRequestContext;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;

public class UserRegisterOperationHandle implements OperationHandle {

	@Override
	public byte[] process(Operation op, LedgerDataSet dataset, TransactionRequestContext requestContext,
			LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {
		UserRegisterOperation userRegOp = (UserRegisterOperation) op;
		BlockchainIdentity bid = userRegOp.getUserID();
		dataset.getUserAccountSet().register(bid.getAddress(), bid.getPubKey());

		// No return value;
		return null;
	}

	@Override
	public boolean support(Class<?> operationType) {
		return UserRegisterOperation.class.isAssignableFrom(operationType);
	}

}
