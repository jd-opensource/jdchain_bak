package com.jd.blockchain.consensus.action;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.utils.ValueType;

@DataContract(code= TypeCodes.CONSENSUS_ACTION_REQUEST)
public interface ActionRequest {

	@DataField(order=1, list=true, primitiveType= ValueType.INT8)
	byte[] getGroupId();

	@DataField(order=2, primitiveType=ValueType.TEXT)
	String getHandleType();

	@DataField(order=3, primitiveType=ValueType.TEXT)
	String getHandleMethod();
	
//	String getMessageType();

	@DataField(order=4, list=true, primitiveType= ValueType.INT8)
	byte[] getMessageBody();

	@DataField(order=5, primitiveType= ValueType.TEXT)
	String getTransactionType();
	
//	String getReponseType();
	
}
