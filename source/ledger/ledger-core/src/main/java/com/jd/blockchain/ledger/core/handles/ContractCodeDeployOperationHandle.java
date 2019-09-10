package com.jd.blockchain.ledger.core.handles;

import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.MultiIDsPolicy;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.SecurityContext;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionRequestExtension;

public class ContractCodeDeployOperationHandle extends AbstractLedgerOperationHandle<ContractCodeDeployOperation> {
	public ContractCodeDeployOperationHandle() {
		super(ContractCodeDeployOperation.class);
	}

	@Override
	protected void doProcess(ContractCodeDeployOperation op, LedgerDataset newBlockDataset,
			TransactionRequestExtension requestContext, LedgerQuery ledger,
			OperationHandleContext handleContext) {
		// TODO: 校验合约代码的正确性；

		// TODO: 请求者应该提供合约账户的公钥签名，以确保注册人对注册的地址和公钥具有合法的使用权；

		// 权限校验；
		SecurityPolicy securityPolicy = SecurityContext.getContextUsersPolicy();
		securityPolicy.checkEndpointPermission(LedgerPermission.UPGRADE_CONTRACT, MultiIDsPolicy.AT_LEAST_ONE);

		// 操作账本；
		ContractCodeDeployOperation contractOP = (ContractCodeDeployOperation) op;
		newBlockDataset.getContractAccountset().deploy(contractOP.getContractID().getAddress(),
				contractOP.getContractID().getPubKey(), contractOP.getAddressSignature(), contractOP.getChainCode());
	}

}
