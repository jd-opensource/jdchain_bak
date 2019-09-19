package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.utils.Bytes;

public class UserAuthInitData implements UserAuthInitSettings {

	static {
		DataContractRegistry.register(UserAuthInitSettings.class);
	}

	private Bytes userAddress;

	private String[] roles;

	private RolesPolicy policy;

	public void setUserAddress(Bytes userAddress) {
		this.userAddress = userAddress;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public void setPolicy(RolesPolicy policy) {
		this.policy = policy;
	}

	@Override
	public Bytes getUserAddress() {
		return userAddress;
	}

	@Override
	public String[] getRoles() {
		return roles;
	}

	@Override
	public RolesPolicy getPolicy() {
		return policy;
	}

}
