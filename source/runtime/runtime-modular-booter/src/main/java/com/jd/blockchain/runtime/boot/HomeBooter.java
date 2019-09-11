package com.jd.blockchain.runtime.boot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 一个模块化代码加载的启动器;
 * 
 * @author huanghaiquan
 *
 */
public class HomeBooter {

	public static final String LIBS = "libs";
	public static final String SYSTEM = "system";
	public static final String RUNTIME = "runtime";

	private static final Pattern ARG_PATTERN = Pattern.compile("^[\\-]\\w+[ ]*(=).*$");

	private static final String HOME = "-home";

	private static final String MODE = "-mode";
	private static final String MODE_DEBUG = "debug";
	private static final String MODE_PRODUCT = "product";

	public static final Pattern MODE_PATTERN = Pattern.compile("^(" + MODE + ")[ ]*(=).*$");

	/**
	 * 以多 ClassLoader 方式启动系统；
	 * <p>
	 * 
	 * 以参数 -home 方式指定系统的 home 目录；<br>
	 * 以参数 -mode=product 指定以生产模式启动，否则以 debug 模式启动；
	 * <p>
	 * 
	 * 系统 home 目录下，以 libs、systems、runtime 区分3个模块： <br>
	 * libs: 公共库模块；<br>
	 * system: 系统运行模块；<br>
	 * runtime: 运行时动态模块；
	 * 
	 * @param args
	 */
	public static HomeContext createHomeContext(String[] args) throws IOException {
		Properties settings = resolveSettings(args);

		String mode = settings.getProperty(MODE);
		boolean productMode = mode != null && MODE_PRODUCT.equalsIgnoreCase(mode);

		String home = settings.getProperty(HOME);
		if (home == null) {
			throw new IllegalArgumentException("Miss home dir!");
		}

		List<String> peerArgList = new ArrayList<>();
		for (String a : args) {
			if (a.startsWith(HOME) || a.startsWith(MODE)) {
				continue;
			}
			peerArgList.add(a);
		}
		String[] peerArgs = peerArgList.size() > 0 ? peerArgList.toArray(new String[peerArgList.size()]) : new String[0];

		File homeDir = new File(home);
		if (!homeDir.isDirectory()) {
			throw new IllegalArgumentException(
					"Home directory don't exist or the path is a file! --" + homeDir.getCanonicalPath());
		}
		String homeAbsPath = homeDir.getCanonicalPath();
		File libDir = new File(homeAbsPath, LIBS);
		if (!libDir.isDirectory()) {
			throw new IllegalArgumentException(
					"Libs directory don't exist or the path is a file! --" + libDir.getAbsolutePath());
		}
		File systemDir = new File(homeAbsPath, SYSTEM);
		if (!systemDir.isDirectory()) {
			throw new IllegalArgumentException(
					"System directory don't exist or the path is a file! --" + systemDir.getAbsolutePath());
		}
		File runtimeDir = new File(homeAbsPath, RUNTIME);
		if (runtimeDir.isFile()) {
			throw new IllegalArgumentException("Runtime dir path is a file! --" + runtimeDir.getAbsolutePath());
		}
		if (!runtimeDir.exists()) {
			runtimeDir.mkdirs();
		}

		// 以 ExtClassLoader 作为所有创建的ClassLoader的 Parent；
		ClassLoader extClassLoader = HomeBooter.class.getClassLoader().getParent();

		URL[] libJars = loadClassPaths(libDir);
		showJars("-------- lib jars --------", libJars);
		URLClassLoader libClassLoader = new URLClassLoader(libJars, extClassLoader);

		URL[] systemJars = loadClassPaths(systemDir);
		showJars("-------- system jars --------", systemJars);
		URLClassLoader systemClassLoader = new URLClassLoader(systemJars, libClassLoader);

		return new HomeContext(libClassLoader, systemClassLoader, homeAbsPath, runtimeDir.getAbsolutePath(),
				productMode, peerArgs);
	}

	private static void showJars(String msg, URL[] jars) {
		System.out.println(msg);
		int i = 0;
		for (URL url : jars) {
			System.out.println(i + ": " + url.toString());
		}
		System.out.println("-----------------");
	}

	public static URL[] loadClassPaths(File dir) {
		try {
			File[] jars = dir.listFiles(f -> f.getName().endsWith(".jar") && f.isFile());
			URL[] classpaths = new URL[jars.length];
			for (int i = 0; i < classpaths.length; i++) {
				classpaths[i] = jars[i].toURI().toURL();
			}
			return classpaths;
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 解析参数中以 “-”或者“--”开头的以“=”分隔的键值参数；
	 * 
	 * @param args
	 * @return
	 */
	private static Properties resolveSettings(String[] args) {
		Properties prop = new Properties();
		if (args == null || args.length == 0) {
			return prop;
		}

		for (String arg : args) {
			if (ARG_PATTERN.matcher(arg).matches()) {
				int i = arg.indexOf("=");
				String argName = arg.substring(0, i).trim();
				String argValue = arg.substring(i + 1, arg.length()).trim();
				prop.setProperty(argName, argValue);
			}
		}

		return prop;
	}

}
