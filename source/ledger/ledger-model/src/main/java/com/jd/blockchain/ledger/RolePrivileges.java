package com.jd.blockchain.ledger;

/**
 * 对角色的授权；
 * 
 * @author huanghaiquan
 *
 */
public class RolePrivileges extends Privileges {

	private String roleName;

	private long version;

	public RolePrivileges(String roleName, long version) {
		this.roleName = roleName;
		this.version = version;
	}

	public RolePrivileges(String roleName, long version, PrivilegeSet privilege) {
		super(privilege);
		this.roleName = roleName;
		this.version = version;
	}

	public RolePrivileges(String roleName, long version, LedgerPrivilege ledgerPrivilege,
			TransactionPrivilege txPrivilege) {
		super(ledgerPrivilege, txPrivilege);
		this.roleName = roleName;
		this.version = version;
	}

	public String getRoleName() {
		return roleName;
	}

	public long getVersion() {
		return version;
	}

}
