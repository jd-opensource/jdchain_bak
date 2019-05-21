package com.jd.blockchain.contract.samples;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.ledger.CONTRACT_TEXT;
import com.jd.blockchain.ledger.TransactionContentBody;

/**
 *  示例：一个“资产管理”智能合约；
 * 
 * @author zhaogw
 */
@Contract
public interface AssetContract2 {

	/**
	 * 发行资产；
	 *            新发行的资产数量；
	 * @param assetHolderAddress
	 *            新发行的资产的持有账户；
	 */
	@ContractEvent(name = "issue-asset")
	void issue(@DataContract(code = DataCodes.TX_CONTENT_BODY) TransactionContentBody transactionContentBody,
			   @DataContract(code = DataCodes.CONTRACT_TEXT) String assetHolderAddress);
}