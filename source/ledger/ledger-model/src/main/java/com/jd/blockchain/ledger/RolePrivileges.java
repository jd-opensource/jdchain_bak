package com.jd.blockchain.ledger;

/**
 * 对角色的授权；
 * 
 * @author huanghaiquan
 *
 */
public class RolePrivileges implements PrivilegeSet {

	private String roleName;

	private long version;

	private LedgerPrivilege ledgerPrivilege;

	private TransactionPrivilege txPrivilege;

	public RolePrivileges(String roleName, long version) {
		this.roleName = roleName;
		this.version = version;
		this.ledgerPrivilege = new LedgerPrivilege();
		this.txPrivilege = new TransactionPrivilege();
	}

	public RolePrivileges(String roleName, long version, PrivilegeSet privilege) {
		this.roleName = roleName;
		this.version = version;
		this.ledgerPrivilege = privilege.getLedgerPrivilege();
		this.txPrivilege = privilege.getTransactionPrivilege();
	}

	public RolePrivileges(String roleName, long version, LedgerPrivilege ledgerPrivilege, TransactionPrivilege txPrivilege) {
		this.roleName = roleName;
		this.version = version;
		this.ledgerPrivilege = ledgerPrivilege;
		this.txPrivilege = txPrivilege;
	}

	public String getRoleName() {
		return roleName;
	}

	public long getVersion() {
		return version;
	}

	@Override
	public LedgerPrivilege getLedgerPrivilege() {
		return ledgerPrivilege;
	}

	public void setLedgerPrivilege(LedgerPrivilege ledgerPrivilege) {
		this.ledgerPrivilege = ledgerPrivilege;
	}

	@Override
	public TransactionPrivilege getTransactionPrivilege() {
		return txPrivilege;
	}

	public void setTransactionPrivilege(TransactionPrivilege txPrivilege) {
		this.txPrivilege = txPrivilege;
	}

}
