package test.com.jd.blockchain.ledger;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract(name="NORMAL-CONTRACT")
public interface NormalContract {

	/**
	 * 发行资产；
	 * 
	 * @param asset  资产代码；
	 * @param amount 本次发行的资产数量；
	 * @return 资产总量；
	 */
	@ContractEvent(name="issue")
	long issue(String asset, long amount);

	/**
	 * 获取资产总量；
	 * 
	 * @param asset
	 * @return
	 */
	@ContractEvent(name="get_amount")
	long getAmount(String asset);

	/**
	 * 获取资产余额；
	 * 
	 * @param address
	 * @param asset
	 * @return
	 */
	@ContractEvent(name="get_balance")
	long getBalance(String address, String asset);
	
	/**
	 * 向账户分配资产；
	 * 
	 * @param address
	 * @param asset
	 * @param amount
	 */
	@ContractEvent(name="assign")
	void assign(String address, String asset, int amount);

	/**
	 * 转移资产；
	 * 
	 * @param fromAddress
	 * @param toAddress
	 * @param asset
	 * @param amount
	 */
	@ContractEvent(name="transfer")
	void transfer(String fromAddress, String toAddress, String asset, long amount);
}
