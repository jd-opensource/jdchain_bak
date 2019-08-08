package com.jd.blockchain.tools.initializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.io.RuntimeIOException;

public class LedgerBindingConfig {

	public static final String CHARSET = "UTF-8";

	public static final String LEDGER_HASH_SEPERATOR = ",";

	public static final String ATTR_SEPERATOR = ".";

	// Binding List;
	public static final String LEDGER_BINDINS = "ledger.bindings";

	// Binding Config Key Prefix;
	public static final String BINDING_PREFIX = "binding";

	// Participant Config Key Prefix;
	public static final String PARTI_PREFIX = "parti.";

	// Participant Attribute Key;
	public static final String PARTI_ADDRESS = PARTI_PREFIX + "address";
	// 参与方名称
	public static final String PARTI_NAME = PARTI_PREFIX + "name";
	public static final String PARTI_PK_PATH = PARTI_PREFIX + "pk-path";
	public static final String PARTI_PK = PARTI_PREFIX + "pk";
	public static final String PARTI_PASSWORD = PARTI_PREFIX + "pwd";


	// DB Connection Config Key Prefix;
	public static final String DB_PREFIX = "db.";

	// DB Connction Attribute Key;
	public static final String DB_CONN = DB_PREFIX + "uri";
	public static final String DB_PASSWORD = DB_PREFIX + "pwd";

	// 账本名字
	public static final String LEDGER_NAME = "name";

	// ------------------------------

	private Map<HashDigest, BindingConfig> bindings = new LinkedHashMap<>();

	public HashDigest[] getLedgerHashs() {
		return bindings.keySet().toArray(new HashDigest[bindings.size()]);
	}

	public BindingConfig getLedger(HashDigest hash) {
		return bindings.get(hash);
	}

	public void addLedgerBinding(HashDigest ledgerHash, BindingConfig binding) {
		bindings.put(ledgerHash, binding);
	}

	public void removeLedgerBinding(HashDigest ledgerHash) {
		bindings.remove(ledgerHash);
	}

