package com.jd.blockchain.ledger.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

public class LedgerAdminAccount implements Transactional, LedgerAdministration {

	static {
		DataContractRegistry.register(LedgerMetadata.class);
	}

	private static Logger LOGGER = LoggerFactory.getLogger(LedgerAdminAccount.class);

	public static final String LEDGER_META_PREFIX = "MTA" + LedgerConsts.KEY_SEPERATOR;
	public static final String LEDGER_PARTICIPANT_PREFIX = "PAR" + LedgerConsts.KEY_SEPERATOR;
	public static final String LEDGER_PRIVILEGE_PREFIX = "PVL" + LedgerConsts.KEY_SEPERATOR;

	private final Bytes metaPrefix;
	private final Bytes privilegePrefix;

	private LedgerMetadata origMetadata;

	private LedgerMetadataImpl metadata;

	/**
	 * 原来的账本设置；
	 * 
	 * <br>
	 * 对 LedgerMetadata 修改的新配置不能立即生效，需要达成共识后，在下一次区块计算中才生效；
	 */
	private LedgerSetting previousSetting;

	/**
	 * 账本的参与节点；
	 */
	private ParticipantDataSet participants;

	// /**
	// * 账本的全局权限设置；
	// */
	// private PrivilegeDataSet privileges;

	private ExPolicyKVStorage settingsStorage;

	private HashDigest adminAccountHash;

	private boolean readonly;

	private boolean updated;

	public HashDigest getHash() {
		return adminAccountHash;
	}

	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * 初始化账本的管理账户；
	 * 
	 * <br>
	 * 
	 * 只在新建账本时调用此方法；
	 * 
	 * @param ledgerSeed
	 * @param setting
	 * @param partiList
	 * @param exPolicyStorage
	 * @param versioningStorage
	 */
	public LedgerAdminAccount(LedgerInitSetting initSetting, String keyPrefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage versioningStorage) {
		this.metaPrefix = Bytes.fromString(keyPrefix + LEDGER_META_PREFIX);
		this.privilegePrefix = Bytes.fromString(keyPrefix + LEDGER_PRIVILEGE_PREFIX);

		ParticipantNode[] parties = initSetting.getConsensusParticipants();
		if (parties.length == 0) {
			throw new LedgerException("No participant!");
		}

		// 检查参与者列表是否已经按照 id 升序排列，并且 id 不冲突；
		// 注：参与者的 id 要求从 0 开始编号，顺序依次递增，不允许跳空；
		for (int i = 0; i < parties.length; i++) {
			// if (parties[i].getAddress() != i) {
			// throw new LedgerException("The id of participant isn't match the order of the
			// participant list!");
			// }
		}

		// 初始化元数据；
		this.metadata = new LedgerMetadataImpl();
		this.metadata.setSeed(initSetting.getLedgerSeed());
		// 新配置；
		this.metadata.setting = new LedgerConfiguration(initSetting.getConsensusProvider(),
				initSetting.getConsensusSettings(), initSetting.getCryptoSetting());
		this.previousSetting = new LedgerConfiguration(initSetting.getConsensusProvider(),
				initSetting.getConsensusSettings(), initSetting.getCryptoSetting());
		this.adminAccountHash = null;

		// 基于原配置初始化参与者列表；
		String partiPrefix = keyPrefix + LEDGER_PARTICIPANT_PREFIX;
		this.participants = new ParticipantDataSet(previousSetting.getCryptoSetting(), partiPrefix, exPolicyStorage,
				versioningStorage);

		for (ParticipantNode p : parties) {
			this.participants.addConsensusParticipant(p);
		}

		// 初始化其它属性；
		this.settingsStorage = exPolicyStorage;
		this.readonly = false;
	}

	public LedgerAdminAccount(HashDigest adminAccountHash, String keyPrefix, ExPolicyKVStorage kvStorage,
			VersioningKVStorage versioningKVStorage, boolean readonly) {
		this.metaPrefix = Bytes.fromString(keyPrefix + LEDGER_META_PREFIX);
		this.privilegePrefix = Bytes.fromString(keyPrefix + LEDGER_PRIVILEGE_PREFIX);
		this.settingsStorage = kvStorage;
		this.readonly = readonly;
		this.origMetadata = loadAndVerifySettings(adminAccountHash);
		this.metadata = new LedgerMetadataImpl(origMetadata);
		// 复制记录一份配置作为上一个区块的原始配置，该实例仅供读取，不做修改，也不会回写到存储；
		this.previousSetting = new LedgerConfiguration(metadata.getSetting());
		this.adminAccountHash = adminAccountHash;
		// this.privileges = new PrivilegeDataSet(metadata.getPrivilegesHash(),
		// metadata.getSetting().getCryptoSetting(),
		// PrefixAppender.prefix(LEDGER_PRIVILEGE_PREFIX, kvStorage),
		// PrefixAppender.prefix(LEDGER_PRIVILEGE_PREFIX, versioningKVStorage),
		// readonly);

		// this.participants = new ParticipantDataSet(metadata.getParticipantsHash(),
		// previousSetting.getCryptoSetting(),
		// PrefixAppender.prefix(LEDGER_PARTICIPANT_PREFIX, kvStorage),
		// PrefixAppender.prefix(LEDGER_PARTICIPANT_PREFIX, versioningKVStorage),
		// readonly);
		String partiPrefix = keyPrefix + LEDGER_PARTICIPANT_PREFIX;
		this.participants = new ParticipantDataSet(metadata.getParticipantsHash(), previousSetting.getCryptoSetting(),
				partiPrefix, kvStorage, versioningKVStorage, readonly);
	}

