package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;

@DataContract(code= DataCodes.USER)
public interface UserInfo extends AccountHeader {
	
	PubKey getDataPubKey();
	
}
