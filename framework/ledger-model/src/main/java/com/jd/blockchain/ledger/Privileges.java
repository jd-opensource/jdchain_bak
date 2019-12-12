package com.jd.blockchain.ledger;

public class Privileges implements PrivilegeSet {

	private LedgerPrivilege ledgerPrivilege;

	private TransactionPrivilege txPrivilege;

	protected Privileges() {
		this.ledgerPrivilege = new LedgerPrivilege();
		this.txPrivilege = new TransactionPrivilege();
	}

	protected Privileges(PrivilegeSet privilege) {
		this.ledgerPrivilege = privilege.getLedgerPrivilege();
		this.txPrivilege = privilege.getTransactionPrivilege();
	}

	protected Privileges(LedgerPrivilege ledgerPrivilege, TransactionPrivilege txPrivilege) {
		this.ledgerPrivilege = ledgerPrivilege;
		this.txPrivilege = txPrivilege;
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

	public static Privileges configure() {
		return new Privileges();
	}
	
	public Privileges enable(LedgerPermission...ledgerPermissions) {
		this.ledgerPrivilege.enable(ledgerPermissions);
		return this;
	}
	
	public Privileges disable(LedgerPermission...ledgerPermissions) {
		this.ledgerPrivilege.disable(ledgerPermissions);
		return this;
	}
	
	public Privileges enable(TransactionPermission...transactionPermissions) {
		this.txPrivilege.enable(transactionPermissions);
		return this;
	}
	
	public Privileges disable(TransactionPermission...transactionPermissions) {
		this.txPrivilege.disable(transactionPermissions);
		return this;
	}
}
