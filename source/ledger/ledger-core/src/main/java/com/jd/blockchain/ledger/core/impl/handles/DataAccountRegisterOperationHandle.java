package com.jd.blockchain.ledger.core.impl.handles;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.TransactionRequestContext;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;

import com.jd.blockchain.utils.concurrent.AsyncFuture;
import org.springframework.stereotype.Service;

@Service
public class DataAccountRegisterOperationHandle implements OperationHandle {

	@Override
	public byte[] process(Operation op, LedgerDataSet dataset, TransactionRequestContext requestContext,
			LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {
		DataAccountRegisterOperation dataAccountRegOp = (DataAccountRegisterOperation) op;
		BlockchainIdentity bid = dataAccountRegOp.getAccountID();

		//TODO: 校验用户身份；

		//TODO: 请求者应该提供数据账户的公钥签名，已确定注册的地址的唯一性；
		dataset.getDataAccountSet().register(bid.getAddress(), bid.getPubKey(), null);

		return null;
	}

	@Override
	public AsyncFuture<byte[]> asyncProcess(Operation op, LedgerDataSet newBlockDataset, TransactionRequestContext requestContext, LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {
		return null;
	}

	@Override
	public boolean support(Class<?> operationType) {
		return DataAccountRegisterOperation.class.isAssignableFrom(operationType);
	}

}