	private LedgerMetadata loadAndVerifySettings(HashDigest adminAccountHash) {
		// String base58Hash = adminAccountHash.toBase58();
		// String key = encodeMetadataKey(base58Hash);
		Bytes key = encodeMetadataKey(adminAccountHash);
		byte[] bytes = settingsStorage.get(key);
		HashFunction hashFunc = CryptoServiceProviders.getHashFunction(adminAccountHash.getAlgorithm());
		if (!hashFunc.verify(adminAccountHash, bytes)) {
			LOGGER.error("The hash verification of ledger settings fail! --[HASH=" + key + "]");
			throw new LedgerException("The hash verification of ledger settings fail!");
		}
		return deserializeMetadata(bytes);
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
	public LedgerMetadata getMetadata() {
		return metadata;
	}

	/**
	 * 返回原来的账本配置；
	 * 
	 * <br>
	 * 此方法总是返回从上一个区块加载的账本配置，即时调用 {@link #setLedgerSetting(LedgerSetting)} 做出了新的更改；
	 * 
	 * @return
	 */
	public LedgerSetting getPreviousSetting() {
		return previousSetting;
	}

	/**
	 * 返回当前设置的账本配置；
	 * 
	 * @return
	 */
	public LedgerSetting getSetting() {
		return metadata.getSetting();
	}

	/**
	 * 更新账本配置；
	 * 
	 * @param ledgerSetting
	 */
	public void setLedgerSetting(LedgerSetting ledgerSetting) {
		if (readonly) {
			throw new IllegalArgumentException("This merkle dataset is readonly!");
		}
		metadata.setSetting(ledgerSetting);
	}

	@Override
	public long getParticipantCount() {
		return participants.getParticipantCount();
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// com.jd.blockchain.ledger.core.LedgerAdministration#getParticipant(java.lang.
	// * String)
	// */
	// @Override
	// public ParticipantNode getParticipant(int id) {
	// return participants.getParticipant(id);
	// }

	@Override
	public ParticipantNode[] getParticipants() {
		return participants.getParticipants();
	}

	/**
	 * 加入新的参与方； 如果指定的参与方已经存在，则引发 LedgerException 异常；
	 * 
	 * @param participant
	 */
	public void addParticipant(ParticipantNode participant) {
		participants.addConsensusParticipant(participant);
	}

	@Override
	public boolean isUpdated() {
		return updated || participants.isUpdated();
	}

	@Override
	public void commit() {
		if (!isUpdated()) {
			return;
		}
		participants.commit();

		metadata.setParticipantsHash(participants.getRootHash());

		// 基于之前的密码配置来计算元数据的哈希；
		byte[] metadataBytes = serializeMetadata(metadata);
		HashFunction hashFunc = CryptoServiceProviders
				.getHashFunction(previousSetting.getCryptoSetting().getHashAlgorithm());
		HashDigest metadataHash = hashFunc.hash(metadataBytes);
		if (adminAccountHash == null || !adminAccountHash.equals(metadataHash)) {
			// update modify;
			// String base58MetadataHash = metadataHash.toBase58();
			// String metadataKey = encodeMetadataKey(base58MetadataHash);
			Bytes metadataKey = encodeMetadataKey(metadataHash);

			boolean nx = settingsStorage.set(metadataKey, metadataBytes, ExPolicy.NOT_EXISTING);
			if (!nx) {
				// 有可能发生了并发写入冲突，不同的节点都向同一个存储服务器上写入数据；
				// throw new LedgerException(
				// "Ledger metadata already exist! --[LedgerMetadataHash=" + base58MetadataHash
				// + "]");
				// LOGGER.warn("Ledger metadata already exist! --[MetadataHash=" +
				// base58MetadataHash + "]");
			}

			adminAccountHash = metadataHash;
		}

		updated = false;
	}

	private LedgerMetadata deserializeMetadata(byte[] bytes) {
		return BinaryEncodingUtils.decode(bytes);
	}

	private byte[] serializeMetadata(LedgerMetadataImpl config) {
		return BinaryEncodingUtils.encode(config, LedgerMetadata.class);
	}

	@Override
	public void cancel() {
		if (!isUpdated()) {
			return;
		}
		participants.cancel();
		metadata = new LedgerMetadataImpl(origMetadata);
	}

	public static class LedgerMetadataImpl implements LedgerMetadata {

		private byte[] seed;

		private LedgerSetting setting;

		private HashDigest participantsHash;

		public LedgerMetadataImpl() {
		}

		public LedgerMetadataImpl(LedgerMetadata metadata) {
			this.seed = metadata.getSeed();
			this.setting = metadata.getSetting();
			this.participantsHash = metadata.getParticipantsHash();
		}

		@Override
		public byte[] getSeed() {
			return seed;
		}

		@Override
		public LedgerSetting getSetting() {
			return setting;
		}

		@Override
		public HashDigest getParticipantsHash() {
			return participantsHash;
		}

		public void setSeed(byte[] seed) {
			this.seed = seed;
		}

		public void setSetting(LedgerSetting setting) {
			// copy a new instance;
			this.setting = new LedgerConfiguration(setting);
		}

		public void setParticipantsHash(HashDigest participantsHash) {
			this.participantsHash = participantsHash;
		}
	}

}