package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

@DataContract(code=DataCodes.DATA_SNAPSHOT)
public interface LedgerDataSnapshot {

	@DataField(order=1, primitiveType = PrimitiveType.BYTES)
	HashDigest getAdminAccountHash();

	@DataField(order=2, primitiveType = PrimitiveType.BYTES)
	HashDigest getUserAccountSetHash();
	
	@DataField(order=3, primitiveType = PrimitiveType.BYTES)
	HashDigest getDataAccountSetHash();
	
	@DataField(order=4, primitiveType = PrimitiveType.BYTES)
	HashDigest getContractAccountSetHash();

//	HashDigest getUserPrivilegeHash();

//	HashDigest getDataPrivilegeHash();

//	HashDigest getContractPrivilegeHash();
	

}