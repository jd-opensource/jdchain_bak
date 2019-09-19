
package com.jd.blockchain.tools.keygen;

import static com.jd.blockchain.crypto.KeyGenUtils.decodePubKey;
import static com.jd.blockchain.crypto.KeyGenUtils.decryptedPrivKeyBytes;
import static com.jd.blockchain.crypto.KeyGenUtils.encodePrivKey;
import static com.jd.blockchain.crypto.KeyGenUtils.encodePubKey;
import static com.jd.blockchain.crypto.KeyGenUtils.readPassword;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.ArgumentSet;
import com.jd.blockchain.utils.ArgumentSet.ArgEntry;
import com.jd.blockchain.utils.ArgumentSet.Setting;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.security.DecryptionException;

public class KeyGenCommand {

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
		AsymmetricKeypair kp = Crypto.getSignatureFunction("ED25519").generateKeypair();

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

	/**
	 * 读取密钥； <br>
	 * 如果是私钥，则需要输入密码；
	 * 
	 * @param keyFile
	 */
	public static void readKey(String keyFile, boolean decrypting) {
		String base58KeyString = FileUtils.readText(keyFile);
		byte[] keyBytes = Base58Utils.decode(base58KeyString);
		if (KeyGenUtils.isPubKeyBytes(keyBytes)) {
			if (decrypting) {
				// Try reading pubKey;
				PubKey pubKey = decodePubKey(keyBytes);
				ConsoleUtils.info(
						"======================== pub key ========================\r\n" + "[%s]\r\n"
								+ "Raw:[%s][%s]\r\n",
						base58KeyString, pubKey.getAlgorithm(), Base58Utils.encode(pubKey.toBytes()));
			} else {
				ConsoleUtils.info("======================== pub key ========================\r\n" + "[%s]\r\n",
						base58KeyString);
			}
			return;
		} else if (KeyGenUtils.isPrivKeyBytes(keyBytes)) {
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

}
