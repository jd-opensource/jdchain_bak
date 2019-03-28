package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.crypto.PubKey;

@DataContract(code= TypeCodes.USER)
public interface UserInfo extends AccountHeader {
	
	PubKey getDataPubKey();
	
}
