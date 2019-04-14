
package com.jd.blockchain.tools.keygen;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.ArgumentSet;
import com.jd.blockchain.utils.ArgumentSet.ArgEntry;
import com.jd.blockchain.utils.ArgumentSet.Setting;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.security.AESUtils;
import com.jd.blockchain.utils.security.DecryptionException;
import com.jd.blockchain.utils.security.ShaUtils;

public class KeyGenCommand {

	public static final byte[] PUB_KEY_FILE_MAGICNUM = { (byte) 0xFF, 112, 117, 98 };

	public static final byte[] PRIV_KEY_FILE_MAGICNUM = { (byte) 0x00, 112, 114, 118 };

	// 指定 -r 参数时为“读取模式”，显示密钥文件； -r 参数之后紧跟着指定要读取的公钥或者私钥文件的路径；
	private static final String READ_ARG = "-r";

	// 在“读取模式”下指定 -d 参数时要求输入密码，并显示解密后的私钥明文；
	private static final String OPT_DECRYPTING = "-d";

	// 未指定 -r 参数时为“生成模式”，输出密钥文件；通过 -o
	// 参数指定文件的输出目录；并通过第一个名称参数指定密钥名称，密钥的保存文件以指定的名称命名；
	private static final String OUT_DIR_ARG = "-o";

	private static final String NAME_ARG = "-n";

	// 指定local.conf的路径，以方便直接修改local.conf配置文件
	private static final String LOCAL_CONF_ARG = "-l";

	// 是否输出调试信息；
	private static final String OPT_DEBUG = "-debug";

	/**
	 * 入口；
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Setting setting = ArgumentSet.setting().prefix(READ_ARG, NAME_ARG, OUT_DIR_ARG, LOCAL_CONF_ARG)
				.option(OPT_DECRYPTING, OPT_DEBUG);
		ArgumentSet argSet = ArgumentSet.resolve(args, setting);
		try {
			ArgEntry[] argEntries = argSet.getArgs();
			if (argEntries.length == 0) {
				ConsoleUtils.info("Miss argument!\r\n"
						+ "-r : Run in reading mode if set this option, or in generating mode if not set.\r\n"
						+ "-d : Decrypt priv key in reading mode, optional.\r\n" + "-n : name of key.\r\n"
						+ "-o : output dir of key under generating mode.\r\n");
				return;
			}

			if (argSet.getArg(READ_ARG) != null) {
				readKey(argSet.getArg(READ_ARG).getValue(), argSet.hasOption(OPT_DECRYPTING));
			} else {
				ArgEntry name = argSet.getArg(NAME_ARG);
				if (name == null) {
					ConsoleUtils.info("Miss name of key!");
					return;
				}
				String outputDir = null;
				ArgEntry dirArg = argSet.getArg(OUT_DIR_ARG);
				if (dirArg == null || dirArg.getValue() == null) {
					// 在当前目录生成；
					outputDir = "." + File.separatorChar;

					// ConsoleUtils.info("Miss storage dir of keys!");
					// return;
				} else {
					outputDir = dirArg.getValue();
				}
				if (!FileUtils.existDirectory(outputDir)) {
					// 创建目录；
					ConsoleUtils.info(
							"The storage dir[" + outputDir + "] doesn't exist,  it will be created automatically!");
					FileUtils.makeDirectory(outputDir);
//					return;
				}
				ArgEntry localConfArg = argSet.getArg(LOCAL_CONF_ARG);
				String localConfPath = localConfArg == null ? null : localConfArg.getValue();

				generateKeyPair(name.getValue(), outputDir, localConfPath);
			}

		} catch (Exception e) {
			ConsoleUtils.info("Error!!! %s", e.getMessage());
			if (argSet.hasOption(OPT_DEBUG)) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 生成密钥，要求输入密码用于保护私钥文件；
	 * 
	 * @param name
	 * @param outputDir
	 */
	private static void generateKeyPair(String name, String outputDir, String localConfPath) {
		AsymmetricKeypair kp = CryptoServiceProviders.getSignatureFunction("ED25519").generateKeypair();

		String base58PubKey = encodePubKey(kp.getPubKey());

		byte[] pwdBytes = readPassword();
		String base58PrivKey = encodePrivKey(kp.getPrivKey(), pwdBytes);

		File pubKeyFile = new File(outputDir, String.format("%s.pub", name));
		File privKeyFile = new File(outputDir, String.format("%s.priv", name));
		FileUtils.writeText(base58PubKey, pubKeyFile);
		FileUtils.writeText(base58PrivKey, privKeyFile);

		String base58PwdKey = null;
		String savePwdStr = ConsoleUtils
				.confirm("Do you want to save encode password to file? Please input y or n ...");
		if (savePwdStr.equalsIgnoreCase("y") || savePwdStr.equalsIgnoreCase("yes")) {
			base58PwdKey = Base58Utils.encode(pwdBytes);
			File pwdFile = new File(outputDir, String.format("%s.pwd", name));
			FileUtils.writeText(base58PwdKey, pwdFile);
		} else if (savePwdStr.equalsIgnoreCase("n") || savePwdStr.equalsIgnoreCase("no")) {
			// do nothing
		} else {
			savePwdStr = ConsoleUtils.confirm("Please input y or n ...");
			if (savePwdStr.equalsIgnoreCase("y") || savePwdStr.equalsIgnoreCase("yes")) {
				base58PwdKey = Base58Utils.encode(pwdBytes);
				File pwdFile = new File(outputDir, String.format("%s.pwd", name));
				FileUtils.writeText(base58PwdKey, pwdFile);
			} else {
				// do nothing
			}
		}
		if (localConfPath != null) {
			File localConf = new File(localConfPath);
			if (localConf.exists()) {
				try {
					List<String> configs = org.apache.commons.io.FileUtils.readLines(localConf);
					List<String> modifyConfigs = new ArrayList<>();
					if (configs != null && !configs.isEmpty()) {
						for (String configLine : configs) {
							if (configLine.startsWith("local.parti.pubkey")) {
								modifyConfigs.add("local.parti.pubkey=" + base58PubKey);
							} else if (configLine.startsWith("local.parti.privkey")) {
								modifyConfigs.add("local.parti.privkey=" + base58PrivKey);
							} else if (configLine.startsWith("local.parti.pwd")) {
								modifyConfigs.add("local.parti.pwd=" + base58PwdKey);
							} else {
								modifyConfigs.add(configLine);
							}
						}
					}
					org.apache.commons.io.FileUtils.writeLines(localConf, modifyConfigs, false);
				} catch (Exception e) {
					System.err.println("Error!!! --[" + e.getClass().getName() + "] " + e.getMessage());
				}
			}
		}
	}

