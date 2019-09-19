package com.jd.blockchain.ledger;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.utils.Bytes;

public class UserRoles implements RoleSet {

	static {
		DataContractRegistry.register(RoleSet.class);
	}

	private Bytes userAddress;

	private RolesPolicy policy;

	private Set<String> roles;

	private long version;

	public UserRoles(Bytes userAddress, long version, RolesPolicy policy) {
		this.userAddress = userAddress;
		this.version = version;
		this.policy = policy;
		this.roles = new TreeSet<String>();
	}

	public UserRoles(Bytes userAddress, long version, RoleSet roleSet) {
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
	
	public Set<String> getRoleSet(){
		return Collections.unmodifiableSet(roles);
	}

	public long getVersion() {
		return version;
	}

	public void addRoles(String... roles) {
		for (String r : roles) {
			this.roles.add(r);
		}
	}

	public void addRoles(Collection<String> roles) {
		for (String r : roles) {
			this.roles.add(r);
		}
	}

	public void removeRoles(String... roles) {
		for (String r : roles) {
			this.roles.remove(r);
		}
	}
	
	public void removeRoles(Collection<String> roles) {
		for (String r : roles) {
			this.roles.remove(r);
		}
	}

	/**
	 * 设置角色集合；<br>
	 * 注意，这不是追加；现有的不在参数指定范围的角色将被移除；
	 * 
	 * @param roles
	 */
	public void setRoles(String[] roles) {
		TreeSet<String> rs = new TreeSet<String>();
		for (String r : roles) {
			rs.add(r);
		}
		this.roles = rs;
	}
}
