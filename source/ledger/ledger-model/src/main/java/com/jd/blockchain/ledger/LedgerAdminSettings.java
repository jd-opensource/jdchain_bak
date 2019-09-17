package com.jd.blockchain.ledger;

public interface LedgerAdminSettings extends LedgerAdminInfo {

	UserRolesSettings getUserRoles();

	RolePrivilegeSettings getRolePrivileges();
}
