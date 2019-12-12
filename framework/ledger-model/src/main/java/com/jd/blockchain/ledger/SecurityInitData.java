package com.jd.blockchain.ledger;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.utils.Bytes;

public class SecurityInitData implements SecurityInitSettings {

	static {
		DataContractRegistry.register(SecurityInitSettings.class);
	}


	private Map<String, RoleInitData> roles = new LinkedHashMap<>();

	private Map<Bytes, UserAuthInitData> userAuthentications = new LinkedHashMap<>();

	@Override
	public RoleInitData[] getRoles() {
		return roles.values().toArray(new RoleInitData[roles.size()]);
	}

	public int getRolesCount() {
		return roles.size();
	}

	public void setRoles(RoleInitData[] roles) {
		Map<String, RoleInitData> newRoles = new LinkedHashMap<>();
		for (RoleInitData r : roles) {
			newRoles.put(r.getRoleName(), r);
		}
		this.roles = newRoles;
	}

	public boolean containsRole(String roleName) {
		return roles.containsKey(roleName);
	}

	public void addRole(String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] transactionPermissions) {
		RoleInitData roleInitData = new RoleInitData(roleName, ledgerPermissions, transactionPermissions);
		roles.put(roleName, roleInitData);
	}

	@Override
	public UserAuthInitData[] getUserAuthorizations() {
		return userAuthentications.values().toArray(new UserAuthInitData[userAuthentications.size()]);
	}

	public void addUserAuthencation(Bytes address, String[] roles, RolesPolicy policy) {
		UserAuthInitData userAuth = new UserAuthInitData();
		userAuth.setUserAddress(address);
		userAuth.setRoles(roles);
		userAuth.setPolicy(policy);

		userAuthentications.put(address, userAuth);
	}
}
