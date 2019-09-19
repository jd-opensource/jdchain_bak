package com.jd.blockchain.ledger.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.ledger.LedgerAdminSettings;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerMetadata;
import com.jd.blockchain.ledger.LedgerMetadata_V2;
import com.jd.blockchain.ledger.LedgerSettings;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.RolePrivilegeSettings;
import com.jd.blockchain.ledger.UserAuthorizationSettings;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

public class LedgerAdminDataset implements Transactional, LedgerAdminDataQuery, LedgerAdminSettings {

	static {
		DataContractRegistry.register(LedgerMetadata.class);
		DataContractRegistry.register(LedgerMetadata_V2.class);
	}

	private static Logger LOGGER = LoggerFactory.getLogger(LedgerAdminDataset.class);

	public static final String LEDGER_META_PREFIX = "MTA" + LedgerConsts.KEY_SEPERATOR;
	public static final String LEDGER_PARTICIPANT_PREFIX = "PAR" + LedgerConsts.KEY_SEPERATOR;
	public static final String LEDGER_SETTING_PREFIX = "SET" + LedgerConsts.KEY_SEPERATOR;
	public static final String ROLE_PRIVILEGE_PREFIX = "RPV" + LedgerConsts.KEY_SEPERATOR;
	public static final String USER_ROLE_PREFIX = "URO" + LedgerConsts.KEY_SEPERATOR;

	private final Bytes metaPrefix;
	private final Bytes settingPrefix;

	private LedgerMetadata_V2 origMetadata;

	private LedgerMetadataInfo metadata;

	/**
	 * 原来的账本设置；
	 * 
	 * <br>
	 * 对 LedgerMetadata 修改的新配置不能立即生效，需要达成共识后，在下一次区块计算中才生效；
	 */
	private LedgerSettings previousSettings;

	private HashDigest previousSettingHash;

	/**
	 * 账本的参与节点；
	 */
	private ParticipantDataset participants;

	/**
	 * “角色-权限”数据集；
	 */
	private RolePrivilegeDataset rolePrivileges;

	/**
	 * “用户-角色”数据集；
	 */
	private UserRoleDataset userRoles;

	/**
	 * 账本参数配置；
	 */
	private LedgerSettings settings;

	private ExPolicyKVStorage storage;

	private HashDigest adminDataHash;

	private boolean readonly;

	private boolean updated;

	public HashDigest getHash() {
		return adminDataHash;
	}

	public boolean isReadonly() {
		return readonly;
	}

	void setReadonly() {
		this.readonly = true;
	}

	public LedgerSettings getPreviousSetting() {
		return previousSettings;
	}

	@Override
	public RolePrivilegeSettings getRolePrivileges() {
		return rolePrivileges;
	}

	@Override
	public UserAuthorizationSettings getAuthorizations() {
		return userRoles;
	}

	@Override
	public LedgerAdminSettings getAdminInfo() {
		return this;
	}

