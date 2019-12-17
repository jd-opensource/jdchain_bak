package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerPrivilege;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.ledger.PrivilegeSet;
import com.jd.blockchain.ledger.Privileges;
import com.jd.blockchain.ledger.RolePrivilegeSettings;
import com.jd.blockchain.ledger.RolePrivileges;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionPrivilege;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;
import com.jd.blockchain.utils.DataEntry;

public class RolePrivilegeDataset implements Transactional, MerkleProvable, RolePrivilegeSettings {

	private MerkleDataSet dataset;

	public RolePrivilegeDataset(CryptoSetting cryptoSetting, String prefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage verStorage) {
		dataset = new MerkleDataSet(cryptoSetting, prefix, exPolicyStorage, verStorage);
	}

	public RolePrivilegeDataset(HashDigest merkleRootHash, CryptoSetting cryptoSetting, String prefix,
			ExPolicyKVStorage exPolicyStorage, VersioningKVStorage verStorage, boolean readonly) {
		dataset = new MerkleDataSet(merkleRootHash, cryptoSetting, Bytes.fromString(prefix), exPolicyStorage,
				verStorage, readonly);
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

	@Override
	public long getRoleCount() {
		return dataset.getDataCount();
	}

	@Override
	public long addRolePrivilege(String roleName, Privileges privileges) {
		return addRolePrivilege(roleName, privileges.getLedgerPrivilege(), privileges.getTransactionPrivilege());
	}

	@Override
	public long addRolePrivilege(String roleName, LedgerPrivilege ledgerPrivilege, TransactionPrivilege txPrivilege) {
		RolePrivileges roleAuth = new RolePrivileges(roleName, -1, ledgerPrivilege, txPrivilege);
		long nv = setRolePrivilege(roleAuth);
		if (nv < 0) {
			throw new LedgerException("Role[" + roleName + "] already exist!");
		}
		return nv;
	}

	@Override
	public long addRolePrivilege(String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] txPermissions) {
		LedgerPrivilege ledgerPrivilege = new LedgerPrivilege();
		for (LedgerPermission lp : ledgerPermissions) {
			ledgerPrivilege.enable(lp);
		}
		TransactionPrivilege txPrivilege = new TransactionPrivilege();
		for (TransactionPermission tp : txPermissions) {
			txPrivilege.enable(tp);
		}
		return addRolePrivilege(roleName, ledgerPrivilege, txPrivilege);
	}

	/**
	 * 设置角色权限； <br>
	 * 如果版本校验不匹配，则返回 -1；
	 * 
	 * @param roleAuth
	 * @return
	 */
	private long setRolePrivilege(RolePrivileges roleAuth) {
		if (roleAuth.getRoleName().length() > MAX_ROLE_NAME_LENGTH) {
			throw new LedgerException("Too long role name!");
		}
		Bytes key = encodeKey(roleAuth.getRoleName());
		byte[] privilegeBytes = BinaryProtocol.encode(roleAuth, PrivilegeSet.class);
		return dataset.setValue(key, privilegeBytes, roleAuth.getVersion());
	}

	/**
	 * 更新角色权限； <br>
	 * 如果指定的角色不存在，或者版本不匹配，则引发 {@link LedgerException} 异常；
	 * 
	 * @param participant
	 */
	@Override
	public void updateRolePrivilege(RolePrivileges roleAuth) {
		long nv = setRolePrivilege(roleAuth);
		if (nv < 0) {
			throw new LedgerException("Update to RoleAuthorization[" + roleAuth.getRoleName()
					+ "] failed due to wrong version[" + roleAuth.getVersion() + "] !");
		}
	}

	/**
	 * 授权角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName    角色；
	 * @param permissions 权限列表；
	 * @return
	 */
	@Override
	public long enablePermissions(String roleName, LedgerPermission... permissions) {
		RolePrivileges roleAuth = getRolePrivilege(roleName);
		if (roleAuth == null) {
			return -1;
		}
		roleAuth.getLedgerPrivilege().enable(permissions);
		return setRolePrivilege(roleAuth);
	}

