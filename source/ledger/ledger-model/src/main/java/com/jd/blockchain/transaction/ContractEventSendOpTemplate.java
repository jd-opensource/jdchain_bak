package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.utils.Bytes;

public class ContractEventSendOpTemplate implements ContractEventSendOperation {
	static {
		DataContractRegistry.register(ContractEventSendOperation.class);
	}

	private Bytes contractAddress;
	private byte[] args;
	private String event;

	public ContractEventSendOpTemplate() {
	}

	public ContractEventSendOpTemplate(Bytes contractAddress, String event, byte[] args) {
		this.contractAddress = contractAddress;
		this.event = event;
		this.args = args;
	}

	@Override
	public Bytes getContractAddress() {
		return contractAddress;
	}

	@Override
	public String getEvent() {
		return event;
	}

	@Override
	public byte[] getArgs() {
		return args;
	}

}
