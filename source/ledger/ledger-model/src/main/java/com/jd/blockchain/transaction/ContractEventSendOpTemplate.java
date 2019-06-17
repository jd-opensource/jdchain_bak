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
	//交易操作时间;
	private long txOpTime;
	// 所属操作Index
	private int opIndex;

	public ContractEventSendOpTemplate() {
	}

	public ContractEventSendOpTemplate(Bytes contractAddress) {
		this(contractAddress, -1);
	}

	public ContractEventSendOpTemplate(Bytes contractAddress, int opIndex) {
		this.contractAddress = contractAddress;
		this.opIndex = opIndex;
		this.txOpTime = System.currentTimeMillis();
	}

	public ContractEventSendOpTemplate(Bytes contractAddress, String event, byte[] args) {
		this(contractAddress, event, args, -1);
	}

	public ContractEventSendOpTemplate(Bytes contractAddress, String event, byte[] args, int opIndex) {
		this.contractAddress = contractAddress;
		this.event = event;
		this.args = args;
		this.opIndex = opIndex;
		this.txOpTime = System.currentTimeMillis();
	}

	public void setArgs(byte[] args) {
		this.args = args;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public void setEventAndArgs(String event, byte[] args) {
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

	@Override
	public long getTxOpTime() {
		return txOpTime;
	}

	/**
	 * 获取所属交易中的序号，该值不需要序列化
	 *
	 * @return
	 */
	public int getOpIndex() {
		return opIndex;
	}
}
