package com.jd.blockchain.contract.samples;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.utils.Bytes;

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
	@ContractEvent(name = "issue-asset-0")
	void issue(@DataContract(code = DataCodes.TX_CONTENT_BODY) TransactionContentBody transactionContentBody,
			   @DataContract(code = DataCodes.CONTRACT_TEXT) String assetHolderAddress);

	/**
	 * issue asset;
	 * @param transactionContentBody
	 * @param assetHolderAddress
	 * @param cashNumber
	 */
	@ContractEvent(name = "issue-asset")
	public void issue(@DataContract(code = DataCodes.TX_CONTENT_BODY) TransactionContentBody transactionContentBody,
					  @DataContract(code = DataCodes.CONTRACT_TEXT) String assetHolderAddress,
					  @DataContract(code = DataCodes.CONTRACT_INT64) long cashNumber);

	/**
	 * Bytes can bring the byte[];
	 * @param bytes
	 * @param assetHolderAddress
	 * @param cashNumber
	 */
	@ContractEvent(name = "issue-asset-2")
	void issue(Bytes bytes,String assetHolderAddress, long cashNumber);

	@ContractEvent(name = "issue-asset-3")
	void issue(Byte bytes, String assetHolderAddress, long cashNumber);

	@ContractEvent(name = "issue-asset-4")
	void issue1(byte[] bytes, String assetHolderAddress, long cashNumber);
}