	public void store(File file) {
		try (FileOutputStream out = new FileOutputStream(file, false)) {
			store(out);
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	public void store(OutputStream out) {
		FileUtils.writeText(toPropertiesString(), out, CHARSET);
	}

	private String toPropertiesString() {
		StringBuilder builder = new StringBuilder();

		HashDigest[] hashs = this.getLedgerHashs();
		writeLedgerHashs(builder, hashs);
		writeLine(builder);

		for (int i = 0; i < hashs.length; i++) {
			writeLine(builder, "#第 %s 个账本[%s]的配置；", i + 1, hashs[i].toBase58());
			BindingConfig binding = getLedger(hashs[i]);
			writeLedger(builder, hashs[i], binding);
			writeParticipant(builder, hashs[i], binding);
			writeDB(builder, hashs[i], binding);
			writeLine(builder);
		}
		return builder.toString();
	}

	private void writeLedgerHashs(StringBuilder builder, HashDigest[] hashs) {
		writeLine(builder, "#绑定的账本的hash列表；以逗号分隔；");

		String[] base58Hashs = new String[hashs.length];
		for (int i = 0; i < base58Hashs.length; i++) {
			base58Hashs[i] = hashs[i].toBase58();
		}
		String base58HashList = String.join(", \\\r\n", base58Hashs);
		writeLine(builder, "%s=%s", LEDGER_BINDINS, base58HashList);
		writeLine(builder);
	}

	private void writeParticipant(StringBuilder builder, HashDigest ledgerHash, BindingConfig binding) {
		String ledgerPrefix = String.join(ATTR_SEPERATOR, BINDING_PREFIX, ledgerHash.toBase58());
		// 参与方配置；
		String partiAddressKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_ADDRESS);
		String partiPkPathKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_PK_PATH);
		String partiNameKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_NAME);
		String partiPKKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_PK);
		String partiPwdKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_PASSWORD);

		writeLine(builder, "#账本的当前共识参与方的节点地址 Address；");
		writeLine(builder, "%s=%s", partiAddressKey, stringOf(binding.getParticipant().getAddress()));
		writeLine(builder, "#账本的当前共识参与方的节点名称 NodeName；");
		writeLine(builder, "%s=%s", partiNameKey, stringOf(binding.getParticipant().getName()));
		writeLine(builder, "#账本的当前共识参与方的私钥文件的保存路径；");
		writeLine(builder, "%s=%s", partiPkPathKey, stringOf(binding.getParticipant().getPkPath()));
		writeLine(builder, "#账本的当前共识参与方的私钥内容（Base58编码）；如果指定了，优先选用此属性，其次是 pk-path 属性；");
		writeLine(builder, "%s=%s", partiPKKey, stringOf(binding.getParticipant().getPk()));
		writeLine(builder, "#账本的当前共识参与方的私钥文件的读取口令；可为空；如果为空时，节点的启动过程中需要手动从控制台输入；");
		writeLine(builder, "%s=%s", partiPwdKey, stringOf(binding.getParticipant().getPassword()));
		writeLine(builder);
	}

	private void writeDB(StringBuilder builder, HashDigest ledgerHash, BindingConfig binding) {
		String ledgerPrefix = String.join(ATTR_SEPERATOR, BINDING_PREFIX, ledgerHash.toBase58());
		// 数据库存储配置；
		String dbConnKey = String.join(ATTR_SEPERATOR, ledgerPrefix, DB_CONN);
		String dbPwdKey = String.join(ATTR_SEPERATOR, ledgerPrefix, DB_PASSWORD);

		writeLine(builder, "#账本的存储数据库的连接字符串；");
		writeLine(builder, "%s=%s", dbConnKey, stringOf(binding.getDbConnection().getUri()));
		writeLine(builder, "#账本的存储数据库的连接口令；");
		writeLine(builder, "%s=%s", dbPwdKey, stringOf(binding.getDbConnection().getPassword()));
		writeLine(builder);
	}

	private void writeLedger(StringBuilder builder, HashDigest ledgerHash, BindingConfig binding) {
		String ledgerPrefix = String.join(ATTR_SEPERATOR, BINDING_PREFIX, ledgerHash.toBase58());
		// 账本相关信息配置；
		String ledgerNameKey = String.join(ATTR_SEPERATOR, ledgerPrefix, LEDGER_NAME);

		writeLine(builder, "#账本的名称；");
		writeLine(builder, "%s=%s", ledgerNameKey, stringOf(binding.getLedgerName()));
		writeLine(builder);
	}

	private static String stringOf(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	private static void writeLine(StringBuilder content, String format, Object... args) {
		content.append(String.format(format, args));
		content.append("\r\n");
	}

	private static void writeLine(StringBuilder content) {
		content.append("\r\n");
	}

	/**
	 * 解析配置；
	 * 
	 * @param file
	 * @return
	 */
	public static LedgerBindingConfig resolve(File file) {
		Properties props = FileUtils.readProperties(file, CHARSET);
		return resolve(props);
	}

	/**
	 * 解析配置；
	 * 
	 * @param in
	 * @return
	 */
	public static LedgerBindingConfig resolve(InputStream in) {
		Properties props = FileUtils.readProperties(in, CHARSET);
		return resolve(props);
	}

	/**
	 * 解析配置；
	 * 
	 * @param props
	 * @return
	 */
	public static LedgerBindingConfig resolve(Properties props) {
		LedgerBindingConfig conf = new LedgerBindingConfig();

		// 解析哈希列表；
		String ledgerHashListString = getProperty(props, LEDGER_BINDINS, true);
		String[] base58Hashs = split(ledgerHashListString, LEDGER_HASH_SEPERATOR);
		if (base58Hashs.length == 0) {
			return conf;
		}
		HashDigest[] hashs = new HashDigest[base58Hashs.length];
		for (int i = 0; i < base58Hashs.length; i++) {
			byte[] hashBytes = Base58Utils.decode(base58Hashs[i]);
			hashs[i] = new HashDigest(hashBytes);

			BindingConfig bindingConf = resolveBinding(props, base58Hashs[i]);
			conf.bindings.put(hashs[i], bindingConf);
		}

		return conf;
	}

	/**
	 * 解析 Binding 配置；
	 * 
	 * @param props
	 * @param ledgerHash
	 * @return
	 */
	private static BindingConfig resolveBinding(Properties props, String ledgerHash) {
		BindingConfig binding = new BindingConfig();

		String ledgerPrefix = String.join(ATTR_SEPERATOR, BINDING_PREFIX, ledgerHash);
		// 参与方配置；
		String partiAddrKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_ADDRESS);
		String partiPkPathKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_PK_PATH);
		String partiNameKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_NAME);
		String partiPKKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_PK);
		String partiPwdKey = String.join(ATTR_SEPERATOR, ledgerPrefix, PARTI_PASSWORD);

		binding.participant.address = getProperty(props, partiAddrKey, true);
		binding.participant.name = getProperty(props, partiNameKey, true);
		binding.participant.pkPath = getProperty(props, partiPkPathKey, false);
		binding.participant.pk = getProperty(props, partiPKKey, false);
		binding.participant.password = getProperty(props, partiPwdKey, false);
