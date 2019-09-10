package com.jd.blockchain.transaction;

public class SecurityOperationBuilderImpl implements SecurityOperationBuilder{

	@Override
	public RolesConfigurer roles() {
		return new RolesConfigureOpTemplate();
	}

	@Override
	public UserAuthorizer authorziations() {
		return new UserAuthorizeOpTemplate();
	}

}
