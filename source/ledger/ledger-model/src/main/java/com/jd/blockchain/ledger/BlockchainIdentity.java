package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ValueType;

@DataContract(code= TypeCodes.BLOCK_CHAIN_IDENTITY)
public interface BlockchainIdentity {

	@DataField(order = 1, primitiveType = ValueType.BYTES)
	Bytes getAddress();

	@DataField(order = 2, primitiveType=ValueType.BYTES)
	PubKey getPubKey();

}