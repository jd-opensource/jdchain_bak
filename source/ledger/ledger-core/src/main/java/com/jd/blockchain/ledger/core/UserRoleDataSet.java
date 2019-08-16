package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVEntry;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

public class UserRoleDataSet implements Transactional, MerkleProvable {

	/**
	 * 角色名称的最大 Unicode 字符数；
	 */
	public static final int MAX_ROLE_NAME_LENGTH = 20;

	private MerkleDataSet dataset;

	public UserRoleDataSet(CryptoSetting cryptoSetting, String prefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage verStorage) {
		dataset = new MerkleDataSet(cryptoSetting, prefix, exPolicyStorage, verStorage);
	}

	public UserRoleDataSet(HashDigest merkleRootHash, CryptoSetting cryptoSetting, String prefix,
			ExPolicyKVStorage exPolicyStorage, VersioningKVStorage verStorage, boolean readonly) {
		dataset = new MerkleDataSet(merkleRootHash, cryptoSetting, prefix, exPolicyStorage, verStorage, readonly);
	}

	@Override
	public HashDigest getRootHash() {
		return dataset.getRootHash();
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		return dataset.getProof(key);
	}

	@Override
	public boolean isUpdated() {
		return dataset.isUpdated();
	}

	@Override
	public void commit() {
		dataset.commit();
	}

	@Override
	public void cancel() {
		dataset.cancel();
	}

	public long getRoleCount() {
		return dataset.getDataCount();
	}

	/**
	 * 加入新的用户角色授权； <br>
	 * 
	 * 如果该用户的授权已经存在，则引发 {@link LedgerException} 异常；
	 * 
	 * @param userAddress
	 * @param rolesPolicy
	 * @param roles
	 */
	public void addUserRoles(Bytes userAddress, RolesPolicy rolesPolicy, String... roles) {
		UserRolesAuthorization roleAuth = new UserRolesAuthorization(userAddress, -1, rolesPolicy);
		roleAuth.addRoles(roles);
		long nv = innerSetUserRolesAuthorization(roleAuth);
		if (nv < 0) {
			throw new LedgerException("Roles authorization of User[" + userAddress + "] already exists!");
		}
	}

	/**
	 * 设置用户角色授权； <br>
	 * 如果版本校验不匹配，则返回 -1；
	 * 
	 * @param userRoles
	 * @return
	 */
	public long innerSetUserRolesAuthorization(UserRolesAuthorization userRoles) {
		byte[] rolesetBytes = BinaryProtocol.encode(userRoles, RoleSet.class);
		return dataset.setValue(userRoles.getUserAddress(), rolesetBytes, userRoles.getVersion());
	}

	/**
	 * 更新用户角色授权； <br>
	 * 如果指定用户的授权不存在，或者版本不匹配，则引发 {@link LedgerException} 异常；
	 * 
	 * @param userRoles
	 */
	public void updateUserRolesAuthorization(UserRolesAuthorization userRoles) {
		long nv = innerSetUserRolesAuthorization(userRoles);
		if (nv < 0) {
			throw new LedgerException("Update to roles of user[" + userRoles.getUserAddress()
					+ "] failed due to wrong version[" + userRoles.getVersion() + "] !");
		}
	}

	/**
	 * 设置用户的角色； <br>
	 * 如果用户的角色授权不存在，则创建新的授权；
	 * 
	 * @param userAddress 用户；
	 * @param policy      角色策略；
	 * @param roles       角色列表；
	 * @return
	 */
	public long setRoles(Bytes userAddress, RolesPolicy policy, String... roles) {
		UserRolesAuthorization userRoles = getUserRolesAuthorization(userAddress);
		if (userRoles == null) {
			userRoles = new UserRolesAuthorization(userAddress, -1, policy);
		}
		userRoles.setPolicy(policy);
		userRoles.setRoles(roles);
		return innerSetUserRolesAuthorization(userRoles);
	}

	/**
	 * 查询角色授权；
	 * 
	 * <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	public UserRolesAuthorization getUserRolesAuthorization(Bytes userAddress) {
		// 只返回最新版本；
		VersioningKVEntry kv = dataset.getDataEntry(userAddress);
		if (kv == null) {
			return null;
		}
		RoleSet roleSet = BinaryProtocol.decode(kv.getValue());
		return new UserRolesAuthorization(userAddress, kv.getVersion(), roleSet);
	}

	public RolePrivilegeAuthorization[] getRoleAuthorizations() {
		VersioningKVEntry[] kvEntries = dataset.getLatestDataEntries(0, (int) dataset.getDataCount());
		RolePrivilegeAuthorization[] pns = new RolePrivilegeAuthorization[kvEntries.length];
		RolePrivilege privilege;
		for (int i = 0; i < pns.length; i++) {
			privilege = BinaryProtocol.decode(kvEntries[i].getValue());
			pns[i] = new RolePrivilegeAuthorization(kvEntries[i].getKey().toUTF8String(), kvEntries[i].getVersion(),
					privilege);
		}
		return pns;
	}

}
