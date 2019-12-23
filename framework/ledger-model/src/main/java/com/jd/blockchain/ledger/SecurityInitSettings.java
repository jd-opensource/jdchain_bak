package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.DataCodes;

/**
 * 安全权限的初始化；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.SECURITY_INIT_SETTING)
public interface SecurityInitSettings {

	/**
	 * 角色列表；
	 * 
	 * @return
	 */
	@DataField(order = 0, refContract = true, list = true)
	RoleInitSettings[] getRoles();
	
	@DataField(order = 1, refContract = true, list = true)
	UserAuthInitSettings[] getUserAuthorizations();

}
