package com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.DConstructor;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.binaryproto.FieldSetter;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.utils.Bytes;

public class ContractEventSendOpTemplate implements ContractEventSendOperation {
	static {
		DataContractRegistry.register(ContractEventSendOperation.class);
	}

	private Bytes contractAddress;
	private byte[] args;
	private String event;
	//交易操作时间;
	private Long txOpTime;

	public ContractEventSendOpTemplate() {
	}

	@DConstructor(name="ContractEventSendOpTemplate")
	public ContractEventSendOpTemplate(@FieldSetter(name="getContractAddress", type="Bytes") Bytes contractAddress,
                                       @FieldSetter(name="getEvent", type="String") String event,
                                       @FieldSetter(name="getArgs", type="byte[]") byte[] args) {
		this.contractAddress = contractAddress;
		this.event = event;
		this.args = args;
		this.txOpTime = System.currentTimeMillis();
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

	@Override
	public Long getTxOpTime() {
		return txOpTime;
	}
}