	/**
	 * 授权角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName    角色；
	 * @param permissions 权限列表；
	 * @return
	 */
	@Override
	public long enablePermissions(String roleName, TransactionPermission... permissions) {
		RolePrivileges roleAuth = getRolePrivilege(roleName);
		if (roleAuth == null) {
			return -1;
		}
		roleAuth.getTransactionPrivilege().enable(permissions);
		return setRolePrivilege(roleAuth);
	}

	/**
	 * 禁止角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName    角色；
	 * @param permissions 权限列表；
	 * @return
	 */
	@Override
	public long disablePermissions(String roleName, LedgerPermission... permissions) {
		RolePrivileges roleAuth = getRolePrivilege(roleName);
		if (roleAuth == null) {
			return -1;
		}
		roleAuth.getLedgerPrivilege().disable(permissions);
		return setRolePrivilege(roleAuth);
	}

	/**
	 * 禁止角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName    角色；
	 * @param permissions 权限列表；
	 * @return
	 */
	@Override
	public long disablePermissions(String roleName, TransactionPermission... permissions) {
		RolePrivileges roleAuth = getRolePrivilege(roleName);
		if (roleAuth == null) {
			return -1;
		}
		roleAuth.getTransactionPrivilege().disable(permissions);
		return setRolePrivilege(roleAuth);
	}

	/**
	 * 授权角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName
	 * @param ledgerPermissions
	 * @param txPermissions
	 * @return
	 */
	@Override
	public long enablePermissions(String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] txPermissions) {
		RolePrivileges roleAuth = getRolePrivilege(roleName);
		if (roleAuth == null) {
			return -1;
		}
		roleAuth.getLedgerPrivilege().enable(ledgerPermissions);
		roleAuth.getTransactionPrivilege().enable(txPermissions);
		return setRolePrivilege(roleAuth);
	}

	/**
	 * 禁用角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName
	 * @param ledgerPermissions
	 * @param txPermissions
	 * @return
	 */
	@Override
	public long disablePermissions(String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] txPermissions) {
		RolePrivileges roleAuth = getRolePrivilege(roleName);
		if (roleAuth == null) {
			return -1;
		}
		roleAuth.getLedgerPrivilege().disable(ledgerPermissions);
		roleAuth.getTransactionPrivilege().disable(txPermissions);
		return setRolePrivilege(roleAuth);
	}

	private Bytes encodeKey(String address) {
		// return id + "";
		return Bytes.fromString(address);
	}

	/**
	 * 查询角色权限；
	 * 
	 * <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	@Override
	public RolePrivileges getRolePrivilege(String roleName) {
		// 只返回最新版本；
		Bytes key = encodeKey(roleName);
		DataEntry<Bytes, byte[]> kv = dataset.getDataEntry(key);
		if (kv == null) {
			return null;
		}
		PrivilegeSet privilege = BinaryProtocol.decode(kv.getValue());
		return new RolePrivileges(roleName, kv.getVersion(), privilege);
	}

	@Override
	public RolePrivileges[] getRolePrivileges(int index, int count) {
		DataEntry<Bytes, byte[]>[] kvEntries = dataset.getLatestDataEntries(index, count);
		RolePrivileges[] pns = new RolePrivileges[kvEntries.length];
		PrivilegeSet privilege;
		for (int i = 0; i < pns.length; i++) {
			privilege = BinaryProtocol.decode(kvEntries[i].getValue());
			pns[i] = new RolePrivileges(kvEntries[i].getKey().toUTF8String(), kvEntries[i].getVersion(), privilege);
		}
		return pns;
	}

	@Override
	public RolePrivileges[] getRolePrivileges() {
		return getRolePrivileges(0, (int) getRoleCount());
	}

	@Override
	public boolean isReadonly() {
		return dataset.isReadonly();
	}

	@Override
	public boolean contains(String roleName) {
		Bytes key = encodeKey(roleName);
		return dataset.getVersion(key) > -1;
	}
}
