package com.jd.blockchain.ledger.core;

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
@DataContract(code = DataCodes.ROLE_PRIVILEGE, name = "ROLE-PRIVILEGE")
public interface RolePrivilege {

	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	LedgerPrivilege getLedgerPrivilege();

	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	TransactionPrivilege getTransactionPrivilege();

}
