package com.jd.blockchain.tools.initializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
import com.jd.blockchain.utils.PropertiesUtils;
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
	public static final String CREATED_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";

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

	public static final String CRYPTO_SERVICE_PROVIDERS_SPLITTER = ",";

	private byte[] ledgerSeed;

	private String ledgerName;

	private List<ConsensusParticipantConfig> consensusParticipants = new ArrayList<>();

	private String consensusProvider;

	private Properties consensusConfig;

	private String[] cryptoProviders;

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

	public List<ConsensusParticipantConfig> getConsensusParticipants() {
		return consensusParticipants;
	}

	public ParticipantNode[] getConsensusParticipantNodes() {
		if (consensusParticipants.isEmpty()) {
			return null;
		}
		ParticipantNode[] participantNodes = new ParticipantNode[consensusParticipants.size()];
		return consensusParticipants.toArray(participantNodes);
	}

	public String[] getCryptoProviders() {
		return cryptoProviders.clone();
	}

	public void setCryptoProviders(String[] cryptoProviders) {
		this.cryptoProviders = cryptoProviders;
	}

	/**
	 * 返回参与者；
	 * 
	 * @param id 从 1 开始； 小于等于 {@link #getConsensusParticipantCount()};
	 * @return
	 */
	public ConsensusParticipantConfig getConsensusParticipant(int id) {
		for (ConsensusParticipantConfig p : consensusParticipants) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	private LedgerInitProperties(byte[] ledgerSeed) {
		this.ledgerSeed = ledgerSeed;
	}

	public void addConsensusParticipant(ConsensusParticipantConfig participant) {
		consensusParticipants.add(participant);
	}

	private static String getKeyOfCsParti(int partId, String partPropKey) {
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

	public static LedgerInitProperties resolve(String dir, Properties props) {
		String hexLedgerSeed = PropertiesUtils.getRequiredProperty(props, LEDGER_SEED).replace("-", "");
		byte[] ledgerSeed = HexUtils.decode(hexLedgerSeed);
		LedgerInitProperties initProps = new LedgerInitProperties(ledgerSeed);

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

		// 解析共识相关的属性；
		initProps.consensusProvider = PropertiesUtils.getRequiredProperty(props, CONSENSUS_SERVICE_PROVIDER);
		String consensusConfigFilePath = PropertiesUtils.getRequiredProperty(props, CONSENSUS_CONFIG);
		try {
			File consensusConfigFile = FileUtils.getFile(dir, consensusConfigFilePath);
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
		initProps.cryptoProviders = cryptoProviders;

		// 解析参与方节点列表；
		int partCount = getInt(PropertiesUtils.getRequiredProperty(props, PART_COUNT));
		if (partCount < 0) {
			throw new IllegalArgumentException(String.format("Property[%s] is negative!", PART_COUNT));
		}
		if (partCount < 4) {
			throw new IllegalArgumentException(String.format("Property[%s] is less than 4!", PART_COUNT));
		}
		for (int i = 0; i < partCount; i++) {
			ConsensusParticipantConfig parti = new ConsensusParticipantConfig();

			parti.setId(i);

			String nameKey = getKeyOfCsParti(i, PART_NAME);
			parti.setName(PropertiesUtils.getRequiredProperty(props, nameKey));

			String pubkeyPathKey = getKeyOfCsParti(i, PART_PUBKEY_PATH);
			String pubkeyPath = PropertiesUtils.getProperty(props, pubkeyPathKey, false);

			String pubkeyKey = getKeyOfCsParti(i, PART_PUBKEY);
			String base58PubKey = PropertiesUtils.getProperty(props, pubkeyKey, false);
			if (base58PubKey != null) {
				PubKey pubKey = KeyGenCommand.decodePubKey(base58PubKey);
				parti.setPubKey(pubKey);
			} else if (pubkeyPath != null) {
				PubKey pubKey = KeyGenCommand.readPubKey(pubkeyPath);
				parti.setPubKey(pubKey);
			} else {
				throw new IllegalArgumentException(
						String.format("Property[%s] and property[%s] are all empty!", pubkeyKey, pubkeyPathKey));
			}

			String initializerHostKey = getKeyOfCsParti(i, PART_INITIALIZER_HOST);
			String initializerHost = PropertiesUtils.getRequiredProperty(props, initializerHostKey);

			String initializerPortKey = getKeyOfCsParti(i, PART_INITIALIZER_PORT);
			int initializerPort = getInt(PropertiesUtils.getRequiredProperty(props, initializerPortKey));

			String initializerSecureKey = getKeyOfCsParti(i, PART_INITIALIZER_SECURE);
			boolean initializerSecure = Boolean
					.parseBoolean(PropertiesUtils.getRequiredProperty(props, initializerSecureKey));
			NetworkAddress initializerAddress = new NetworkAddress(initializerHost, initializerPort, initializerSecure);
			parti.setInitializerAddress(initializerAddress);

			initProps.addConsensusParticipant(parti);
		}

		return initProps;
	}

	private static int getInt(String strInt) {
		return Integer.parseInt(strInt.trim());
	}

	/**
	 * 参与方配置信息；
	 * 
	 * @author huanghaiquan
	 *
	 */
	public static class ConsensusParticipantConfig implements ParticipantNode {

		private int id;

		private String address;

		private String name;

//		private String pubKeyPath;

		private PubKey pubKey;

		// private NetworkAddress consensusAddress;

		private NetworkAddress initializerAddress;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String getAddress() {
			return address;
		}

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

		public NetworkAddress getInitializerAddress() {
			return initializerAddress;
		}

		public void setInitializerAddress(NetworkAddress initializerAddress) {
			this.initializerAddress = initializerAddress;
		}

		public PubKey getPubKey() {
			return pubKey;
		}

		public void setPubKey(PubKey pubKey) {
			this.pubKey = pubKey;
			this.address = AddressEncoding.generateAddress(pubKey).toBase58();
		}

	}

}
