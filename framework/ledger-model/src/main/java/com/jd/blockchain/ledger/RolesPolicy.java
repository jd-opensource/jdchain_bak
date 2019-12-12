package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * 多角色策略；<br>
 * 
 * 表示如何处理一个对象被赋予多个角色时的综合权限；
 * 
 * @author huanghaiquan
 *
 */
@EnumContract(code = DataCodes.ENUM_MULTI_ROLES_POLICY, name = "USER-ROLE-POLICY")
public enum RolesPolicy {

	/**
	 * 合并权限；<br>
	 * 
	 * 综合权限是所有角色权限的并集，即任何一个角色的权限都被继承；
	 */
	UNION((byte) 0),

	/**
	 * 交叉权限；<br>
	 * 
	 * 综合权限是所有角色权限的交集，即只有全部角色共同拥有的权限才会被继承；
	 */
	INTERSECT((byte) 1);

	@EnumField(type = PrimitiveType.INT8)
	public final byte CODE;

	private RolesPolicy(byte code) {
		this.CODE = code;
	}

}
