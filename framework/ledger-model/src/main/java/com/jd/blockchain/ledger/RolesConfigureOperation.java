package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * 角色配置操作；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.TX_OP_ROLE_CONFIGURE)
public interface RolesConfigureOperation extends Operation {

	@DataField(order = 2, refContract = true, list = true)
	RolePrivilegeEntry[] getRoles();

	@DataContract(code = DataCodes.TX_OP_ROLE_CONFIGURE_ENTRY)
	public static interface RolePrivilegeEntry {

		@DataField(order = 1, primitiveType = PrimitiveType.TEXT)
		String getRoleName();

		@DataField(order = 2, refEnum = true, list = true)
		LedgerPermission[] getEnableLedgerPermissions();

		@DataField(order = 3, refEnum = true, list = true)
		LedgerPermission[] getDisableLedgerPermissions();

		@DataField(order = 4, refEnum = true, list = true)
		TransactionPermission[] getEnableTransactionPermissions();

		@DataField(order = 5, refEnum = true, list = true)
		TransactionPermission[] getDisableTransactionPermissions();

	}
}
