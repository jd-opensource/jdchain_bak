package com.jd.blockchain.ledger;

public class TransactionPrivilege extends PrivilegeBitset<TransactionPermission> {

	public TransactionPrivilege() {
	}

	public TransactionPrivilege(byte[] codeBytes) {
		super(codeBytes);
	}

	@Override
	public TransactionPrivilege clone() {
		return (TransactionPrivilege) super.clone();
	}

}
