package com.jd.blockchain.tools.initializer;

import java.io.File;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.LedgerInitProperties.ParticipantProperties;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig.BindingConfig;
import com.jd.blockchain.utils.ArgumentSet;
import com.jd.blockchain.utils.ArgumentSet.ArgEntry;
import com.jd.blockchain.utils.ArgumentSet.Setting;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * 账本初始化器；
 * 
 * @author huanghaiquan
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
public class LedgerInitCommand {

	private static final String LEDGER_BINDING_FILE_NAME = "ledger-binding.conf";

	// 当前参与方的本地配置文件的路径(local.conf)；
	private static final String LOCAL_ARG = "-l";

	// 账本的初始化配置文件的路径(ledger.init)；
	private static final String INI_ARG = "-i";

	// 是否输出调试信息；
	private static final String DEBUG_OPT = "-debug";

	private static final String MONITOR_OPT = "-monitor";

	private static final Prompter DEFAULT_PROMPTER = new ConsolePrompter();

	private static final Prompter ANSWER_PROMPTER = new PresetAnswerPrompter("Y");

	private static final Prompter LOG_PROMPTER = new LogPrompter();

	/**
	 * 入口；
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Prompter prompter = DEFAULT_PROMPTER;

		Setting argSetting = ArgumentSet.setting().prefix(LOCAL_ARG, INI_ARG).option(DEBUG_OPT).option(MONITOR_OPT);
		ArgumentSet argSet = ArgumentSet.resolve(args, argSetting);

		try {
			if (argSet.hasOption(MONITOR_OPT)) {
				prompter = LOG_PROMPTER;
			}

			ArgEntry localArg = argSet.getArg(LOCAL_ARG);
			if (localArg == null) {
				prompter.info("Miss local config file which can be specified with arg [%s]!!!", LOCAL_ARG);

			}
			LocalConfig localConf = LocalConfig.resolve(localArg.getValue());

			ArgEntry iniArg = argSet.getArg(INI_ARG);
			if (iniArg == null) {
				prompter.info("Miss ledger initializing config file which can be specified with arg [%s]!!!", INI_ARG);
				return;
			}

			// load ledger init setting;
			LedgerInitProperties ledgerInitProperties = LedgerInitProperties.resolve(iniArg.getValue());
			String localNodePubKeyString = localConf.getLocal().getPubKeyString();
			PubKey localNodePubKey = KeyGenUtils.decodePubKey(localNodePubKeyString);
			// 地址根据公钥生成
			String localNodeAddress = AddressEncoding.generateAddress(localNodePubKey).toBase58();

			// 加载全部公钥;
			int currId = -1;
			for (int i = 0; i < ledgerInitProperties.getConsensusParticipantCount(); i++) {
				ParticipantProperties partiConf = ledgerInitProperties.getConsensusParticipant(i);
//				String partiAddress = partiConf.getAddress();
//				if (partiAddress == null) {
//					if (partiConf.getPubKeyPath() != null) {
//						PubKey pubKey = KeyGenUtils.readPubKey(partiConf.getPubKeyPath());
//						partiConf.setPubKey(pubKey);
//						partiAddress = partiConf.getAddress();
//					}
//				}
				if (localNodeAddress.equals(partiConf.getAddress().toBase58())) {
					currId = i;
				}
			}
			if (currId == -1) {
				throw new IllegalStateException(
						"The current node specified in local.conf is not found in ledger.init!");
			}

			// 加载当前节点的私钥；
			String base58Pwd = localConf.getLocal().getPassword();
			if (base58Pwd == null) {
				base58Pwd = KeyGenUtils.readPasswordString();
			}
			PrivKey privKey = KeyGenUtils.decodePrivKey(localConf.getLocal().getPrivKeyString(), base58Pwd);

			// Output ledger binding config of peer;
			if (!FileUtils.existDirectory(localConf.getBindingOutDir())) {
				FileUtils.makeDirectory(localConf.getBindingOutDir());
			}
			File ledgerBindingFile = new File(localConf.getBindingOutDir(), LEDGER_BINDING_FILE_NAME);
			LedgerBindingConfig conf;
			if (ledgerBindingFile.exists()) {
				conf = LedgerBindingConfig.resolve(ledgerBindingFile);
			} else {
				conf = new LedgerBindingConfig();
			}

			// 启动初始化；
			LedgerInitCommand initCommand = new LedgerInitCommand();
			HashDigest newLedgerHash = initCommand.startInit(currId, privKey, base58Pwd, ledgerInitProperties,
					localConf.getStoragedDb(), prompter, conf);

			if (newLedgerHash != null) {
				// success;
				// so save ledger binding config to file system;
				conf.store(ledgerBindingFile);
				prompter.info("\r\n------ Update Ledger binding configuration success! ------[%s]",
						ledgerBindingFile.getAbsolutePath());
			}

		} catch (Exception e) {
			prompter.error("\r\nError!! -- %s\r\n", e.getMessage());
			if (argSet.hasOption(DEBUG_OPT)) {
				e.printStackTrace();
			}

			prompter.error("\r\n Ledger init process has been broken by error!");
		}
		prompter.confirm(InitializingStep.LEDGER_INIT_COMPLETED.toString(), "\r\n\r\n Press any key to quit. :>");

		if (argSet.hasOption(MONITOR_OPT)) {
			// 管理工具启动的方式下，需自动退出
			System.exit(0);
		}
	}

	private LedgerManager ledgerManager;

	public LedgerManager getLedgerManager() {
		return ledgerManager;
	}

	public LedgerInitCommand() {
	}

	public HashDigest startInit(int currId, PrivKey privKey, String base58Pwd,
			LedgerInitProperties ledgerInitProperties, DBConnectionConfig dbConnConfig, Prompter prompter,
			LedgerBindingConfig conf, Object... extBeans) {
		if (currId < 0 || currId >= ledgerInitProperties.getConsensusParticipantCount()) {
			prompter.info(
					"Your participant id is illegal which is less than 1 or great than the total participants count[%s]!!!",
					ledgerInitProperties.getConsensusParticipantCount());
			return null;
		}

		// generate binding config;
		BindingConfig bindingConf = new BindingConfig();

		// 设置账本名称
		bindingConf.setLedgerName(ledgerInitProperties.getLedgerName());

		bindingConf.getParticipant()
				.setAddress(ledgerInitProperties.getConsensusParticipant(currId).getAddress().toBase58());
		// 设置参与方名称
		bindingConf.getParticipant().setName(ledgerInitProperties.getConsensusParticipant(currId).getName());

		String encodedPrivKey = KeyGenUtils.encodePrivKey(privKey, base58Pwd);
		bindingConf.getParticipant().setPk(encodedPrivKey);
		bindingConf.getParticipant().setPassword(base58Pwd);

		bindingConf.getDbConnection().setConnectionUri(dbConnConfig.getUri());
		bindingConf.getDbConnection().setPassword(dbConnConfig.getPassword());

		// confirm continue；
		prompter.info("\r\n\r\n This is participant [%s], the ledger initialization is ready to start!\r\n", currId);
//		ConsoleUtils.confirm("Press any key to continue... ");
//		prompter.confirm("Press any key to continue... ");

		// start the web controller of Ledger Initializer;
		NetworkAddress serverAddress = ledgerInitProperties.getConsensusParticipant(currId).getInitializerAddress();
		String argServerAddress = String.format("--server.address=%s", serverAddress.getHost());
		String argServerPort = String.format("--server.port=%s", serverAddress.getPort());
		String[] innerArgs = { argServerAddress, argServerPort };

		SpringApplication app = new SpringApplication(LedgerInitCommand.class);
		if (extBeans != null && extBeans.length > 0) {
			app.addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) applicationContext -> {
				ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
				for (Object bean : extBeans) {
					beanFactory.registerSingleton(bean.toString(), bean);
				}
			});
		}
		ConfigurableApplicationContext ctx = app.run(innerArgs);
		this.ledgerManager = ctx.getBean(LedgerManager.class);

		prompter.info("\r\n------ Web controller of Ledger Initializer[%s:%s] was started. ------\r\n",
				serverAddress.getHost(), serverAddress.getPort());

		try {
			LedgerInitProcess initProc = ctx.getBean(LedgerInitProcess.class);
			HashDigest ledgerHash = initProc.initialize(currId, privKey, ledgerInitProperties,
					bindingConf.getDbConnection(), prompter);

			if (ledgerHash == null) {
				// ledger init fail;
				prompter.error("\r\n------ Ledger initialize fail! ------\r\n");
				return null;
			} else {
				prompter.info("\r\n------ Ledger initialize success! ------");
				prompter.info("New Ledger Hash is :[%s]", ledgerHash.toBase58());

				if (conf == null) {
					conf = new LedgerBindingConfig();
				}
				conf.addLedgerBinding(ledgerHash, bindingConf);

				return ledgerHash;

			}
		} finally {
			ctx.close();
			prompter.info("\r\n------ Web listener[%s:%s] was closed. ------\r\n", serverAddress.getHost(),
					serverAddress.getPort());
		}
	}

}
