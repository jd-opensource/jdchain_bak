package com.jd.blockchain.ledger;

import java.util.ArrayList;
import java.util.List;

public class SecurityInitData implements SecurityInitSettings {

	private List<RoleInitData> roles = new ArrayList<RoleInitData>();

	@Override
	public RoleInitData[] getRoles() {
		return roles.toArray(new RoleInitData[roles.size()]);
	}

	public void setRoles(RoleInitData[] roles) {
		List<RoleInitData> list = new ArrayList<RoleInitData>();
		for (RoleInitData r : roles) {
			list.add(r);
		}
		this.roles = list;
	}

	public void add(String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] transactionPermissions) {
		RoleInitData roleInitData = new RoleInitData(roleName, ledgerPermissions, transactionPermissions);
		roles.add(roleInitData);
	}

	@Override
	public UserAuthInitSettings[] getUserAuthorizations() {
		// TODO Auto-generated method stub
		return null;
	}
}
