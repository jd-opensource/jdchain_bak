package com.jd.blockchain.ledger.core.handles;

import org.springframework.stereotype.Service;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.TransactionRequestContext;

@Service
public class DataAccountRegisterOperationHandle implements OperationHandle {

	@Override
	public BytesValue process(Operation op, LedgerDataset dataset, TransactionRequestContext requestContext,
			LedgerDataset previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {
		DataAccountRegisterOperation dataAccountRegOp = (DataAccountRegisterOperation) op;
		BlockchainIdentity bid = dataAccountRegOp.getAccountID();

		//TODO: 校验用户身份；

		//TODO: 请求者应该提供数据账户的公钥签名，已确定注册的地址的唯一性；
		dataset.getDataAccountSet().register(bid.getAddress(), bid.getPubKey(), null);

		return null;
	}

//	@Override
//	public AsyncFuture<byte[]> asyncProcess(Operation op, LedgerDataSet newBlockDataset, TransactionRequestContext requestContext, LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {
//		return null;
//	}

	@Override
	public boolean support(Class<?> operationType) {
		return DataAccountRegisterOperation.class.isAssignableFrom(operationType);
	}

}
