package test.com.jd.blockchain.intgr.contract;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;
import com.jd.blockchain.ledger.ContractBizContent;
import com.jd.blockchain.utils.Bytes;

import java.math.BigDecimal;

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
    void issue(ContractBizContent contractBizContent, String assetHolderAddress);

	/**
	 * 发行资产；
	 *            新发行的资产数量；
	 * @param assetHolderAddress
	 *            新发行的资产的持有账户；
	 */
	@ContractEvent(name = "issue-asset")
	void issue(ContractBizContent contractBizContent, String assetHolderAddress, long cashNumber);

	@ContractEvent(name = "issue-asset-2")
	void issue(Bytes bytes, String assetHolderAddress, long cashNumber);

    @ContractEvent(name = "issue-asset-3")
    void issue(Byte byteObj, String assetHolderAddress, long cashNumber);

	@ContractEvent(name = "issue-asset-4")
	void issue(Byte byteObj, String assetHolderAddress, Bytes cashNumber);
}