package test.com.jd.blockchain.ledger.core;

public interface TestContractImpl {

	/**
	 * 发行资产；
	 * 
	 * @param asset  资产代码；
	 * @param amount 本次发行的资产数量；
	 * @return 资产总量；
	 */
	long issue(String asset, long amount);

	/**
	 * 获取资产总量；
	 * 
	 * @param asset
	 * @return
	 */
	long getAmount(String asset);

	/**
	 * 获取资产余额；
	 * 
	 * @param address
	 * @param asset
	 * @return
	 */
	long getBalance(String address, String asset);
	
	/**
	 * 向账户分配资产；
	 * 
	 * @param address
	 * @param asset
	 * @param amount
	 */
	void assign(String address, String asset, int amount);

	/**
	 * 转移资产；
	 * 
	 * @param fromAddress
	 * @param toAddress
	 * @param asset
	 * @param amount
	 */
	void transfer(String fromAddress, String toAddress, String asset, long amount);
}
