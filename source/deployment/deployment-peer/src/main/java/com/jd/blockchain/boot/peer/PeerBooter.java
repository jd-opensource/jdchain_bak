package com.jd.blockchain.boot.peer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.blockchain.runtime.boot.HomeBooter;
import com.jd.blockchain.runtime.boot.HomeContext;

/**
 * Peer 启动器；
 * 
 * @author huanghaiquan
 *
 */
public class PeerBooter {

	public static final String MODULAR_FACTORY_CLASS = "com.jd.blockchain.runtime.modular.ModularFactory";

	public static final String MODULAR_FACTORY_METHOD = "startSystem";

	public static final Class<?>[] MODULAR_FACTORY_METHOD_ARG_TYPES = { String.class, boolean.class, ClassLoader.class,
			String.class, ClassLoader.class, String[].class };

	public static final String SYSTEM_MAIN_CLASS = "com.jd.blockchain.peer.PeerServerBooter";

	public static void main(String[] args) {
		try {

			HomeContext homeContext = HomeBooter.createHomeContext(args);

			startPeer(homeContext);

			writePID(homeContext.getHomeDir());
		} catch (Exception e) {
			System.err.println("Error!!! --[" + e.getClass().getName() + "] " + e.getMessage());
		}
	}

	public static void startPeer(HomeContext home) throws IOException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// start system;
		Class<?> modularFactoryClass = home.getSystemClassLoader().loadClass(MODULAR_FACTORY_CLASS);
		Method modularFactoryMethod = modularFactoryClass.getMethod(MODULAR_FACTORY_METHOD,
				MODULAR_FACTORY_METHOD_ARG_TYPES);

		Object[] systemStartingArgs = { home.getRuntimeDir(), home.isProductMode(), home.getLibsClassLoader(),
				SYSTEM_MAIN_CLASS, home.getSystemClassLoader(), home.getStartingArgs() };
		modularFactoryMethod.invoke(null, systemStartingArgs);
	}

	private static final void writePID(String homeDir) throws IOException {
		String pidFilePath = homeDir + File.separator + "bin" + File.separator + "PID.log";
		File pidFile = new File(pidFilePath);
		if (!pidFile.exists()) {
			pidFile.createNewFile();
		}
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String pid = name.split("@")[0];
		List<String> bootInfos = new ArrayList<>();
		bootInfos.add("JDChain peer node starts to boot ......\r\n");
		bootInfos.add(String.format("PEER_BOOT_TIME = [%s] \r\n", new Date().toString()));
		bootInfos.add(String.format("PEER_BOOT_PID = [%s] \r\n", pid));
		try (FileOutputStream outputStream = new FileOutputStream(pidFile)) {
			for (String bootInfo : bootInfos) {
				outputStream.write(bootInfo.getBytes());
			}
			outputStream.flush();
		}
	}
}
