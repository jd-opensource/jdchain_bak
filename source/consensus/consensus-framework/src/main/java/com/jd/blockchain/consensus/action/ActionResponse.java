package com.jd.blockchain.consensus.action;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.CONSENSUS_ACTION_RESPONSE)
public interface ActionResponse {

	@DataField(order=1, list=true, primitiveType= DataType.INT8)
	byte[] getMessage();

    @DataField(order=2, primitiveType=DataType.BOOLEAN)
	boolean getError();

    @DataField(order=3, primitiveType=DataType.TEXT)
	String getErrorMessage();

	@DataField(order=4, primitiveType=DataType.TEXT)
	String getErrorType();

}