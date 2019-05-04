package com.jd.blockchain.consensus.action;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.CONSENSUS_ACTION_REQUEST)
public interface ActionRequest {

	@DataField(order=1, list=true, primitiveType= DataType.INT8)
	byte[] getGroupId();

	@DataField(order=2, primitiveType=DataType.TEXT)
	String getHandleType();

	@DataField(order=3, primitiveType=DataType.TEXT)
	String getHandleMethod();
	
//	String getMessageType();

	@DataField(order=4, list=true, primitiveType= DataType.INT8)
	byte[] getMessageBody();

	@DataField(order=5, primitiveType= DataType.TEXT)
	String getTransactionType();
	
//	String getReponseType();
	
}
