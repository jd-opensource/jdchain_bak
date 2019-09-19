package com.jd.blockchain.ledger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.jd.blockchain.consts.Global;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.LedgerInitProperties.CryptoProperties;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.PropertiesUtils;
import com.jd.blockchain.utils.StringUtils;
import com.jd.blockchain.utils.codec.HexUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

public class LedgerInitProperties {

	// 账本种子；
	public static final String LEDGER_SEED = "ledger.seed";

	// 账本名称
	public static final String LEDGER_NAME = "ledger.name";

	// 声明的账本建立时间；
	public static final String CREATED_TIME = "created-time";
	// 创建时间的格式；
	public static final String CREATED_TIME_FORMAT = Global.DEFAULT_TIME_FORMAT;

	// 角色清单；
	public static final String ROLES = "security.roles";
	// 角色的账本权限；用角色名称替代占位符；
	public static final String ROLE_LEDGER_PRIVILEGES_PATTERN = "security.role.%s.ledger-privileges";
	// 角色的交易权限；用角色名称替代占位符；
	public static final String ROLE_TX_PRIVILEGES_PATTERN = "security.role.%s.tx-privileges";

	// 共识参与方的个数，后续以 part.id 分别标识每一个参与方的配置；
	public static final String PART_COUNT = "cons_parti.count";
	// 共识参与方的名称的模式；
	public static final String PART_ID_PATTERN = "cons_parti.%s";
	// 参与方的名称；
	public static final String PART_NAME = "name";
	// 参与方的公钥文件路径；
	public static final String PART_PUBKEY_PATH = "pubkey-path";
	// 参与方的公钥文件路径；
	public static final String PART_PUBKEY = "pubkey";
	// 参与方的角色清单；
	public static final String PART_ROLES = "roles";
	// 参与方的角色权限策略；
	public static final String PART_ROLES_POLICY = "roles-policy";

	// 共识参与方的账本初始服务的主机；
	public static final String PART_INITIALIZER_HOST = "initializer.host";
	// 共识参与方的账本初始服务的端口；
	public static final String PART_INITIALIZER_PORT = "initializer.port";
	// 共识参与方的账本初始服务是否开启安全连接；
	public static final String PART_INITIALIZER_SECURE = "initializer.secure";

	// 共识服务的参数配置；必须；
	public static final String CONSENSUS_CONFIG = "consensus.conf";

	// 共识服务提供者；必须；
	public static final String CONSENSUS_SERVICE_PROVIDER = "consensus.service-provider";

	// 密码服务提供者列表，以英文逗点“,”分隔；必须；
	public static final String CRYPTO_SERVICE_PROVIDERS = "crypto.service-providers";
	// 从存储中加载账本数据时，是否校验哈希；可选；
	public static final String CRYPTO_VRIFY_HASH = "crypto.verify-hash";
	// 哈希算法；
	public static final String CRYPTO_HASH_ALGORITHM = "crypto.hash-algorithm";

	public static final String CRYPTO_SERVICE_PROVIDERS_SPLITTER = ",";

	private byte[] ledgerSeed;

	private String ledgerName;

	private RoleInitData[] roles;

	private List<ParticipantProperties> consensusParticipants = new ArrayList<>();

	private String consensusProvider;

	private Properties consensusConfig;

//	private String[] cryptoProviders;

	private CryptoProperties cryptoProperties = new CryptoProperties();

	private long createdTime;

