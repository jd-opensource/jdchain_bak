package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.DigitalSignature;

public class AuthorizationVO implements Authorization {
	
	private String address;
	
	private byte[] code;
	
	private DigitalSignature signature;
	
	
	@Override
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public byte[] getCode() {
		return code;
	}

	public void setCode(byte[] code) {
		this.code = code;
	}

	@Override
	public DigitalSignature getSignature() {
		return signature;
	}


	public void setSignature(DigitalSignature signature) {
		this.signature = signature;
	}

}