	public static String encodePubKey(PubKey pubKey) {
		byte[] pubKeyBytes = BytesUtils.concat(PUB_KEY_FILE_MAGICNUM, pubKey.toBytes());
		String base58PubKey = Base58Utils.encode(pubKeyBytes);
		return base58PubKey;
	}

	public static PubKey decodePubKey(String base58PubKey) {
		byte[] keyBytes = Base58Utils.decode(base58PubKey);
		return decodePubKey(keyBytes);
	}

	public static String encodePrivKey(PrivKey privKey, String base58Pwd) {
		byte[] pwdBytes = Base58Utils.decode(base58Pwd);
		return encodePrivKey(privKey, pwdBytes);
	}

	public static String encodePrivKey(PrivKey privKey, byte[] pwdBytes) {
		byte[] encodedPrivKeyBytes = encryptPrivKey(privKey, pwdBytes);
		String base58PrivKey = Base58Utils.encode(encodedPrivKeyBytes);
		return base58PrivKey;
	}

	public static byte[] encryptPrivKey(PrivKey privKey, byte[] pwdBytes) {
		SecretKey userKey = AESUtils.generateKey128(pwdBytes);
		byte[] encryptedPrivKeyBytes = AESUtils.encrypt(privKey.toBytes(), userKey);
		return BytesUtils.concat(PRIV_KEY_FILE_MAGICNUM, encryptedPrivKeyBytes);
	}

	/**
	 * 读取密钥； <br>
	 * 如果是私钥，则需要输入密码；
	 * 
	 * @param keyFile
	 */
	public static void readKey(String keyFile, boolean decrypting) {
		String base58KeyString = FileUtils.readText(keyFile);
		byte[] keyBytes = Base58Utils.decode(base58KeyString);
		if (BytesUtils.startsWith(keyBytes, PUB_KEY_FILE_MAGICNUM)) {
			if (decrypting) {
				// Try reading pubKey;
				PubKey pubKey = doDecodePubKeyBytes(keyBytes);
				ConsoleUtils.info(
						"======================== pub key ========================\r\n" + "[%s]\r\n"
								+ "Raw:[%s][%s]\r\n",
						base58KeyString, pubKey.getAlgorithm(), Base58Utils.encode(pubKey.toBytes()));
			} else {
				ConsoleUtils.info("======================== pub key ========================\r\n" + "[%s]\r\n",
						base58KeyString);
			}
			return;
		} else if (BytesUtils.startsWith(keyBytes, PRIV_KEY_FILE_MAGICNUM)) {
			// Try reading privKye;
			try {
				if (decrypting) {
					byte[] pwdBytes = readPassword();
					PrivKey privKey = decryptedPrivKeyBytes(keyBytes, pwdBytes);
					ConsoleUtils.info(
							"======================== priv key ========================\r\n" + "[%s]\r\n"
									+ "Raw:[%s][%s]\r\n",
							base58KeyString, privKey.getAlgorithm(), Base58Utils.encode(privKey.toBytes()));
				} else {
					ConsoleUtils.info("======================== priv key ========================\r\n[%s]\r\n",
							base58KeyString);
				}
			} catch (DecryptionException e) {
				ConsoleUtils.error("Invalid password!");
			}
			return;
		} else {
			ConsoleUtils.error("Unknow key!!");
			return;
		}
	}

