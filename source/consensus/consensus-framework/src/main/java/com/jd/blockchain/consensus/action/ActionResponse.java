package com.jd.blockchain.consensus.action;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.CONSENSUS_ACTION_RESPONSE)
public interface ActionResponse {

	@DataField(order=1, list=true, primitiveType= PrimitiveType.INT8)
	byte[] getMessage();

    @DataField(order=2, primitiveType=PrimitiveType.BOOLEAN)
	boolean getError();

    @DataField(order=3, primitiveType=PrimitiveType.TEXT)
	String getErrorMessage();

	@DataField(order=4, primitiveType=PrimitiveType.TEXT)
	String getErrorType();

}