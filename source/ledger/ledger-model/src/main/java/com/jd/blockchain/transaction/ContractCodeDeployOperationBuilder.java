package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;

public interface ContractCodeDeployOperationBuilder {

	/**
	 * 部署合约；
	 * 
	 * @param id
	 *            区块链身份；
	 * @param chainCode
	 *            合约应用的字节代码；
	 * @return
	 */
	ContractCodeDeployOperation deploy(BlockchainIdentity id, byte[] chainCode);

}
