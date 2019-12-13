package com.jd.blockchain.peer;

import com.jd.blockchain.consts.Global;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.utils.ArgumentSet;
import com.jd.blockchain.utils.ConsoleUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 节点服务器；
 * 
 * @author huanghaiquan
 *
 */
public class PeerServerBooter {

	private static final Log log = LogFactory.getLog(PeerServerBooter.class);

	// 初始化账本绑定配置文件的路径；
	public static final String LEDGERBIND_ARG = "-c";
	// 服务地址；
	private static final String HOST_ARG = "-h";
	// 服务端口；
	private static final String PORT_ARG = "-p";
	// 是否输出调试信息；
	private static final String DEBUG_OPT = "-debug";

	public static String ledgerBindConfigFile;
	
	static {
		// 加载 Global ，初始化全局设置；
		Global.initialize();
	}

	public static void main(String[] args) {
		PeerServerBooter peerServerBooter = new PeerServerBooter();
		peerServerBooter.handle(args);
	}

	public void handle(String[] args){
		LedgerBindingConfig ledgerBindingConfig = null;
		ArgumentSet arguments = ArgumentSet.resolve(args,
				ArgumentSet.setting().prefix(LEDGERBIND_ARG, HOST_ARG, PORT_ARG).option(DEBUG_OPT));
		boolean debug = false;
		try {
			ArgumentSet.ArgEntry argLedgerBindConf = arguments.getArg(LEDGERBIND_ARG);
			ledgerBindConfigFile = argLedgerBindConf == null ? null : argLedgerBindConf.getValue();
			if (ledgerBindConfigFile == null) {
				ConsoleUtils.info("Load build-in default configuration ...");
				ClassPathResource configResource = new ClassPathResource("ledger-binding.conf");

				try (InputStream in = configResource.getInputStream()) {
					ledgerBindingConfig = LedgerBindingConfig.resolve(in);
				} catch (Exception e) {
					throw e;
				}
			} else {
				ConsoleUtils.info("Load configuration,ledgerBindConfigFile position="+ledgerBindConfigFile);
				File file = new File(ledgerBindConfigFile);
				ledgerBindingConfig = LedgerBindingConfig.resolve(file);
			}
			String host = null;
			ArgumentSet.ArgEntry hostArg = arguments.getArg(HOST_ARG);
			if (hostArg != null) {
				host = hostArg.getValue();
			}
			int port = 0;
			ArgumentSet.ArgEntry portArg = arguments.getArg(PORT_ARG);
			if (portArg != null) {
				try {
					port = Integer.parseInt(portArg.getValue());
				} catch (NumberFormatException e) {
					// ignore NumberFormatException of port argument;
				}
			}

			debug = arguments.hasOption(DEBUG_OPT);

			PeerServerBooter booter = new PeerServerBooter(ledgerBindingConfig, host, port);
			if(log.isDebugEnabled()){
				log.debug("PeerServerBooter's urls="+ Arrays.toString(((URLClassLoader) booter.getClass().getClassLoader()).getURLs()));
			}
			booter.start();
		} catch (Exception e) {
			ConsoleUtils.error("Error occurred on startup! --%s", e.getMessage());
			if (debug) {
				e.printStackTrace();
			}
		}
	}

	private LedgerBindingConfig ledgerBindingConfig;
	private String hostAddress;
	private int port;
	private Object[] externalBeans;
	private volatile ConfigurableApplicationContext appContext;

	public PeerServerBooter(){}

	public PeerServerBooter(LedgerBindingConfig ledgerBindingConfig, String hostAddress, int port,
							Object... externalBeans) {
		this.ledgerBindingConfig = ledgerBindingConfig;
		this.hostAddress = hostAddress;
		this.port = port;
		this.externalBeans = externalBeans;
	}

	public synchronized void start() {
		if (appContext != null) {
			throw new IllegalStateException("Peer server is running already!");
		}
		appContext = startServer(ledgerBindingConfig, hostAddress, port, externalBeans);
	}

	public synchronized void close() {
		if (appContext == null) {
			return;
		}
		ConfigurableApplicationContext ctx = appContext;
		appContext = null;
		ctx.close();
	}

	/**
	 * 启动服务；
	 * 
	 * @param ledgerBindingConfig
	 *            账本绑定配置；
	 * @param hostAddress
	 *            服务地址；如果为空，则采用默认配置;
	 * @param port
	 *            端口地址；如果小于等于 0 ，则采用默认配置；
	 * @return
	 */
	private static ConfigurableApplicationContext startServer(LedgerBindingConfig ledgerBindingConfig,
			String hostAddress, int port, Object... externalBeans) {
		List<String> argList = new ArrayList<String>();
		String argServerAddress = String.format("--server.address=%s", "0.0.0.0");
		argList.add(argServerAddress);
//		if (hostAddress != null && hostAddress.length() > 0) {
//			String argServerAddress = String.format("--server.address=%s", hostAddress);
//			argList.add(argServerAddress);
//		}
		if (port > 0) {
			String argServerPort = String.format("--server.port=%s", port);
			argList.add(argServerPort);
		}

		String[] args = argList.toArray(new String[argList.size()]);

		SpringApplication app = new SpringApplication(PeerConfiguration.class);
		if (externalBeans != null && externalBeans.length > 0) {
			app.addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) applicationContext -> {
				ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
				for (Object bean : externalBeans) {
					if (bean != null) {
						beanFactory.registerSingleton(bean.toString(), bean);
					}
				}
			});
		}
		// 启动 web 服务；
		ConfigurableApplicationContext ctx = app.run(args);

		// 建立共识网络；
		Map<String, LedgerBindingConfigAware> bindingConfigAwares = ctx.getBeansOfType(LedgerBindingConfigAware.class);
		for (LedgerBindingConfigAware aware : bindingConfigAwares.values()) {
			aware.setConfig(ledgerBindingConfig);
		}
		ConsensusManage consensusManage = ctx.getBean(ConsensusManage.class);
		consensusManage.runAllRealms();

		return ctx;
	}

	public DbConnectionFactory getDBConnectionFactory() {
		return appContext.getBean(DbConnectionFactory.class);
	}
}
