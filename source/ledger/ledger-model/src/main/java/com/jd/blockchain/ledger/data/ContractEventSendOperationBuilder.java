package com.jd.blockchain.ledger.data;

import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.utils.Bytes;

public interface ContractEventSendOperationBuilder {

	/**
	 * @param address 合约地址；
	 * @param event 事件名；
	 * @param args 事件参数；
	 * @return
	 */
	ContractEventSendOperation send(String address, String event, byte[] args);
	
	/**
	 * @param address 合约地址；
	 * @param event 事件名；
	 * @param args 事件参数；
	 * @return
	 */
	ContractEventSendOperation send(Bytes address, String event, byte[] args);

}
