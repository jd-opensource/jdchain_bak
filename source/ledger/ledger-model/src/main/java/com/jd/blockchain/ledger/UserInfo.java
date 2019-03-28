package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.crypto.asymmetric.PubKey;

@DataContract(code= TypeCodes.USER)
public interface UserInfo extends AccountHeader {
	
	PubKey getDataPubKey();
	
}
