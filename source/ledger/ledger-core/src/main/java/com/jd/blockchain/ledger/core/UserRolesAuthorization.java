package com.jd.blockchain.ledger.core;

import java.util.Set;
import java.util.TreeSet;

import com.jd.blockchain.utils.Bytes;

public class UserRolesAuthorization implements RoleSet {

	private Bytes userAddress;

	private RolesPolicy policy;

	private Set<String> roles;

	private long version;

	public UserRolesAuthorization(Bytes userAddress, long version, RolesPolicy policy) {
		this.userAddress = userAddress;
		this.version = version;
		this.policy = policy;
		this.roles = new TreeSet<String>();
	}

	public UserRolesAuthorization(Bytes userAddress, long version, RoleSet roleSet) {
		this.userAddress = userAddress;
		this.version = version;
		this.policy = roleSet.getPolicy();
		this.roles = initRoles(roleSet.getRoles());

	}

	private Set<String> initRoles(String[] roles) {
		TreeSet<String> roleset = new TreeSet<String>();
		if (roles != null) {
			for (String r : roles) {
				roleset.add(r);
			}
		}
		return roleset;
	}

	public Bytes getUserAddress() {
		return userAddress;
	}

	@Override
	public RolesPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(RolesPolicy policy) {
		this.policy = policy;
	}

	public int getRoleCount() {
		return roles.size();
	}

	@Override
	public String[] getRoles() {
		return roles.toArray(new String[roles.size()]);
	}

	public long getVersion() {
		return version;
	}

	public void addRoles(String... roles) {
		for (String r : roles) {
			this.roles.add(r);
		}
	}

	/**
	 * 设置角色集合；<br>
	 * 注意，这不是追加；现有的不在参数指定范围的角色将被移除；
	 * 
	 * @param roles
	 */
	public void setRoles(String[] roles) {

	}
}
