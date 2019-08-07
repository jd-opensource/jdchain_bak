package com.jd.blockchain.ledger.core;

public class TxPrivilege extends AbstractPrivilege<TxPermission> {

	public TxPrivilege(byte[] codeBytes) {
		super(codeBytes);
	}

	@Override
	protected int getCodeIndex(TxPermission permission) {
		return permission.CODE & 0xFF;
	}

}
