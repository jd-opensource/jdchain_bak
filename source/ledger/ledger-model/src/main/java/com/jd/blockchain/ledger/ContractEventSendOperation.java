package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ValueType;

/**
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.TX_OP_CONTRACT_EVENT_SEND)
public interface ContractEventSendOperation extends Operation {
	
//	@DataField(order=1, refEnum=true)
//	@Override
//	default OperationType getType() {
//		return OperationType.SEND_CONTRACT_EVENT;
//	}
	
	@DataField(order=2, primitiveType=ValueType.BYTES)
	Bytes getContractAddress();
	
	@DataField(order=3, primitiveType=ValueType.TEXT)
	String getEvent();
	
	
	@DataField(order=4, primitiveType=ValueType.BYTES)
	byte[] getArgs();
	
}
