package com.jd.blockchain.ledger.core;

public enum LedgerPermission {

	SET_ROLE((byte) 0);

	public final byte CODE;

	private LedgerPermission(byte code) {
		this.CODE = code;
	}

}
