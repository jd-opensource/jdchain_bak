package com.jd.blockchain.crypto;

public enum AddressVersion {

	V1((byte) 0x91);

	public final byte CODE;

	AddressVersion(byte code) {
		CODE = code;
	}

}
