package com.jd.blockchain.ledger;

public class TransactionPrivilege extends AbstractPrivilege<TransactionPermission> {

	public TransactionPrivilege() {
	}

	public TransactionPrivilege(byte[] codeBytes) {
		super(codeBytes);
	}

	@Override
	protected int getCodeIndex(TransactionPermission permission) {
		return permission.CODE & 0xFF;
	}

}
