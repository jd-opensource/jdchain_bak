package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.Bytes;

/**
 * 发送合约事件的操作；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.TX_OP_CONTRACT_EVENT_SEND)
public interface ContractEventSendOperation extends Operation {

	/**
	 * 响应事件的合约地址；
	 * 
	 * @return
	 */
	@DataField(order = 2, primitiveType = PrimitiveType.BYTES)
	Bytes getContractAddress();

	/**
	 * 事件名；
	 * 
	 * @return
	 */
	@DataField(order = 3, primitiveType = PrimitiveType.TEXT)
	String getEvent();

	/**
	 * 事件参数；
	 * 
	 * @return
	 */
	@DataField(order = 4, refContract = true)
	BytesValueList getArgs();

}
