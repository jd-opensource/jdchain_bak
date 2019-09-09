package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.utils.Bytes;

public interface UserAuthorize {

	UserRolesAuthorizer forUser(BlockchainIdentity... userId);

	UserRolesAuthorizer forUser(Bytes... userAddress);

}
