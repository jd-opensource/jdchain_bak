package com.jd.blockchain.ledger;

/**
 * 权限类型；
 * 
 * @author huanghaiquan
 *
 */
public enum PrivilegeType {
	
	/**
	 * 账户注册；
	 */
	ACCOUNT_REGISTER(1),
	
	/**
	 * 账户权限配置；
	 */
	PRIVILEGE_CONFIG(2),
	
	/**
	 * 状态数据写入；
	 */
	STATE_WRITE(4),
	
	/**
	 * 合约应用部署；
	 */
	CONTRACT_APP_DEPLOY(8),
	
	/**
	 * 合约应用调用；
	 */
	CONTRACT_APP_INVOKE(16);
	
	public final int CODE;
	
	private PrivilegeType(int code) {
		this.CODE = code;
	}
	
}
