package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.ValueType;

@DataContract(code=TypeCodes.DATA_SNAPSHOT)
public interface LedgerDataSnapshot {

	@DataField(order=1, primitiveType = ValueType.BYTES)
	HashDigest getAdminAccountHash();

	@DataField(order=2, primitiveType = ValueType.BYTES)
	HashDigest getUserAccountSetHash();
	
	@DataField(order=3, primitiveType = ValueType.BYTES)
	HashDigest getDataAccountSetHash();
	
	@DataField(order=4, primitiveType = ValueType.BYTES)
	HashDigest getContractAccountSetHash();

//	HashDigest getUserPrivilegeHash();

//	HashDigest getDataPrivilegeHash();

//	HashDigest getContractPrivilegeHash();
	

}