//		if (binding.participant.address < 0) {
//			throw new IllegalArgumentException(
//					String.format("Participant id less than 0 in ledger binding[%s]!", ledgerHash));
//		}
		if (binding.participant.pkPath == null && binding.participant.pk == null) {
			throw new IllegalArgumentException(
					String.format("No priv key config of participant of ledger binding[%s]!", ledgerHash));
		}

		// 数据库存储配置；
		String dbConnKey = String.join(ATTR_SEPERATOR, ledgerPrefix, DB_CONN);
		String dbPwdKey = String.join(ATTR_SEPERATOR, ledgerPrefix, DB_PASSWORD);

		binding.dbConnection.setConnectionUri(getProperty(props, dbConnKey, true));
		binding.dbConnection.setPassword(getProperty(props, dbPwdKey, false));
		if (binding.dbConnection.getUri() == null) {
			throw new IllegalArgumentException(
					String.format("No db connection config of participant of ledger binding[%s]!", ledgerHash));
		}

		// 设置账本名称
		String ledgerNameKey = String.join(ATTR_SEPERATOR, ledgerPrefix, LEDGER_NAME);
		binding.ledgerName = getProperty(props, ledgerNameKey, true);

		return binding;
	}

	private static String[] split(String str, String seperator) {
		String[] items = str.split(seperator);
		List<String> validItems = new ArrayList<>();
		for (int i = 0; i < items.length; i++) {
			items[i] = items[i].trim();
			if (items[i].length() > 0) {
				validItems.add(items[i]);
			}
		}
		return validItems.toArray(new String[validItems.size()]);
	}

	/**
	 * 返回指定属性的值；
	 * 
	 * <br>
	 * 当值不存在时，如果是必需参数，则抛出异常 {@link IllegalArgumentException}，否则返回 null；
	 * 
	 * @param props
	 *            属性表；
	 * @param key
	 *            属性的键；
	 * @param required
	 *            是否为必需参数；
	 * @return 长度大于 0 的字符串，或者 null；
	 */
	private static String getProperty(Properties props, String key, boolean required) {
		String value = props.getProperty(key);
		if (value == null) {
			if (required) {
				throw new IllegalArgumentException("Miss property[" + key + "]!");
			}
			return null;
		}
		value = value.trim();
		if (value.length() == 0) {
			if (required) {
				throw new IllegalArgumentException("Miss property[" + key + "]!");
			}
			return null;
		}
		return value;
	}

	public static class BindingConfig {

		private String ledgerName;

		// 账本名字
		private ParticipantBindingConfig participant = new ParticipantBindingConfig();

		private DBConnectionConfig dbConnection = new DBConnectionConfig();

		public ParticipantBindingConfig getParticipant() {
			return participant;
		}

		public DBConnectionConfig getDbConnection() {
			return dbConnection;
		}

		public void setLedgerName(String ledgerName) {
			this.ledgerName = ledgerName;
		}

		public String getLedgerName() {
			return ledgerName;
		}
	}

	public static class ParticipantBindingConfig {

		private String address;

		private String name;

		private String pkPath;

		private String pk;

		private String password;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getPkPath() {
			return pkPath;
		}

		public void setPkPath(String pkPath) {
			this.pkPath = pkPath;
		}

		public String getPk() {
			return pk;
		}

		public void setPk(String pk) {
			this.pk = pk;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

}
