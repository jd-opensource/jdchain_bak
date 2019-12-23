package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.UserAuthorizeOperation;

public interface UserAuthorizer extends UserAuthorize {
	
	UserAuthorizeOperation getOperation();
	
}
