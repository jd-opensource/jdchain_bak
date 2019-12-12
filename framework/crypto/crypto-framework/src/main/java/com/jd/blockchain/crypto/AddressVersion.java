package com.jd.blockchain.crypto;

/**
 * The version of Blockchain Address generation rule； <br>
 * 
 * 
 * 
 * @author huanghaiquan
 *
 */
public enum AddressVersion {

	V1((byte) 0x91);

	// Note: Implementor can only add new enum items, cann't remove or modify
	// existing enum items;

	public final byte CODE;

	AddressVersion(byte code) {
		CODE = code;
	}

}
