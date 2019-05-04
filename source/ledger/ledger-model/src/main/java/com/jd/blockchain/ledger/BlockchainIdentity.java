package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.Bytes;

@DataContract(code= DataCodes.BLOCK_CHAIN_IDENTITY)
public interface BlockchainIdentity {

	@DataField(order = 1, primitiveType = DataType.BYTES)
	Bytes getAddress();

	@DataField(order = 2, primitiveType=DataType.BYTES)
	PubKey getPubKey();

}