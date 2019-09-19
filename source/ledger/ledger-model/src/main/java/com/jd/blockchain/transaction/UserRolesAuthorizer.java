package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.RolesPolicy;

public interface UserRolesAuthorizer extends UserAuthorize {
	
	UserRolesAuthorizer authorize(String... roles);

	UserRolesAuthorizer unauthorize(String... roles);

	UserRolesAuthorizer setPolicy(RolesPolicy rolePolicy);

}
