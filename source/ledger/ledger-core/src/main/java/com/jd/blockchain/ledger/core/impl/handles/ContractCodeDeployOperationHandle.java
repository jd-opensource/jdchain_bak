package com.jd.blockchain.ledger.core.impl.handles;

import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.TransactionRequestContext;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;

import org.springframework.stereotype.Service;

@Service
public class ContractCodeDeployOperationHandle implements OperationHandle {

	@Override
	public void process(Operation op, LedgerDataSet dataset, TransactionRequestContext requestContext,
			LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {
		ContractCodeDeployOperation contractOP = (ContractCodeDeployOperation) op;
		// TODO: 校验合约代码的正确性；
		
		// TODO: 请求者应该提供合约账户的公钥签名，已确定注册的地址的唯一性；
		dataset.getContractAccountSet().deploy(contractOP.getContractID().getAddress(),
				contractOP.getContractID().getPubKey(), contractOP.getAddressSignature(), contractOP.getChainCode());
	}

	@Override
	public boolean support(Class<?> operationType) {
		return ContractCodeDeployOperation.class.isAssignableFrom(operationType);
	}

}
