package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.RolesConfigureOperation;

public interface RolesConfigurer extends RolesConfigure {
	
	RolesConfigureOperation getOperation();

}
