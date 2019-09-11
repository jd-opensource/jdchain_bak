package com.jd.blockchain.ledger;

public class RoleInitData implements RoleInitSettings {

	private String roleName;

	private LedgerPermission[] ledgerPermissions;

	private TransactionPermission[] transactionPermissions;

	public RoleInitData() {
	}

	public RoleInitData(String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] transactionPermissions) {
		this.roleName = roleName;
		this.ledgerPermissions = ledgerPermissions;
		this.transactionPermissions = transactionPermissions;
	}

	@Override
	public String getRoleName() {
		return roleName;
	}

	@Override
	public LedgerPermission[] getLedgerPermissions() {
		return ledgerPermissions;
	}

	@Override
	public TransactionPermission[] getTransactionPermissions() {
		return transactionPermissions;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setLedgerPermissions(LedgerPermission[] ledgerPermissions) {
		this.ledgerPermissions = ledgerPermissions;
	}

	public void setTransactionPermissions(TransactionPermission[] transactionPermissions) {
		this.transactionPermissions = transactionPermissions;
	}

}
