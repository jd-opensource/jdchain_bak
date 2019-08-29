package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.RolesConfigureOperation;

public interface RolesConfigurer {
	
	RolesConfigureOperation getOperation();
	
	RolePrivilegeConfigurer configure(String roleName);

}
