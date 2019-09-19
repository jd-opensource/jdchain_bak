package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

@DataContract(code = DataCodes.MERKLE_SNAPSHOT)
public interface MerkleSnapshot {

	@DataField(order = 0, primitiveType = PrimitiveType.BYTES)
	HashDigest getRootHash();

}
