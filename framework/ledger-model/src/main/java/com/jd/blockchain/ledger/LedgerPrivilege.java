package com.jd.blockchain.ledger;

/**
 * LedgerPrivilege 账本特权是授权给特定角色的权限代码序列；
 * 
 * @author huanghaiquan
 *
 */
public class LedgerPrivilege extends PrivilegeBitset<LedgerPermission> {

	public LedgerPrivilege() {
	}

	public LedgerPrivilege(byte[] codeBytes) {
		super(codeBytes);
	}

	@Override
	public LedgerPrivilege clone() {
		return (LedgerPrivilege) super.clone();
	}

}