	/**
	 * 初始化账本的管理账户；
	 * 
	 * <br>
	 * 
	 * 只在新建账本时调用此方法；
	 * 
	 * @param ledgerSeed
	 * @param settings
	 * @param partiList
	 * @param exPolicyStorage
	 * @param versioningStorage
	 */
	public LedgerAdminDataset(LedgerInitSetting initSetting, String keyPrefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage versioningStorage) {
		this.metaPrefix = Bytes.fromString(keyPrefix + LEDGER_META_PREFIX);
		this.settingPrefix = Bytes.fromString(keyPrefix + LEDGER_SETTING_PREFIX);

		ParticipantNode[] parties = initSetting.getConsensusParticipants();
		if (parties.length == 0) {
			throw new LedgerException("No participant!");
		}

		// 初始化元数据；
		this.metadata = new LedgerMetadataInfo();
		this.metadata.setSeed(initSetting.getLedgerSeed());
		// 新配置；
		this.settings = new LedgerConfiguration(initSetting.getConsensusProvider(), initSetting.getConsensusSettings(),
				initSetting.getCryptoSetting());
		this.previousSettings = new LedgerConfiguration(settings);
		this.previousSettingHash = null;
		this.adminDataHash = null;

		// 基于原配置初始化参与者列表；
		String partiPrefix = keyPrefix + LEDGER_PARTICIPANT_PREFIX;
		this.participants = new ParticipantDataset(previousSettings.getCryptoSetting(), partiPrefix, exPolicyStorage,
				versioningStorage);

		for (ParticipantNode p : parties) {
			this.participants.addConsensusParticipant(p);
		}

		String rolePrivilegePrefix = keyPrefix + ROLE_PRIVILEGE_PREFIX;
		this.rolePrivileges = new RolePrivilegeDataset(this.settings.getCryptoSetting(), rolePrivilegePrefix,
				exPolicyStorage, versioningStorage);

		String userRolePrefix = keyPrefix + USER_ROLE_PREFIX;
		this.userRoles = new UserRoleDataset(this.settings.getCryptoSetting(), userRolePrefix, exPolicyStorage,
				versioningStorage);

		// 初始化其它属性；
		this.storage = exPolicyStorage;
		this.readonly = false;
	}

	public LedgerAdminDataset(HashDigest adminAccountHash, String keyPrefix, ExPolicyKVStorage kvStorage,
			VersioningKVStorage versioningKVStorage, boolean readonly) {
		this.metaPrefix = Bytes.fromString(keyPrefix + LEDGER_META_PREFIX);
		this.settingPrefix = Bytes.fromString(keyPrefix + LEDGER_SETTING_PREFIX);
		this.storage = kvStorage;
		this.readonly = readonly;
		this.origMetadata = loadAndVerifyMetadata(adminAccountHash);
		this.metadata = new LedgerMetadataInfo(origMetadata);
		this.settings = loadAndVerifySettings(metadata.getSettingsHash());
		// 复制记录一份配置作为上一个区块的原始配置，该实例仅供读取，不做修改，也不会回写到存储；
		this.previousSettings = new LedgerConfiguration(settings);
		this.previousSettingHash = metadata.getSettingsHash();
		this.adminDataHash = adminAccountHash;

		String partiPrefix = keyPrefix + LEDGER_PARTICIPANT_PREFIX;
		this.participants = new ParticipantDataset(metadata.getParticipantsHash(), previousSettings.getCryptoSetting(),
				partiPrefix, kvStorage, versioningKVStorage, readonly);

		String rolePrivilegePrefix = keyPrefix + ROLE_PRIVILEGE_PREFIX;
		this.rolePrivileges = new RolePrivilegeDataset(metadata.getRolePrivilegesHash(),
				previousSettings.getCryptoSetting(), rolePrivilegePrefix, kvStorage, versioningKVStorage, readonly);

		String userRolePrefix = keyPrefix + USER_ROLE_PREFIX;
		this.userRoles = new UserRoleDataset(metadata.getUserRolesHash(), previousSettings.getCryptoSetting(),
				userRolePrefix, kvStorage, versioningKVStorage, readonly);
	}

	private LedgerSettings loadAndVerifySettings(HashDigest settingsHash) {
		if (settingsHash == null) {
			return null;
		}
		Bytes key = encodeSettingsKey(settingsHash);
		byte[] bytes = storage.get(key);
		HashFunction hashFunc = Crypto.getHashFunction(settingsHash.getAlgorithm());
		if (!hashFunc.verify(settingsHash, bytes)) {
			String errorMsg = "Verification of the hash for ledger setting failed! --[HASH=" + key + "]";
			LOGGER.error(errorMsg);
			throw new LedgerException(errorMsg);
		}
		return deserializeSettings(bytes);
	}

	private LedgerSettings deserializeSettings(byte[] bytes) {
		return BinaryProtocol.decode(bytes);
	}

	private byte[] serializeSetting(LedgerSettings setting) {
		return BinaryProtocol.encode(setting, LedgerSettings.class);
	}

