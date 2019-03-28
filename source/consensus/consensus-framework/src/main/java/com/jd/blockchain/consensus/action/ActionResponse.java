package com.jd.blockchain.consensus.action;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.utils.ValueType;

@DataContract(code= TypeCodes.CONSENSUS_ACTION_RESPONSE)
public interface ActionResponse {

	@DataField(order=1, list=true, primitiveType= ValueType.INT8)
	byte[] getMessage();

    @DataField(order=2, primitiveType=ValueType.BOOLEAN)
	boolean getError();

    @DataField(order=3, primitiveType=ValueType.TEXT)
	String getErrorMessage();

	@DataField(order=4, primitiveType=ValueType.TEXT)
	String getErrorType();

}