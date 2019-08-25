package com.jd.blockchain.ledger;

public class TransactionPrivilege extends PrivilegeBitset<TransactionPermission> {

	private static final CodeIndexer<TransactionPermission> CODE_INDEXER = new TransactionPermissionCodeIndexer();

	public TransactionPrivilege() {
		super(CODE_INDEXER);
	}

	public TransactionPrivilege(byte[] codeBytes) {
		super(codeBytes, CODE_INDEXER);
	}

	private static class TransactionPermissionCodeIndexer implements CodeIndexer<TransactionPermission> {

		@Override
		public int getCodeIndex(TransactionPermission permission) {
			return permission.CODE & 0xFF;
		}

	}
}
