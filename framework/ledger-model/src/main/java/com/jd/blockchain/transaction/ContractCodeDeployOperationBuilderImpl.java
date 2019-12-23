package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;

public class ContractCodeDeployOperationBuilderImpl implements ContractCodeDeployOperationBuilder{
	
	@Override
	public ContractCodeDeployOperation deploy(BlockchainIdentity id, byte[] chainCode) {
		ContractCodeDeployOpTemplate op = new ContractCodeDeployOpTemplate(id, chainCode);
		return op;
	}

}
