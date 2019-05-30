package com.jd.blockchain.gateway.boot;

import com.jd.blockchain.gateway.GatewayServerBooter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatewayBooter {

	public static void main(String[] args) {
		try {
			writePID();
			GatewayServerBooter.main(args);
		} catch (Exception e) {
			System.err.println("Error!!! --[" + e.getClass().getName() + "] " + e.getMessage());
		}
	}

	private static final void writePID() throws Exception {
		URL url = GatewayBooter.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation();
		String currPath = java.net.URLDecoder.decode(url.getPath(), "UTF-8");
		if (currPath.contains("!/")) {
			currPath = currPath.substring(5, currPath.indexOf("!/"));
		}
		if (currPath.endsWith(".jar")) {
			currPath = currPath.substring(0, currPath.lastIndexOf("/") + 1);
		}
		System.out.printf("currentPath = %s \r\n", currPath);
		File file = new File(currPath);
		String homeDir = file.getParent();
		String pidFilePath = homeDir + File.separator + "bin" + File.separator + "PID.log";
		File pidFile = new File(pidFilePath);
		if (!pidFile.exists()) {
			pidFile.createNewFile();
		}
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String pid = name.split("@")[0];
		List<String> bootInfos = new ArrayList<>();
		bootInfos.add("JDChain gateway starts to boot ......\r\n");
		bootInfos.add(String.format("GW_BOOT_TIME = [%s] \r\n", new Date().toString()));
		bootInfos.add(String.format("GW_BOOT_PID = [%s] \r\n", pid));
		try (FileOutputStream outputStream = new FileOutputStream(pidFile)) {
			for (String bootInfo : bootInfos) {
				outputStream.write(bootInfo.getBytes(StandardCharsets.UTF_8));
			}
			outputStream.flush();
		}
	}
}
