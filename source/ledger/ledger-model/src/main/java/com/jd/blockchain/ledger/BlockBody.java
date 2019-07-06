package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

@DataContract(code= DataCodes.BLOCK_BODY)
public interface BlockBody extends  LedgerDataSnapshot{
	
	@DataField(order=2, primitiveType = PrimitiveType.BYTES)
	HashDigest getPreviousHash();

	@DataField(order=3, primitiveType = PrimitiveType.BYTES)
	HashDigest getLedgerHash();

	@DataField(order=4, primitiveType= PrimitiveType.INT64)
	long getHeight();

	@DataField(order=5, primitiveType = PrimitiveType.BYTES)
	HashDigest getTransactionSetHash();
	
	@DataField(order=6, primitiveType = PrimitiveType.INT64)
	long getTimestamp();
}
