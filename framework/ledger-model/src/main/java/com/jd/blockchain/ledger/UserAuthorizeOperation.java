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
@DataContract(code = DataCodes.TX_OP_USER_ROLES_AUTHORIZE)
public interface UserAuthorizeOperation extends Operation {

	@DataField(order = 2, refContract = true, list = true)
	UserRolesEntry[] getUserRolesAuthorizations();

	@DataContract(code = DataCodes.TX_OP_USER_ROLE_AUTHORIZE_ENTRY)
	public static interface UserRolesEntry {

		/**
		 * 用户地址；
		 * 
		 * @return
		 */
		@DataField(order = 0, primitiveType = PrimitiveType.BYTES, list = true)
		Bytes[] getUserAddresses();

		/**
		 * 要更新的多角色权限策略；
		 * 
		 * @return
		 */
		@DataField(order = 2, refEnum = true)
		RolesPolicy getPolicy();

		/**
		 * 授权的角色清单；
		 * 
		 * @return
		 */
		@DataField(order = 3, primitiveType = PrimitiveType.TEXT, list = true)
		String[] getAuthorizedRoles();

		/**
		 * 取消授权的角色清单；
		 * 
		 * @return
		 */
		@DataField(order = 4, primitiveType = PrimitiveType.TEXT, list = true)
		String[] getUnauthorizedRoles();

	}
}
