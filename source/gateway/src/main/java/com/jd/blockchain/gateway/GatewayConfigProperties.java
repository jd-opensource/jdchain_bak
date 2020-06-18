package com.jd.blockchain.gateway;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import com.jd.blockchain.utils.IllegalDataException;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

public class GatewayConfigProperties {

	// HTTP协议相关配置项的键的前缀；
	public static final String HTTP_PREFIX = "http.";
	// 网关的HTTP服务地址；
	public static final String HTTP_HOST = HTTP_PREFIX + "host";
	// 网关的HTTP服务端口；
	public static final String HTTP_PORT = HTTP_PREFIX + "port";
	// 网关的HTTP服务上下文路径，可选；
	public static final String HTTP_CONTEXT_PATH = HTTP_PREFIX + "context-path";

	// 共识相关配置项的键的前缀；
	public static final String PEER_PREFIX = "peer.";
	// 共识节点的数量
	public static final String PEER_SIZE = PEER_PREFIX + "size";
	// 共识节点的服务地址；
	public static final String PEER_HOST_FORMAT = PEER_PREFIX + "%s.host";
	// 共识节点的服务端口；
	public static final String PEER_PORT_FORMAT = PEER_PREFIX + "%s.port";
	// 共识节点的服务是否启用安全证书；
	public static final String PEER_SECURE_FORMAT = PEER_PREFIX + "%s.secure";
	// 支持共识的Provider列表，以英文逗号分隔
	public static final String PEER_PROVIDERS = PEER_PREFIX + "providers";

	// 数据检索服务URL地址
	public static final String DATA_RETRIEVAL_URL="data.retrieval.url";
	public static final String SCHEMA_RETRIEVAL_URL="schema.retrieval.url";

	// 密钥相关配置项的键的前缀；
	public static final String KEYS_PREFIX = "keys.";
	// 默认密钥相关配置项的键的前缀；
	public static final String DEFAULT_KEYS_PREFIX = KEYS_PREFIX + "default.";
	// 默认私钥的内容；
	public static final String DEFAULT_PUBKEY = DEFAULT_KEYS_PREFIX + "pubkey";
	// 默认私钥的文件存储路径；
	public static final String DEFAULT_PRIVKEY_PATH = DEFAULT_KEYS_PREFIX + "privkey-path";
	// 默认私钥的内容；
	public static final String DEFAULT_PRIVKEY = DEFAULT_KEYS_PREFIX + "privkey";
	// 默认私钥的密码；
	public static final String DEFAULT_PK_PWD = DEFAULT_KEYS_PREFIX + "privkey-password";


	private HttpConfig http = new HttpConfig();

	private ProviderConfig providerConfig = new ProviderConfig();

	private Set<NetworkAddress> masterPeerAddresses = new HashSet<>();

	private String dataRetrievalUrl;
	private String schemaRetrievalUrl;

	private KeysConfig keys = new KeysConfig();

	public HttpConfig http() {
		return http;
	}

	public Set<NetworkAddress> masterPeerAddresses() {
		return masterPeerAddresses;
	}

	public String dataRetrievalUrl() {
		return this.dataRetrievalUrl;
	}

	public void setDataRetrievalUrl(String dataRetrievalUrl) {
		this.dataRetrievalUrl = dataRetrievalUrl;
	}

	public String getSchemaRetrievalUrl() {
		return schemaRetrievalUrl;
	}

	public void setSchemaRetrievalUrl(String schemaRetrievalUrl) {
		this.schemaRetrievalUrl = schemaRetrievalUrl;
	}

	public ProviderConfig providerConfig() {
		return providerConfig;
	}

	public void addMasterPeerAddress(NetworkAddress peerAddress) {
		if (peerAddress == null) {
			throw new IllegalArgumentException("peerAddress is null!");
		}
		this.masterPeerAddresses.add(peerAddress);
	}

	public KeysConfig keys() {
		return keys;
	}

	public GatewayConfigProperties() {
	}

	public static GatewayConfigProperties resolve(String file) {
		Properties props = FileUtils.readProperties(file, "UTf-8");
		return resolve(props);
	}

	public static GatewayConfigProperties resolve(File file) {
		Properties props = FileUtils.readProperties(file, "UTf-8");
		return resolve(props);
	}

	public static GatewayConfigProperties resolve(InputStream in) {
		Properties props = FileUtils.readProperties(in, "UTf-8");
		return resolve(props);
	}

