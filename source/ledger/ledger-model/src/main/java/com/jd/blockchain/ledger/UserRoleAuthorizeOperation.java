package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.Bytes;

/**
 * 角色配置操作；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.TX_OP_USER_ROLE_AUTHORIZE)
public interface UserRoleAuthorizeOperation extends Operation {

	@DataField(order = 2, refContract = true, list = true)
	UserRoleAuthEntry[] getUserRoleAuthorizations();

	@DataContract(code = DataCodes.TX_OP_USER_ROLE_AUTHORIZE_ENTRY)
	public static interface UserRoleAuthEntry {

		@DataField(order = 0, primitiveType = PrimitiveType.BYTES)
		Bytes getUserAddress();

		@DataField(order = 2, primitiveType = PrimitiveType.INT64)
		long getExplectedVersion();
		
		/**
		 * 要更新的多角色权限策略；
		 * @return
		 */
		RolesPolicy getRolesPolicy();

		/**
		 * 授权的角色清单；
		 * 
		 * @return
		 */
		@DataField(order = 1, primitiveType = PrimitiveType.TEXT)
		String[] getAuthRoles();
		
		/**
		 * 取消授权的角色清单；
		 * 
		 * @return
		 */
		@DataField(order = 1, primitiveType = PrimitiveType.TEXT)
		String[] getUnauthRoles();

	}
}
