package test.com.jd.blockchain.intgr.contract;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

/**
 *  示例：一个“资产管理”智能合约；
 * 
 * @author huanghaiquan
 *
 */
@Contract
public interface AssetContract {

	/**
	 * 发行资产；
	 * 
	 * @param amount
	 *            新发行的资产数量；
	 * @param assetHolderAddress
	 *            新发行的资产的持有账户；
	 */
	@ContractEvent(name = "issue-asset")
	void issue(long amount, String assetHolderAddress);

	/**
	 * 转移资产
	 * 
	 * @param fromAddress
	 *            转出账户；
	 * @param toAddress
	 *            转入账户；
	 * @param amount
	 *            转移的资产数额；
	 */
	@ContractEvent(name = "transfer-asset")
	void transfer(String fromAddress, String toAddress, long amount);

}