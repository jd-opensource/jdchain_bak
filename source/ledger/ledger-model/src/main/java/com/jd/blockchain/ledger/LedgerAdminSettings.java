package com.jd.blockchain.ledger;

public interface LedgerAdminSettings extends LedgerAdminInfo {

	UserAuthorizationSettings getAuthorizations();

	RolePrivilegeSettings getRolePrivileges();
}
