package com.jd.blockchain.ledger.core;

/**
 * LedgerPrivilege 账本特权是授权给特定角色的权限代码序列；
 * 
 * @author huanghaiquan
 *
 */
public class LedgerPrivilege extends AbstractPrivilege<LedgerPermission> {

	public LedgerPrivilege() {
	}
	
	public LedgerPrivilege(byte[] codeBytes) {
		super(codeBytes);
	}

	@Override
	protected int getCodeIndex(LedgerPermission permission) {
		return permission.CODE & 0xFF;
	}

}
