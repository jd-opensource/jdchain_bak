package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * 表示赋予角色的特权码；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.PRIVILEGE_SET, name = "PRIVILEGE-SET")
public interface PrivilegeSet {

	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	LedgerPrivilege getLedgerPrivilege();

	@DataField(order = 2, primitiveType = PrimitiveType.BYTES)
	TransactionPrivilege getTransactionPrivilege();

}