	private LedgerMetadata_V2 loadAndVerifyMetadata(HashDigest adminAccountHash) {
		Bytes key = encodeMetadataKey(adminAccountHash);
		byte[] bytes = storage.get(key);
		HashFunction hashFunc = Crypto.getHashFunction(adminAccountHash.getAlgorithm());
		if (!hashFunc.verify(adminAccountHash, bytes)) {
			String errorMsg = "Verification of the hash for ledger metadata failed! --[HASH=" + key + "]";
			LOGGER.error(errorMsg);
			throw new LedgerException(errorMsg);
		}
		return deserializeMetadata(bytes);
	}

	private Bytes encodeSettingsKey(HashDigest settingsHash) {
		return settingPrefix.concat(settingsHash);
	}

	private Bytes encodeMetadataKey(HashDigest metadataHash) {
		// return LEDGER_META_PREFIX + metadataHash;
		// return metaPrefix + metadataHash;
		return metaPrefix.concat(metadataHash);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.LedgerAdministration#getMetadata()
	 */
	@Override
	public LedgerMetadata_V2 getMetadata() {
		return metadata;
	}

//	/**
//	 * 返回原来的账本配置；
//	 * 
//	 * <br>
//	 * 此方法总是返回从上一个区块加载的账本配置，即时调用 {@link #setLedgerSetting(LedgerSettings)} 做出了新的更改；
//	 * 
//	 * @return
//	 */
//	public LedgerSettings getPreviousSetting() {
//		return previousSettings;
//	}

	/**
	 * 返回当前设置的账本配置；
	 * 
	 * @return
	 */

	@Override
	public LedgerSettings getSettings() {
		return settings;
	}

	/**
	 * 更新账本配置；
	 * 
	 * @param ledgerSetting
	 */
	public void setLedgerSetting(LedgerSettings ledgerSetting) {
		if (readonly) {
			throw new IllegalArgumentException("This merkle dataset is readonly!");
		}
		settings = ledgerSetting;
		updated = true;
	}

	@Override
	public long getParticipantCount() {
		return participants.getParticipantCount();
	}

	@Override
	public ParticipantNode[] getParticipants() {
		return participants.getParticipants();
	}

	@Override
	public ParticipantDataset getParticipantDataset() {
		return participants;
	}

	/**
	 * 加入新的参与方； 如果指定的参与方已经存在，则引发 LedgerException 异常；
	 * 
	 * @param participant
	 */
	public void addParticipant(ParticipantNode participant) {
		participants.addConsensusParticipant(participant);
	}


	/**
	 * 更新参与方的状态参数；
	 *
	 * @param participant
	 */
	public void updateParticipant(ParticipantNode participant) {
		participants.updateConsensusParticipant(participant);
	}

	@Override
	public boolean isUpdated() {
		return updated || participants.isUpdated() || rolePrivileges.isUpdated() || userRoles.isUpdated();
	}

	@Override
	public void commit() {
		if (!isUpdated()) {
			return;
		}
		// 计算并更新参与方集合的根哈希；
		participants.commit();
		metadata.setParticipantsHash(participants.getRootHash());

		// 计算并更新角色权限集合的根哈希；
		rolePrivileges.commit();
		metadata.setRolePrivilegesHash(rolePrivileges.getRootHash());

		// 计算并更新用户角色授权集合的根哈希；
		userRoles.commit();
		metadata.setUserRolesHash(userRoles.getRootHash());

		// 当前区块上下文的密码参数设置的哈希函数；
		HashFunction hashFunc = Crypto.getHashFunction(previousSettings.getCryptoSetting().getHashAlgorithm());

		// 计算并更新参数配置的哈希；
		if (settings == null) {
			throw new LedgerException("Missing ledger settings!");
		}
		byte[] settingsBytes = serializeSetting(settings);
		HashDigest settingsHash = hashFunc.hash(settingsBytes);
		metadata.setSettingsHash(settingsHash);
		if (previousSettingHash == null || !previousSettingHash.equals(settingsHash)) {
			Bytes settingsKey = encodeSettingsKey(settingsHash);
			boolean nx = storage.set(settingsKey, settingsBytes, ExPolicy.NOT_EXISTING);
			if (!nx) {
				String base58MetadataHash = settingsHash.toBase58();
				// 有可能发生了并发写入冲突，不同的节点都向同一个存储服务器上写入数据；
				String errMsg = "Ledger metadata already exist! --[MetadataHash=" + base58MetadataHash + "]";
				LOGGER.warn(errMsg);
				throw new LedgerException(errMsg);
			}
		}

		// 基于之前的密码配置来计算元数据的哈希；
		byte[] metadataBytes = serializeMetadata(metadata);

		HashDigest metadataHash = hashFunc.hash(metadataBytes);
		if (adminDataHash == null || !adminDataHash.equals(metadataHash)) {
			// update modify;
			// String base58MetadataHash = metadataHash.toBase58();
			// String metadataKey = encodeMetadataKey(base58MetadataHash);
			Bytes metadataKey = encodeMetadataKey(metadataHash);

			boolean nx = storage.set(metadataKey, metadataBytes, ExPolicy.NOT_EXISTING);
			if (!nx) {
				String base58MetadataHash = metadataHash.toBase58();
				// 有可能发生了并发写入冲突，不同的节点都向同一个存储服务器上写入数据；
				String errMsg = "Ledger metadata already exist! --[MetadataHash=" + base58MetadataHash + "]";
				LOGGER.warn(errMsg);
				throw new LedgerException(errMsg);
			}

			adminDataHash = metadataHash;
		}

		updated = false;
	}

	private LedgerMetadata_V2 deserializeMetadata(byte[] bytes) {
		return BinaryProtocol.decode(bytes);
	}

	private byte[] serializeMetadata(LedgerMetadataInfo config) {
		return BinaryProtocol.encode(config, LedgerMetadata_V2.class);
	}

	@Override
	public void cancel() {
		if (!isUpdated()) {
			return;
		}
		participants.cancel();
		metadata =origMetadata == null ? new LedgerMetadataInfo() :  new LedgerMetadataInfo(origMetadata);
	}

	public static class LedgerMetadataInfo implements LedgerMetadata_V2 {

		private byte[] seed;

//		private LedgerSetting setting;

		private HashDigest participantsHash;

		private HashDigest settingsHash;

		private HashDigest rolePrivilegesHash;

		private HashDigest userRolesHash;

		public LedgerMetadataInfo() {
		}

		public LedgerMetadataInfo(LedgerMetadata_V2 metadata) {
			this.seed = metadata.getSeed();
			this.participantsHash = metadata.getParticipantsHash();
			this.settingsHash = metadata.getSettingsHash();
			this.rolePrivilegesHash = metadata.getRolePrivilegesHash();
			this.userRolesHash = metadata.getUserRolesHash();
		}

		@Override
		public byte[] getSeed() {
			return seed;
		}

		@Override
		public HashDigest getSettingsHash() {
			return settingsHash;
		}

		@Override
		public HashDigest getParticipantsHash() {
			return participantsHash;
		}

		@Override
		public HashDigest getRolePrivilegesHash() {
			return rolePrivilegesHash;
		}

		@Override
		public HashDigest getUserRolesHash() {
			return userRolesHash;
		}

		public void setSeed(byte[] seed) {
			this.seed = seed;
		}

		public void setSettingsHash(HashDigest settingHash) {
			this.settingsHash = settingHash;
		}

		public void setParticipantsHash(HashDigest participantsHash) {
			this.participantsHash = participantsHash;
		}

		public void setRolePrivilegesHash(HashDigest rolePrivilegesHash) {
			this.rolePrivilegesHash = rolePrivilegesHash;
		}

		public void setUserRolesHash(HashDigest userRolesHash) {
			this.userRolesHash = userRolesHash;
		}
	}

}