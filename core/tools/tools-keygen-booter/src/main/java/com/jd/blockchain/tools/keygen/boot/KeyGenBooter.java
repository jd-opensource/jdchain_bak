package com.jd.blockchain.tools.keygen.boot;

import com.jd.blockchain.tools.keygen.KeyGenCommand;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class KeyGenBooter {

	private static final String charSet = "UTF-8";

	private static final String jarSuffix = ".jar";
	
	public static void main(String[] args) {
		load();
		KeyGenCommand.main(args);
	}


	private static void load() {
		List<File> jarPaths = loadPaths();
		try {
			if (!jarPaths.isEmpty()) {
				loadJars(jarPaths);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void loadJars(List<File> jarPaths) throws Exception {
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		boolean accessible = method.isAccessible();
		try {
			if (accessible == false) {
				method.setAccessible(true);
			}
			// 获取系统类加载器
			URLClassLoader classLoader = (URLClassLoader) Thread
					.currentThread()
					.getContextClassLoader();
			for (File file : jarPaths) {
				URL url = file.toURI().toURL();
				try {
					method.invoke(classLoader, url);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		} finally {
			method.setAccessible(accessible);
		}
	}

	private static List<File> loadPaths() {
		List<File> loadJarFiles = new ArrayList<>();
		URL url = KeyGenBooter.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation();
		try {
			String currPath = java.net.URLDecoder.decode(url.getPath(), charSet);
			if (currPath.endsWith(jarSuffix)) {
				currPath = currPath.substring(0, currPath.lastIndexOf("/") + 1);
			}
			File file = new File(currPath);
			loadJarFiles.addAll(dirJars(file));
			// 获取上级路径
			String systemPath = file.getParent() + File.separator + "system";
			loadJarFiles.addAll(dirJars(new File(systemPath)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return loadJarFiles;
	}

	private static List<File> dirJars(File dir) {
		List<File> jars = new ArrayList<>();
		if (dir.exists() && dir.isDirectory()) {
			File[] jarArray = dir.listFiles();
			if (jarArray != null) {
				for (File jar : jarArray) {
					if (jar.getAbsolutePath().endsWith(jarSuffix)) {
						jars.add(jar);
					}
				}
			}
		}
		return jars;
	}
}
