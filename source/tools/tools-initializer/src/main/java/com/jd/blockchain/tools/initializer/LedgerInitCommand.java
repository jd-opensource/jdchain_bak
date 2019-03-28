package com.jd.blockchain.tools.initializer;

import java.io.File;
import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.core.impl.LedgerManager;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig.BindingConfig;
import com.jd.blockchain.tools.initializer.LedgerInitProperties.ConsensusParticipantConfig;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
import com.jd.blockchain.utils.ArgumentSet;
import com.jd.blockchain.utils.ConsoleUtils;
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

	/**
	 * 入口；
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Setting argSetting = ArgumentSet.setting().prefix(LOCAL_ARG, INI_ARG).option(DEBUG_OPT);
		ArgumentSet argset = ArgumentSet.resolve(args, argSetting);

		try {
			ArgEntry localArg = argset.getArg(LOCAL_ARG);
			if (localArg == null) {
				ConsoleUtils.info("Miss local config file which can be specified with arg [%s]!!!", LOCAL_ARG);

			}
			LocalConfig localConf = LocalConfig.resolve(localArg.getValue());

			ArgEntry iniArg = argset.getArg(INI_ARG);
			if (iniArg == null) {
				ConsoleUtils.info("Miss ledger initializing config file which can be specified with arg [%s]!!!",
						INI_ARG);
				return;
			}

			// // load ledger init setting;
			LedgerInitProperties ledgerInitSetting = LedgerInitProperties.resolve(iniArg.getValue());
			String localNodePubKeyString = localConf.getLocal().getPubKeyString();
			PubKey localNodePubKey = KeyGenCommand.decodePubKey(localNodePubKeyString);
			// 地址根据公钥生成
			String localNodeAddress = AddressEncoding.generateAddress(localNodePubKey).toBase58();

			// load all pub keys;
			int currId = -1;
			for (int i = 0; i < ledgerInitSetting.getConsensusParticipantCount(); i++) {
				ConsensusParticipantConfig pconf = ledgerInitSetting.getConsensusParticipant(i);
				String currPartAddress = pconf.getAddress();
				if (currPartAddress == null) {
					if (pconf.getPubKeyPath() != null) {
						PubKey pubKey = KeyGenCommand.readPubKey(pconf.getPubKeyPath());
						pconf.setPubKey(pubKey);
						currPartAddress = pconf.getAddress();
					}
				}
				if (localNodeAddress.equals(currPartAddress)) {
					currId = i;
				}
			}
			if (currId == -1) {
				throw new IllegalStateException("The current node specified in local.conf is not found in ledger.init!");
			}

			String base58Pwd = localConf.getLocal().getPassword();
			if (base58Pwd == null) {
				base58Pwd = KeyGenCommand.readPasswordString();
			}
			PrivKey privKey = KeyGenCommand.decodePrivKey(localConf.getLocal().getPrivKeyString(), base58Pwd);

			// Load consensus properties;
			Properties props = FileUtils.readProperties(localConf.getConsensusConfig());
			ConsensusProvider csProvider = ConsensusProviders.getProvider(localConf.getConsensusProvider());
			ConsensusSettings csSettings = csProvider.getSettingsFactory().getConsensusSettingsBuilder()
					.createSettings(props);


			// ConsensusProperties csProps = new ConsensusProperties(props);

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
			HashDigest newLedgerHash = initCommand.startInit(currId, privKey, base58Pwd, ledgerInitSetting, csSettings, csProvider,
					localConf.getStoragedDb(), new ConsolePrompter(), conf);

			if (newLedgerHash != null) {
				// success;
				// so save ledger binding config to file system;
				conf.store(ledgerBindingFile);
				ConsoleUtils.info("\r\n------ Update Ledger binding configuration success! ------[%s]",
						ledgerBindingFile.getAbsolutePath());
			}

			// ConsoleUtils.confirm("\r\n\r\n Press any key to quit. :>");

		} catch (Exception e) {
			ConsoleUtils.error("\r\nError!! -- %s\r\n", e.getMessage());
			if (argset.hasOption(DEBUG_OPT)) {
				e.printStackTrace();
			}

			ConsoleUtils.error("\r\n Ledger init process has been broken by error!");
		}

	}

	private LedgerManager ledgerManager;

	public LedgerManager getLedgerManager() {
		return ledgerManager;
	}

	public LedgerInitCommand() {
	}

	public HashDigest startInit(int currId, PrivKey privKey, String base58Pwd, LedgerInitProperties ledgerSetting,
			ConsensusSettings csSettings, ConsensusProvider csProvider, DBConnectionConfig dbConnConfig,
			Prompter prompter, LedgerBindingConfig conf, Object... extBeans) {
		if (currId < 0 || currId >= ledgerSetting.getConsensusParticipantCount()) {
			ConsoleUtils.info(
					"Your participant id is illegal which is less than 1 or great than the total participants count[%s]!!!",
					ledgerSetting.getConsensusParticipantCount());
			return null;
		}

		// generate binding config;
		BindingConfig bindingConf = new BindingConfig();
		// bindingConf.setCsConfigFile(localConf.getConsensusConfig());
		bindingConf.getParticipant().setAddress(ledgerSetting.getConsensusParticipant(currId).getAddress());
		String encodedPrivKey = KeyGenCommand.encodePrivKey(privKey, base58Pwd);
		bindingConf.getParticipant().setPk(encodedPrivKey);
		bindingConf.getParticipant().setPassword(base58Pwd);

		bindingConf.getDbConnection().setConnectionUri(dbConnConfig.getUri());
		bindingConf.getDbConnection().setPassword(dbConnConfig.getPassword());

		// bindingConf.getMqConnection().setServer(mqConnConfig.getServer());
		// bindingConf.getMqConnection().setTopic(mqConnConfig.getTopic());

		// confirm continue；
		prompter.info("\r\n\r\n This is participant [%s], the ledger initialization is ready to start!\r\n", currId);
		// ConsoleUtils.confirm("Press any key to continue... ");
		// prompter.confirm("Press any key to continue... ");

		// start web listener;
		NetworkAddress serverAddress = ledgerSetting.getConsensusParticipant(currId).getInitializerAddress();
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

		prompter.info("\r\n------ Web listener[%s:%s] was started. ------\r\n", serverAddress.getHost(),
				serverAddress.getPort());

		try {
			LedgerInitProcess initProc = ctx.getBean(LedgerInitProcess.class);
			HashDigest ledgerHash = initProc.initialize(currId, privKey, ledgerSetting, csSettings, csProvider,
					bindingConf.getDbConnection(), prompter);

			if (ledgerHash == null) {
				// ledger init fail;
				ConsoleUtils.error("\r\n------ Ledger initialize fail! ------\r\n");
				return null;
			} else {
				ConsoleUtils.info("\r\n------ Ledger initialize success! ------");
				ConsoleUtils.info("New Ledger Hash is :[%s]", ledgerHash.toBase58());

				if (conf == null) {
					conf = new LedgerBindingConfig();
				}
				conf.addLedgerBinding(ledgerHash, bindingConf);

				return ledgerHash;

			}
		} finally {
			ctx.close();
			ConsoleUtils.info("\r\n------ Web listener[%s:%s] was closed. ------\r\n", serverAddress.getHost(),
					serverAddress.getPort());
		}
	}

}
