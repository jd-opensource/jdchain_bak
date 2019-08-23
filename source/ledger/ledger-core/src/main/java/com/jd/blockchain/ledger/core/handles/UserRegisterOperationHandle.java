package com.jd.blockchain.ledger.core.handles;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.TransactionRequestContext;
import com.jd.blockchain.utils.Bytes;


public class UserRegisterOperationHandle implements OperationHandle {

	@Override
	public BytesValue process(Operation op, LedgerDataset dataset, TransactionRequestContext requestContext,
			LedgerDataset previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {


		UserRegisterOperation userRegOp = (UserRegisterOperation) op;
		BlockchainIdentity bid = userRegOp.getUserID();

		Bytes userAddress = bid.getAddress();

		dataset.getUserAccountSet().register(userAddress, bid.getPubKey());

		return null;
	}

	@Override
	public boolean support(Class<?> operationType) {
		return UserRegisterOperation.class.isAssignableFrom(operationType);
	}

}
