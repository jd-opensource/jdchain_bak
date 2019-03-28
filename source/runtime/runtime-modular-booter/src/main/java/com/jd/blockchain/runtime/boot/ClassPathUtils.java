package com.jd.blockchain.runtime.boot;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ClassPathUtils {
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
}
