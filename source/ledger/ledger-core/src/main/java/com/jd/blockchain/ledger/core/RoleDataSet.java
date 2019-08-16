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

public class RoleDataSet implements Transactional, MerkleProvable {

	/**
	 * 角色名称的最大 Unicode 字符数；
	 */
	public static final int MAX_ROLE_NAME_LENGTH = 20;

	private MerkleDataSet dataset;

	public RoleDataSet(CryptoSetting cryptoSetting, String prefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage verStorage) {
		dataset = new MerkleDataSet(cryptoSetting, prefix, exPolicyStorage, verStorage);
	}

	public RoleDataSet(HashDigest merkleRootHash, CryptoSetting cryptoSetting, String prefix,
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
	 * 加入新的角色授权； <br>
	 * 
	 * 如果指定的角色已经存在，则引发 {@link LedgerException} 异常；
	 * 
	 * @param roleName        角色名称；不能超过 {@link #MAX_ROLE_NAME_LENGTH} 个 Unicode 字符；
	 * @param ledgerPrivilege
	 * @param txPrivilege
	 */
	public void addRoleAuthorization(String roleName, LedgerPrivilege ledgerPrivilege,
			TransactionPrivilege txPrivilege) {
		RolePrivilegeAuthorization roleAuth = new RolePrivilegeAuthorization(roleName, -1, ledgerPrivilege, txPrivilege);
		long nv = innerSetRoleAuthorization(roleAuth);
		if (nv < 0) {
			throw new LedgerException("Role[" + roleName + "] already exist!");
		}
	}

	/**
	 * 设置角色授权； <br>
	 * 如果版本校验不匹配，则返回 -1；
	 * 
	 * @param roleAuth
	 * @return
	 */
	public long innerSetRoleAuthorization(RolePrivilegeAuthorization roleAuth) {
		if (roleAuth.getRoleName().length() > MAX_ROLE_NAME_LENGTH) {
			throw new LedgerException("Too long role name!");
		}
		Bytes key = encodeKey(roleAuth.getRoleName());
		byte[] privilegeBytes = BinaryProtocol.encode(roleAuth, RolePrivilege.class);
		return dataset.setValue(key, privilegeBytes, roleAuth.getVersion());
	}

	/**
	 * 更新角色授权； <br>
	 * 如果指定的角色不存在，或者版本不匹配，则引发 {@link LedgerException} 异常；
	 * 
	 * @param participant
	 */
	public void updateRoleAuthorization(RolePrivilegeAuthorization roleAuth) {
		long nv = innerSetRoleAuthorization(roleAuth);
		if (nv < 0) {
			throw new LedgerException("Update to RoleAuthorization[" + roleAuth.getRoleName()
					+ "] failed due to wrong version[" + roleAuth.getVersion() + "] !");
		}
	}

	/**
	 * 授权角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param participant
	 */
	public long authorizePermissions(String roleName, LedgerPermission... permissions) {
		RolePrivilegeAuthorization roleAuth = getRoleAuthorization(roleName);
		if (roleAuth == null) {
			return -1;
		}
		roleAuth.getLedgerPrivilege().enable(permissions);
		return innerSetRoleAuthorization(roleAuth);
	}

	/**
	 * 授权角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param participant
	 */
	public long authorizePermissions(String roleName, TransactionPermission... permissions) {
		RolePrivilegeAuthorization roleAuth = getRoleAuthorization(roleName);
		if (roleAuth == null) {
			return -1;
		}
		roleAuth.getTransactionPrivilege().enable(permissions);
		return innerSetRoleAuthorization(roleAuth);
	}

	private Bytes encodeKey(String address) {
		// return id + "";
		return Bytes.fromString(address);
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
	public RolePrivilegeAuthorization getRoleAuthorization(String roleName) {
		// 只返回最新版本；
		Bytes key = encodeKey(roleName);
		VersioningKVEntry kv = dataset.getDataEntry(key);
		if (kv == null) {
			return null;
		}
		RolePrivilege privilege = BinaryProtocol.decode(kv.getValue());
		return new RolePrivilegeAuthorization(roleName, kv.getVersion(), privilege);
	}

	public RolePrivilegeAuthorization[] getRoleAuthorizations() {
		VersioningKVEntry[] kvEntries = dataset.getLatestDataEntries(0, (int) dataset.getDataCount());
		RolePrivilegeAuthorization[] pns = new RolePrivilegeAuthorization[kvEntries.length];
		RolePrivilege privilege;
		for (int i = 0; i < pns.length; i++) {
			privilege = BinaryProtocol.decode(kvEntries[i].getValue());
			pns[i] = new RolePrivilegeAuthorization(kvEntries[i].getKey().toUTF8String(), kvEntries[i].getVersion(), privilege);
		}
		return pns;
	}

}