	public byte[] getLedgerSeed() {
		return ledgerSeed.clone();
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public Properties getConsensusConfig() {
		return consensusConfig;
	}

	public String getConsensusProvider() {
		return consensusProvider;
	}

	public int getConsensusParticipantCount() {
		return consensusParticipants.size();
	}

	public List<ParticipantProperties> getConsensusParticipants() {
		return consensusParticipants;
	}

	public ParticipantNode[] getConsensusParticipantNodes() {
		if (consensusParticipants.isEmpty()) {
			return null;
		}
		ParticipantNode[] participantNodes = new ParticipantNode[consensusParticipants.size()];
		return consensusParticipants.toArray(participantNodes);
	}

	public CryptoProperties getCryptoProperties() {
		return cryptoProperties;
	}

	public void setCryptoProperties(CryptoProperties cryptoProperties) {
		if (cryptoProperties == null) {
			cryptoProperties = new CryptoProperties();
		}
		this.cryptoProperties = cryptoProperties;
	}

	/**
	 * 返回参与者；
	 * 
	 * @param id 从 1 开始； 小于等于 {@link #getConsensusParticipantCount()};
	 * @return
	 */
	public ParticipantProperties getConsensusParticipant(int id) {
		for (ParticipantProperties p : consensusParticipants) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 私有的构造器；
	 * 
	 * @param ledgerSeed
	 */
	private LedgerInitProperties(byte[] ledgerSeed) {
		this.ledgerSeed = ledgerSeed;
	}

	public void addConsensusParticipant(ParticipantProperties participant) {
		consensusParticipants.add(participant);
	}

	private static String getKeyOfParticipant(int partId, String partPropKey) {
		String partAddrStr = String.format(PART_ID_PATTERN, partId);
		return String.format("%s.%s", partAddrStr, partPropKey);
	}

	public static LedgerInitProperties resolve(String initSettingFile) {
		Properties props = FileUtils.readProperties(initSettingFile, "UTF-8");
		File realFile = new File(initSettingFile);
		return resolve(realFile.getParentFile().getPath(), props);
	}

	public static LedgerInitProperties resolve(InputStream in) {
		Properties props = FileUtils.readProperties(in, "UTF-8");
		return resolve(props);
	}

	public static LedgerInitProperties resolve(Properties props) {
		return resolve(null, props);
	}

	/**
	 * 从属性表解析账本初始化参数；
	 * 
	 * @param baseDirectory 基础路径；属性中涉及文件位置的相对路径以此参数指定的目录为父目录；
	 * @param props         要解析的属性表；
	 * @return
	 */
	public static LedgerInitProperties resolve(String baseDirectory, Properties props) {
		String hexLedgerSeed = PropertiesUtils.getRequiredProperty(props, LEDGER_SEED).replace("-", "");
		byte[] ledgerSeed = HexUtils.decode(hexLedgerSeed);
		LedgerInitProperties initProps = new LedgerInitProperties(ledgerSeed);

		// 解析账本信息；
		// 账本名称
		String ledgerName = PropertiesUtils.getRequiredProperty(props, LEDGER_NAME);
		initProps.ledgerName = ledgerName;

		// 创建时间；
		String strCreatedTime = PropertiesUtils.getRequiredProperty(props, CREATED_TIME);
		try {
			initProps.createdTime = new SimpleDateFormat(CREATED_TIME_FORMAT).parse(strCreatedTime).getTime();
		} catch (ParseException ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex);
		}

		// 解析角色清单；
		String strRoleNames = PropertiesUtils.getOptionalProperty(props, ROLES);
		String[] roles = StringUtils.splitToArray(strRoleNames, ",");

		Map<String, RoleInitData> rolesInitSettingMap = new TreeMap<String, RoleInitData>();
		// 解析角色权限表；
		for (String role : roles) {
			String ledgerPrivilegeKey = getKeyOfRoleLedgerPrivilege(role);
			String strLedgerPermissions = PropertiesUtils.getOptionalProperty(props, ledgerPrivilegeKey);
			LedgerPermission[] ledgerPermissions = resolveLedgerPermissions(strLedgerPermissions);

			String txPrivilegeKey = getKeyOfRoleTxPrivilege(role);
			String strTxPermissions = PropertiesUtils.getOptionalProperty(props, txPrivilegeKey);
			TransactionPermission[] txPermissions = resolveTransactionPermissions(strTxPermissions);

			if (ledgerPermissions.length > 0 || txPermissions.length > 0) {
				RoleInitData rolesSettings = new RoleInitData(role, ledgerPermissions, txPermissions);
				rolesInitSettingMap.put(role, rolesSettings);
			}
		}
		RoleInitData[] rolesInitDatas = rolesInitSettingMap.values()
				.toArray(new RoleInitData[rolesInitSettingMap.size()]);
		initProps.setRoles(rolesInitDatas);

		// 解析共识相关的属性；
		initProps.consensusProvider = PropertiesUtils.getRequiredProperty(props, CONSENSUS_SERVICE_PROVIDER);
		String consensusConfigFilePath = PropertiesUtils.getRequiredProperty(props, CONSENSUS_CONFIG);
		try {
			File consensusConfigFile = FileUtils.getFile(baseDirectory, consensusConfigFilePath);
			initProps.consensusConfig = FileUtils.readProperties(consensusConfigFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(
					String.format("Consensus config file[%s] doesn't exist! ", consensusConfigFilePath), e);
		}

		// 解析密码提供者列表；
		String cryptoProviderNames = PropertiesUtils.getProperty(props, CRYPTO_SERVICE_PROVIDERS, true);
		String[] cryptoProviders = cryptoProviderNames.split(CRYPTO_SERVICE_PROVIDERS_SPLITTER);
		for (int i = 0; i < cryptoProviders.length; i++) {
			cryptoProviders[i] = cryptoProviders[i].trim();
		}
		initProps.cryptoProperties.setProviders(cryptoProviders);
		// 哈希校验选项；
		boolean verifyHash = PropertiesUtils.getBooleanOptional(props, CRYPTO_VRIFY_HASH, false);
		initProps.cryptoProperties.setVerifyHash(verifyHash);
		// 哈希算法；
		String hashAlgorithm = PropertiesUtils.getOptionalProperty(props, CRYPTO_HASH_ALGORITHM);
		initProps.cryptoProperties.setHashAlgorithm(hashAlgorithm);
		

		// 解析参与方节点列表；
		int partCount = getInt(PropertiesUtils.getRequiredProperty(props, PART_COUNT));
		if (partCount < 0) {
			throw new IllegalArgumentException(String.format("Property[%s] is negative!", PART_COUNT));
		}
		if (partCount < 4) {
			throw new IllegalArgumentException(String.format("Property[%s] is less than 4!", PART_COUNT));
		}
		for (int i = 0; i < partCount; i++) {
			ParticipantProperties parti = new ParticipantProperties();

			parti.setId(i);

			String nameKey = getKeyOfParticipant(i, PART_NAME);
			parti.setName(PropertiesUtils.getRequiredProperty(props, nameKey));

			String pubkeyPathKey = getKeyOfParticipant(i, PART_PUBKEY_PATH);
			String pubkeyPath = PropertiesUtils.getProperty(props, pubkeyPathKey, false);

			String pubkeyKey = getKeyOfParticipant(i, PART_PUBKEY);
			String base58PubKey = PropertiesUtils.getProperty(props, pubkeyKey, false);
			if (base58PubKey != null) {
				PubKey pubKey = KeyGenUtils.decodePubKey(base58PubKey);
				parti.setPubKey(pubKey);
			} else if (pubkeyPath != null) {
				PubKey pubKey = KeyGenUtils.readPubKey(pubkeyPath);
				parti.setPubKey(pubKey);
			} else {
				throw new IllegalArgumentException(
						String.format("Property[%s] and property[%s] are all empty!", pubkeyKey, pubkeyPathKey));
			}

			// 解析参与方的角色权限配置；
			String partiRolesKey = getKeyOfParticipant(i, PART_ROLES);
			String strPartiRoles = PropertiesUtils.getOptionalProperty(props, partiRolesKey);
			String[] partiRoles = StringUtils.splitToArray(strPartiRoles, ",");
			parti.setRoles(partiRoles);

			String partiRolePolicyKey = getKeyOfParticipant(i, PART_ROLES_POLICY);
			String strPartiPolicy = PropertiesUtils.getOptionalProperty(props, partiRolePolicyKey);
			RolesPolicy policy = strPartiPolicy == null ? RolesPolicy.UNION
					: RolesPolicy.valueOf(strPartiPolicy.trim());
			policy = policy == null ? RolesPolicy.UNION : policy;
			parti.setRolesPolicy(policy);

			// 解析参与方的网络配置参数；
			String initializerHostKey = getKeyOfParticipant(i, PART_INITIALIZER_HOST);
			String initializerHost = PropertiesUtils.getRequiredProperty(props, initializerHostKey);

			String initializerPortKey = getKeyOfParticipant(i, PART_INITIALIZER_PORT);
			int initializerPort = getInt(PropertiesUtils.getRequiredProperty(props, initializerPortKey));

			String initializerSecureKey = getKeyOfParticipant(i, PART_INITIALIZER_SECURE);
			boolean initializerSecure = Boolean
					.parseBoolean(PropertiesUtils.getRequiredProperty(props, initializerSecureKey));
			NetworkAddress initializerAddress = new NetworkAddress(initializerHost, initializerPort, initializerSecure);
			parti.setInitializerAddress(initializerAddress);
			parti.setParticipantNodeState(ParticipantNodeState.ACTIVED);
			initProps.addConsensusParticipant(parti);
		}

		return initProps;
	}

	private static TransactionPermission[] resolveTransactionPermissions(String strTxPermissions) {
		String[] strPermissions = StringUtils.splitToArray(strTxPermissions, ",");
		List<TransactionPermission> permissions = new ArrayList<TransactionPermission>();
		if (strPermissions != null) {
			for (String pm : strPermissions) {
				TransactionPermission permission = TransactionPermission.valueOf(pm);
				if (permission != null) {
					permissions.add(permission);
				}
			}
		}
		return permissions.toArray(new TransactionPermission[permissions.size()]);
	}

	private static LedgerPermission[] resolveLedgerPermissions(String strLedgerPermissions) {
		String[] strPermissions = StringUtils.splitToArray(strLedgerPermissions, ",");
		List<LedgerPermission> permissions = new ArrayList<LedgerPermission>();
		if (strPermissions != null) {
			for (String pm : strPermissions) {
				LedgerPermission permission = LedgerPermission.valueOf(pm);
				if (permission != null) {
					permissions.add(permission);
				}
			}
		}
		return permissions.toArray(new LedgerPermission[permissions.size()]);
	}

	private static String getKeyOfRoleLedgerPrivilege(String role) {
		return String.format(ROLE_LEDGER_PRIVILEGES_PATTERN, role);
	}

	private static String getKeyOfRoleTxPrivilege(String role) {
		return String.format(ROLE_TX_PRIVILEGES_PATTERN, role);
	}

	private static int getInt(String strInt) {
		return Integer.parseInt(strInt.trim());
	}

	public RoleInitData[] getRoles() {
		return roles;
	}

	public void setRoles(RoleInitData[] roles) {
		this.roles = roles;
	}

	public static class CryptoProperties {

		private String[] providers;

		private boolean verifyHash;

		private String hashAlgorithm;

		public String[] getProviders() {
			return providers;
		}

		public void setProviders(String[] providers) {
			this.providers = providers;
		}

		public boolean isVerifyHash() {
			return verifyHash;
		}

		public void setVerifyHash(boolean verifyHash) {
			this.verifyHash = verifyHash;
		}

		public String getHashAlgorithm() {
			return hashAlgorithm;
		}

		public void setHashAlgorithm(String hashAlgorithm) {
			this.hashAlgorithm = hashAlgorithm;
		}

	}

	/**
	 * 参与方配置信息；
	 * 
	 * @author huanghaiquan
	 *
	 */
	public static class ParticipantProperties implements ParticipantNode {

		private int id;

		private Bytes address;

		private String name;

		private PubKey pubKey;

		private String[] roles;

		private RolesPolicy rolesPolicy;

		// private NetworkAddress consensusAddress;

		private ParticipantNodeState participantNodeState;

		private NetworkAddress initializerAddress;

		@Override
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public Bytes getAddress() {
			return address;
		}

		@Override
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

//		public String getPubKeyPath() {
//			return pubKeyPath;
//		}
//
//		public void setPubKeyPath(String pubKeyPath) {
//			this.pubKeyPath = pubKeyPath;
//		}

		@Override
		public ParticipantNodeState getParticipantNodeState() {
			return participantNodeState;
		}

		public void setParticipantNodeState(ParticipantNodeState participantNodeState) {
			this.participantNodeState = participantNodeState;
		}

		public NetworkAddress getInitializerAddress() {
			return initializerAddress;
		}

		public void setInitializerAddress(NetworkAddress initializerAddress) {
			this.initializerAddress = initializerAddress;
		}

		@Override
		public PubKey getPubKey() {
			return pubKey;
		}

		public void setPubKey(PubKey pubKey) {
			this.pubKey = pubKey;
			this.address = AddressEncoding.generateAddress(pubKey);
		}

		public String[] getRoles() {
			return roles;
		}

		public void setRoles(String[] roles) {
			this.roles = roles;
		}

		public RolesPolicy getRolesPolicy() {
			return rolesPolicy;
		}

		public void setRolesPolicy(RolesPolicy rolesPolicy) {
			this.rolesPolicy = rolesPolicy;
		}

	}

}
