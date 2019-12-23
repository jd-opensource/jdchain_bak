package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.Bytes;

@DataContract(code = DataCodes.SECURITY_USER_AUTH_INIT_SETTING)
public interface UserAuthInitSettings {

	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	Bytes getUserAddress();

	@DataField(order = 2, primitiveType = PrimitiveType.TEXT, list = true)
	String[] getRoles();

	@DataField(order = 3, refEnum =  true)
	RolesPolicy getPolicy();

}
