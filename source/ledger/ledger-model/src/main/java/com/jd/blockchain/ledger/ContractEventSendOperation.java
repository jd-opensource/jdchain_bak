package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.Bytes;

/**
 * @author huanghaiquan
 *
 */
@DataContract(code= DataCodes.TX_OP_CONTRACT_EVENT_SEND)
public interface ContractEventSendOperation extends Operation {
	
	@DataField(order=2, primitiveType=PrimitiveType.BYTES)
	Bytes getContractAddress();
	
	@DataField(order=3, primitiveType=PrimitiveType.TEXT)
	String getEvent();
	
	
	@DataField(order=4, primitiveType=PrimitiveType.BYTES)
	byte[] getArgs();

	//获得交易操作时间;
	@DataField(order=5, primitiveType=PrimitiveType.INT64)
	Long getTxOpTime();
	
}
