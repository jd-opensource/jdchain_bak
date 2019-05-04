package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

@DataContract(code= DataCodes.BLOCK_BODY)
public interface BlockBody extends  LedgerDataSnapshot{
	
	@DataField(order=2, primitiveType = DataType.BYTES)
	HashDigest getPreviousHash();

	@DataField(order=3, primitiveType = DataType.BYTES)
	HashDigest getLedgerHash();

	@DataField(order=4, primitiveType= DataType.INT64)
	long getHeight();

	@DataField(order=5, primitiveType = DataType.BYTES)
	HashDigest getTransactionSetHash();
}
