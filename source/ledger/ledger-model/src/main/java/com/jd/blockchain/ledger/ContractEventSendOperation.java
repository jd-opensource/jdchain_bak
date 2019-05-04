package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.Bytes;

/**
 * @author huanghaiquan
 *
 */
@DataContract(code= DataCodes.TX_OP_CONTRACT_EVENT_SEND)
public interface ContractEventSendOperation extends Operation {
	
	@DataField(order=2, primitiveType=DataType.BYTES)
	Bytes getContractAddress();
	
	@DataField(order=3, primitiveType=DataType.TEXT)
	String getEvent();
	
	
	@DataField(order=4, primitiveType=DataType.BYTES)
	byte[] getArgs();
	
}
