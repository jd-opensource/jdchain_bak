package com.jd.blockchain.consensus.action;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.CONSENSUS_ACTION_REQUEST)
public interface ActionRequest {

	@DataField(order=1, list=true, primitiveType= PrimitiveType.INT8)
	byte[] getGroupId();

	@DataField(order=2, primitiveType=PrimitiveType.TEXT)
	String getHandleType();

	@DataField(order=3, primitiveType=PrimitiveType.TEXT)
	String getHandleMethod();
	
//	String getMessageType();

	@DataField(order=4, list=true, primitiveType= PrimitiveType.INT8)
	byte[] getMessageBody();

	@DataField(order=5, primitiveType= PrimitiveType.TEXT)
	String getTransactionType();
	
//	String getReponseType();
	
}
