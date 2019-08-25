package com.jd.blockchain.ledger;

/**
 * LedgerPrivilege 账本特权是授权给特定角色的权限代码序列；
 * 
 * @author huanghaiquan
 *
 */
public class LedgerPrivilege extends PrivilegeBitset<LedgerPermission> {

	private static final CodeIndexer<LedgerPermission> CODE_INDEXER = new LedgerPermissionCodeIndexer();

	public LedgerPrivilege() {
		super(CODE_INDEXER);
	}

	public LedgerPrivilege(byte[] codeBytes) {
		super(codeBytes, CODE_INDEXER);
	}

	private static class LedgerPermissionCodeIndexer implements CodeIndexer<LedgerPermission> {

		@Override
		public int getCodeIndex(LedgerPermission permission) {
			return permission.CODE & 0xFF;
		}

	}
}
