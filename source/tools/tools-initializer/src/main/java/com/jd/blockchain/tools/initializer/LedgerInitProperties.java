package com.jd.blockchain.tools.initializer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
import com.jd.blockchain.utils.codec.HexUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import org.springframework.core.io.ClassPathResource;

public class LedgerInitProperties {

	// 账本种子；
	public static final String LEDGER_SEED = "ledger.seed";
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
	// //共识参与方的共识服务的主机地址；
	// public static final String PART_CONSENSUS_HOST = "consensus.host";
	// // 共识参与方的共识服务的端口；
	// public static final String PART_CONSENSUS_PORT = "consensus.port";
	// // 共识参与方的共识服务是否开启安全连接；
	// public static final String PART_CONSENSUS_SECURE = "consensus.secure";
	// 共识参与方的账本初始服务的主机；
	public static final String PART_INITIALIZER_HOST = "initializer.host";
	// 共识参与方的账本初始服务的端口；
	public static final String PART_INITIALIZER_PORT = "initializer.port";
	// 共识参与方的账本初始服务是否开启安全连接；
	public static final String PART_INITIALIZER_SECURE = "initializer.secure";

	private byte[] ledgerSeed;

	private List<ConsensusParticipantConfig> consensusParticipants = new ArrayList<>();

	public byte[] getLedgerSeed() {
		return ledgerSeed;
	}

	public static byte[] getHostSettingValue() throws Exception {
		ClassPathResource hostConfigResource = new ClassPathResource("hosts.config");
		InputStream fis = hostConfigResource.getInputStream();
		ByteArrayOutputStream bos = null;
		byte[] buffer = null;
		try {
			bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			// host file to bytes
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			throw new Exception(e.getMessage(), e);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
		return buffer;
	}

	public static byte[] getSystemSettingValue() throws Exception {
		ClassPathResource systemConfigResource = new ClassPathResource("bftsmart.config");
		InputStream fis = systemConfigResource.getInputStream();
		ByteArrayOutputStream bos = null;
		byte[] buffer = null;
		try {
			bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			// file to bytes
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			throw new Exception(e.getMessage(), e);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
		return buffer;
	}

	public int getConsensusParticipantCount() {
		return consensusParticipants.size();
	}

	public List<ConsensusParticipantConfig> getConsensusParticipants() {
		return consensusParticipants;
	}

	public ConsensusParticipantConfig[] getConsensusParticipantArray() {
		return consensusParticipants.toArray(new ConsensusParticipantConfig[consensusParticipants.size()]);
	}

	/**
	 * 返回参与者；
	 * 
	 * @param address
	 *            从 1 开始； 小于等于 {@link #getConsensusParticipantCount()};
	 * @return
	 */
	public ConsensusParticipantConfig getConsensusParticipant(int id) {
		for (ConsensusParticipantConfig p : consensusParticipants) {
			if (p.getId()== id) {
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
		return resolve(props);
	}

	public static LedgerInitProperties resolve(InputStream in) {
		Properties props = FileUtils.readProperties(in, "UTF-8");
		return resolve(props);
	}

	private static LedgerInitProperties resolve(Properties props) {
		String hexLedgerSeed = getProperty(props, LEDGER_SEED).replace("-", "");
		byte[] ledgerSeed = HexUtils.decode(hexLedgerSeed);
		LedgerInitProperties setting = new LedgerInitProperties(ledgerSeed);

		int partCount = getInt(getProperty(props, PART_COUNT));
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
			parti.setName(getProperty(props, nameKey));

			String pubkeyPathKey = getKeyOfCsParti(i, PART_PUBKEY_PATH);
			parti.setPubKeyPath(getProperty(props, pubkeyPathKey));

			String pubkeyKey = getKeyOfCsParti(i, PART_PUBKEY);
			String base58PubKey = getProperty(props, pubkeyKey);
			if (base58PubKey != null) {
				PubKey pubKey = KeyGenCommand.decodePubKey(base58PubKey);
				parti.setPubKey(pubKey);
			}

			// String consensusHostKey = getKeyOfCsParti(i, PART_CONSENSUS_HOST);
			// String consensusHost = getProperty(props, consensusHostKey);
			//
			// String consensusPortKey = getKeyOfCsParti(i, PART_CONSENSUS_PORT);
			// int consensusPort = getInt(getProperty(props, consensusPortKey));
			//
			// String consensusSecureKey = getKeyOfCsParti(i, PART_CONSENSUS_SECURE);
			// boolean consensusSecure = Boolean.parseBoolean(getProperty(props,
			// consensusSecureKey));
			// NetworkAddress consensusAddress = new NetworkAddress(consensusHost,
			// consensusPort, consensusSecure);
			// parti.setConsensusAddress(consensusAddress);

			String initializerHostKey = getKeyOfCsParti(i, PART_INITIALIZER_HOST);
			String initializerHost = getProperty(props, initializerHostKey);

			String initializerPortKey = getKeyOfCsParti(i, PART_INITIALIZER_PORT);
			int initializerPort = getInt(getProperty(props, initializerPortKey));

			String initializerSecureKey = getKeyOfCsParti(i, PART_INITIALIZER_SECURE);
			boolean initializerSecure = Boolean.parseBoolean(getProperty(props, initializerSecureKey));
			NetworkAddress initializerAddress = new NetworkAddress(initializerHost, initializerPort, initializerSecure);
			parti.setInitializerAddress(initializerAddress);

			setting.addConsensusParticipant(parti);
		}

		return setting;
	}

	private static String getProperty(Properties props, String key) {
		return getProperty(props, key, true);
	}

	private static String getProperty(Properties props, String key, boolean required) {
		String value = props.getProperty(key);
		if (value == null) {
			if (required) {
				throw new IllegalArgumentException("Miss property[" + key + "]!");
			}
			return null;
		}
		value = value.trim();
		return value.length() == 0 ? null : value;
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

		private String pubKeyPath;

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

		public String getPubKeyPath() {
			return pubKeyPath;
		}

		public void setPubKeyPath(String pubKeyPath) {
			this.pubKeyPath = pubKeyPath;
		}

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