	public static GatewayConfigProperties resolve(Properties props) {
		GatewayConfigProperties configProps = new GatewayConfigProperties();
		configProps.http.host = getProperty(props, HTTP_HOST, true);
		configProps.http.port = getInt(props, HTTP_PORT, true);
		configProps.http.contextPath = getProperty(props, HTTP_CONTEXT_PATH, false);

		int peerSize = getInt(props, PEER_SIZE, true);
		if (peerSize <= 0) {
			throw new IllegalDataException("Peer size is illegal !!!");
		}
		for (int i = 0; i < peerSize; i++) {
			String peerHost = getProperty(props, String.format(PEER_HOST_FORMAT, i), true);
			int peerPort = getInt(props, String.format(PEER_PORT_FORMAT, i), true);
			boolean peerSecure = getBoolean(props, String.format(PEER_SECURE_FORMAT, i), false);
			configProps.addMasterPeerAddress(new NetworkAddress(peerHost, peerPort, peerSecure));
		}
		String dataRetrievalUrl = getProperty(props, DATA_RETRIEVAL_URL, true);
		configProps.dataRetrievalUrl = dataRetrievalUrl;

		String schemaRetrievalUrl = getProperty(props, SCHEMA_RETRIEVAL_URL, true);
		configProps.schemaRetrievalUrl = schemaRetrievalUrl;

		String providers = getProperty(props, PEER_PROVIDERS, true);
		if (providers == null || providers.length() <= 0) {
			throw new IllegalArgumentException("Miss peer providers!");
		}
		String[] providerArray = providers.split(",");
		for (String provider : providerArray) {
			configProps.providerConfig.add(provider);
		}

		configProps.keys.defaultPK.pubKeyValue = getProperty(props, DEFAULT_PUBKEY, true);
		configProps.keys.defaultPK.privKeyPath = getProperty(props, DEFAULT_PRIVKEY_PATH, false);
		configProps.keys.defaultPK.privKeyValue = getProperty(props, DEFAULT_PRIVKEY, false);
		if (configProps.keys.defaultPK.privKeyPath == null && configProps.keys.defaultPK.privKeyValue == null) {
			throw new IllegalArgumentException("Miss both of pk-path and pk content!");
		}
		configProps.keys.defaultPK.privKeyPassword = getProperty(props, DEFAULT_PK_PWD, true);

		return configProps;
	}

	private static String getProperty(Properties props, String key, boolean required) {
		String value = props.getProperty(key);
		if (value != null) {
			value = value.trim();
		}
		if (value == null || value.length() == 0) {
			if (required) {
				throw new IllegalArgumentException("Miss property[" + key + "]!");
			}
			return null;
		}
		return value;
	}

	private static boolean getBoolean(Properties props, String key, boolean required) {
		String strBool = getProperty(props, key, required);
		if (strBool == null) {
			return false;
		}
		return Boolean.parseBoolean(strBool);
	}

	private static int getInt(Properties props, String key, boolean required) {
		String strInt = getProperty(props, key, required);
		if (strInt == null) {
			return 0;
		}
		return getInt(strInt);
	}

	private static int getInt(String strInt) {
		return Integer.parseInt(strInt.trim());
	}

	// ------------------------------------------------------------

	public static class ProviderConfig {
		List<String> providers = new ArrayList<>();

		public void add(String provider) {
			providers.add(provider);
		}

		public List<String> getProviders() {
			return providers;
		}
	}

	public static class HttpConfig {

		private String host;

		private int port;

		private String contextPath;

		private HttpConfig() {
		}

		private HttpConfig(String host, int port, String contextPath) {
			this.host = host;
			this.port = port;
			this.contextPath = contextPath;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getContextPath() {
			return contextPath;
		}

		public void setContextPath(String contextPath) {
			this.contextPath = contextPath;
		}
	}

	public static class KeysConfig {

		private KeyPairConfig defaultPK = new KeyPairConfig();

		public KeyPairConfig getDefault() {
			return defaultPK;
		}
	}

	public static class KeyPairConfig {

		private String pubKeyValue;

		private String privKeyPath;

		private String privKeyValue;

		private String privKeyPassword;

		public String getPrivKeyPath() {
			return privKeyPath;
		}

		public String getPrivKeyValue() {
			return privKeyValue;
		}

		public String getPrivKeyPassword() {
			return privKeyPassword;
		}

		public String getPubKeyValue() {
			return pubKeyValue;
		}

		public void setPubKeyValue(String pubKeyValue) {
			this.pubKeyValue = pubKeyValue;
		}

		public void setPrivKeyPath(String privKeyPath) {
			this.privKeyPath = privKeyPath;
		}

		public void setPrivKeyValue(String privKeyValue) {
			this.privKeyValue = privKeyValue;
		}

		public void setPrivKeyPassword(String privKeyPassword) {
			this.privKeyPassword = privKeyPassword;
		}

	}

}