	private static PubKey doDecodePubKeyBytes(byte[] encodedPubKeyBytes) {
		byte[] pubKeyBytes = Arrays.copyOfRange(encodedPubKeyBytes, PUB_KEY_FILE_MAGICNUM.length,
				encodedPubKeyBytes.length);
		return new PubKey(pubKeyBytes);
	}

	public static PrivKey decryptedPrivKeyBytes(byte[] encodedPrivKeyBytes, byte[] pwdBytes) {
		// Read privKye;
		SecretKey userKey = AESUtils.generateKey128(pwdBytes);
		byte[] encryptedKeyBytes = Arrays.copyOfRange(encodedPrivKeyBytes, PRIV_KEY_FILE_MAGICNUM.length,
				encodedPrivKeyBytes.length);
		try {
			byte[] plainKeyBytes = AESUtils.decrypt(encryptedKeyBytes, userKey);
			return new PrivKey(plainKeyBytes);
		} catch (DecryptionException e) {
			throw new DecryptionException("Invalid password!", e);
		}
	}

	public static PubKey readPubKey(String keyFile) {
		String base58KeyString = FileUtils.readText(keyFile);
		return decodePubKey(base58KeyString);
	}

	public static PubKey decodePubKey(byte[] encodedPubKeyBytes) {
		if (BytesUtils.startsWith(encodedPubKeyBytes, PUB_KEY_FILE_MAGICNUM)) {
			// Read pubKey;
			return doDecodePubKeyBytes(encodedPubKeyBytes);
		}

		throw new IllegalArgumentException("The specified bytes is not valid PubKey generated by the KeyGen tool!");
	}

	/**
	 * 从控制台读取加密口令，以二进制数组形式返回原始口令的一次SHA256的结果；
	 * 
	 * @return
	 */
	public static byte[] readPassword() {
		byte[] pwdBytes = ConsoleUtils.readPassword();
		return ShaUtils.hash_256(pwdBytes);
	}

	/**
	 * 对指定的原始密码进行编码生成用于加解密的密码；
	 * 
	 * @param rawPassword
	 * @return
	 */
	public static byte[] encodePassword(String rawPassword) {
		byte[] pwdBytes = BytesUtils.toBytes(rawPassword, "UTF-8");
		return ShaUtils.hash_256(pwdBytes);
	}

	/**
	 * 对指定的原始密码进行编码生成用于加解密的密码；
	 * 
	 * @param rawPassword
	 * @return
	 */
	public static String encodePasswordAsBase58(String rawPassword) {
		return Base58Utils.encode(encodePassword(rawPassword));
	}

	/**
	 * 从控制台读取加密口令，以Base58字符串形式返回口令的一次SHA256的结果；
	 * 
	 * @return
	 */
	public static String readPasswordString() {
		return Base58Utils.encode(readPassword());
	}

	public static PrivKey readPrivKey(String keyFile, String base58Pwd) {
		return readPrivKey(keyFile, Base58Utils.decode(base58Pwd));
	}

	/**
	 * 从文件读取私钥；
	 * 
	 * @param keyFile
	 * @param pwdBytes
	 * @return
	 */
	public static PrivKey readPrivKey(String keyFile, byte[] pwdBytes) {
		String base58KeyString = FileUtils.readText(keyFile);
		byte[] keyBytes = Base58Utils.decode(base58KeyString);
		if (!BytesUtils.startsWith(keyBytes, PRIV_KEY_FILE_MAGICNUM)) {
			throw new IllegalArgumentException("The specified file is not a private key file!");
		}
		return decryptedPrivKeyBytes(keyBytes, pwdBytes);
	}

	public static PrivKey decodePrivKey(String base58Key, String base58Pwd) {
		byte[] decryptedKey = Base58Utils.decode(base58Pwd);
		return decodePrivKey(base58Key, decryptedKey);
	}

	public static PrivKey decodePrivKey(String base58Key, byte[] pwdBytes) {
		byte[] keyBytes = Base58Utils.decode(base58Key);
		if (!BytesUtils.startsWith(keyBytes, PRIV_KEY_FILE_MAGICNUM)) {
			throw new IllegalArgumentException("The specified file is not a private key file!");
		}
		return decryptedPrivKeyBytes(keyBytes, pwdBytes);
	}

	public static PrivKey decodePrivKeyWithRawPassword(String base58Key, String rawPassword) {
		byte[] pwdBytes = encodePassword(rawPassword);
		byte[] keyBytes = Base58Utils.decode(base58Key);
		if (!BytesUtils.startsWith(keyBytes, PRIV_KEY_FILE_MAGICNUM)) {
			throw new IllegalArgumentException("The specified file is not a private key file!");
		}
		return decryptedPrivKeyBytes(keyBytes, pwdBytes);
	}

}
