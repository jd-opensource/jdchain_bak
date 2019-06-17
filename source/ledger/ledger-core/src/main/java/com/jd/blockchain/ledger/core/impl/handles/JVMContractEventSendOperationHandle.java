package com.jd.blockchain.ledger.core.impl.handles;

import static com.jd.blockchain.utils.BaseConstant.CONTRACT_SERVICE_PROVIDER;

import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.contract.engine.ContractEngine;
import com.jd.blockchain.contract.engine.ContractServiceProviders;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.ContractAccount;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.TransactionRequestContext;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;
import com.jd.blockchain.utils.concurrent.AsyncFuture;

public class JVMContractEventSendOperationHandle extends AbtractContractEventHandle {

	private static final ContractEngine JVM_ENGINE;

	static {
		JVM_ENGINE = ContractServiceProviders.getProvider(CONTRACT_SERVICE_PROVIDER).getEngine();
	}

	@Override
	protected ContractCode loadContractCode(ContractAccount contract) {
		ContractCode contractCode = JVM_ENGINE.getContract(contract.getAddress(), contract.getChaincodeVersion());
		if (contractCode == null) {
			// 装载合约；
			contractCode = JVM_ENGINE.setupContract(contract.getAddress(), contract.getChaincodeVersion(),
					contract.getChainCode());
		}
		return contractCode;
	}

//	@Override
//	public AsyncFuture<byte[]> asyncProcess(Operation op, LedgerDataSet newBlockDataset,
//			TransactionRequestContext requestContext, LedgerDataSet previousBlockDataset,
//			OperationHandleContext handleContext, LedgerService ledgerService) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
