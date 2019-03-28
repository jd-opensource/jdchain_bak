package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.utils.ValueType;

@DataContract(code= TypeCodes.BLOCK_BODY)
public interface BlockBody extends  LedgerDataSnapshot{
	
	@DataField(order=2, primitiveType = ValueType.BYTES)
	HashDigest getPreviousHash();

	@DataField(order=3, primitiveType = ValueType.BYTES)
	HashDigest getLedgerHash();

	@DataField(order=4, primitiveType= ValueType.INT64)
	long getHeight();

	@DataField(order=5, primitiveType = ValueType.BYTES)
	HashDigest getTransactionSetHash();
}
