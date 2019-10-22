package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;

@DataContract(code= DataCodes.USER_ACCOUNT_HEADER)
public interface UserAccountHeader extends BlockchainIdentity {
	
	PubKey getDataPubKey();
	
}
