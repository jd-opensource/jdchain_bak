package com.jd.blockchain.ledger;

/**
 * 权限类型；
 * 
 * @author huanghaiquan
 *
 */
public enum PermissionType {

	/**
	 * 账户权限配置；
	 */
	SET_PRIVILEGE(1),

	/**
	 * 注册参与方；
	 */
	REG_PARTICIPANT(2),

	/**
	 * 配置账本；包括除了{@link #SET_PRIVILEGE}、 {@link #REG_PARTICIPANT} 之外的其它账本设置，例如：设置密码参数、共识参数等；
	 */
	CONFIG_LEDGER(4),

	/**
	 * 用户注册；
	 */
	REG_USER(8),

	/**
	 * 注册数据账户；
	 */
	REG_DATA_ACCOUNT(16),

	/**
	 * 部署新的合约代码；
	 */
	DEPLOY_CONTRACT(32),

	/**
	 * 写入用户信息；
	 */
	SET_USER(1024),
	
	/**
	 * 写入数据；
	 */
	SET_DATA(2048),
	
	/**
	 * 写入数据；
	 */
	INVOKE_CONTRACT(4096),
	
	/**
	 * 升级合约代码；
	 */
	UPDATE_CONTRACT(8192);


	public final int CODE;

	private PermissionType(int code) {
		this.CODE = code;
	}

}
