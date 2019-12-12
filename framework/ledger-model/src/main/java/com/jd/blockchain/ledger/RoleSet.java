package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * 角色集；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.ROLE_SET)
public interface RoleSet {

	@DataField(order = 1, refEnum = true)
	RolesPolicy getPolicy();

	@DataField(order = 2, primitiveType = PrimitiveType.TEXT, list = true)
	String[] getRoles();

}
