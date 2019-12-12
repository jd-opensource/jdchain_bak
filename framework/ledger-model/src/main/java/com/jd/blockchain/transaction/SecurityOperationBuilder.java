package com.jd.blockchain.transaction;

public interface SecurityOperationBuilder {

	/**
	 * 配置角色；
	 * 
	 * @return
	 */
	RolesConfigurer roles();

	/**
	 * 授权用户；
	 * 
	 * @return
	 */
	UserAuthorizer authorziations();

}
