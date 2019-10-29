package com.jd.blockchain.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.io.RuntimeIOException;

public abstract class RuntimeContext {
	
	public static interface Environment {
		
		boolean isProductMode();
		
	}
	

	private static final Object mutex = new Object();

	private static volatile RuntimeContext runtimeContext;

	public static RuntimeContext get() {
		if (runtimeContext == null) {
			synchronized (mutex) {
				if (runtimeContext == null) {
					runtimeContext = new DefaultRuntimeContext();
				}
			}
		}
		return runtimeContext;
	}

	protected static void set(RuntimeContext runtimeContext) {
		if (RuntimeContext.runtimeContext != null) {
			throw new IllegalStateException("RuntimeContext has been setted!");
		}
		RuntimeContext.runtimeContext = runtimeContext;
	}

	private Map<String, Module> modules = new ConcurrentHashMap<>();
	
	public RuntimeContext() {
	}

	private File getDynamicModuleJarFile(String name) {
		name = name + ".mdl";
		return new File(getRuntimeDir(), name);
	}

	public Module getDynamicModule(String name) {
		return modules.get(name);
		
	}

	public List<Module> getDynamicModules() {
		return new ArrayList<>(modules.values());
	}

	public Module createDynamicModule(String name, byte[] jarBytes) {
		Module module = modules.get(name);
		if (module != null) {
			return module;
		}
		synchronized (DefaultRuntimeContext.class) {
			module = modules.get(name);
			if (module != null) {
				return module;
			}
		}

		// Save File to Disk;
		File jarFile = getDynamicModuleJarFile(name);
		if (jarFile.exists()) {
			if (jarFile.isFile()) {
				FileUtils.deleteFile(jarFile);
			} else {
				throw new IllegalStateException("Code storage conflict! --" + jarFile.getAbsolutePath());
			}
		}
		FileUtils.writeBytes(jarBytes, jarFile);

		try {
			URL jarURL = jarFile.toURI().toURL();
			ClassLoader moduleClassLoader = createDynamicModuleClassLoader(jarURL);

			Attributes m = new JarFile(jarFile).getManifest().getMainAttributes();
			String contractMainClass = m.getValue(Attributes.Name.MAIN_CLASS);
			module = new DefaultModule(name, moduleClassLoader, contractMainClass);
			modules.put(name, module);

			return module;
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	
	public abstract Environment getEnvironment();
	
	protected abstract String getRuntimeDir();

	protected abstract URLClassLoader createDynamicModuleClassLoader(URL jarURL);

	// ------------------------- inner types --------------------------
	
	private static class EnvSettings implements Environment{
		
		private boolean productMode;
		
		@Override
		public boolean isProductMode() {
			return productMode;
		}

		public void setProductMode(boolean productMode) {
			this.productMode = productMode;
		}
		
	}

	private static class DefaultModule extends AbstractModule {

		private String name;

		private ClassLoader moduleClassLoader;

		private String mainClass;

		public DefaultModule(String name, ClassLoader cl, String mainClass) {
			this.name = name;
			this.moduleClassLoader = cl;
			this.mainClass = mainClass;
		}

		@Override
		public String getMainClass() {
			return mainClass;
		}


		@Override
		public String getName() {
			return name;
		}

//		@Override
//		public Module getParent() {
//			return null;
//		}

		@Override
		protected ClassLoader getModuleClassLoader() {
			return moduleClassLoader;
		}

	}

	/**
	 * Default RuntimeContext is a context of that:<br>
	 * all modules are running in a single class loader;
	 * 
	 * @author huanghaiquan
	 *
	 */
	static class DefaultRuntimeContext extends RuntimeContext {

		protected String homeDir;

		protected String runtimeDir;
		
		protected EnvSettings environment;

		public DefaultRuntimeContext() {
			
			this.environment = new EnvSettings();
			this.environment.setProductMode(true);
			
			try {
				this.homeDir = new File("./").getCanonicalPath();
				this.runtimeDir = new File(homeDir, "runtime").getAbsolutePath();
			} catch (IOException e) {
				throw new RuntimeIOException(e.getMessage(), e);
			}
		}
		
		@Override
		public Environment getEnvironment() {
			return environment;
		}

		@Override
		protected String getRuntimeDir() {
			return runtimeDir;
		}

		@Override
		protected URLClassLoader createDynamicModuleClassLoader(URL jarURL) {
			return new ContractURLClassLoader(jarURL, RuntimeContext.class.getClassLoader());
		}

	}

	static class ContractURLClassLoader extends URLClassLoader {

		private static final String BLACK_CONFIG = "black.config";

		private static final Set<String> BLACK_CLASSES = new HashSet<>();

		private static final Set<String> BLACK_PACKAGES = new HashSet<>();

		static {
			initBlacks();
		}

		public ContractURLClassLoader(URL contractJarURL, ClassLoader parent) {
			super(new URL[] { contractJarURL }, parent);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (BLACK_CLASSES.contains(name)) {
				throw new IllegalStateException(String.format("Contract cannot use Class [%s]", name));
			} else {
				// 判断该包是否是黑名单
				String trimName = name.trim();
				String packageName = trimName.substring(0, trimName.length() - 2);
				if (BLACK_PACKAGES.contains(packageName)) {
					throw new IllegalStateException(String.format("Contract cannot use Class [%s]", name));
				}
			}
			return super.loadClass(name);
		}

		private static void initBlacks() {
			try {
				InputStream inputStream = ContractURLClassLoader.class.getResourceAsStream("/"+ BLACK_CONFIG);
				String text = FileUtils.readText(inputStream);
				String[] textArray = text.split("\n");
				for (String setting : textArray) {
					// 支持按照逗号分隔
					if (setting == null || setting.length() == 0) {
						continue;
					}
					String[] settingArray = setting.split(",");
					for (String set : settingArray) {
						String totalClass = set.trim();
						if (totalClass.endsWith("*")) {
							// 说明是包，获取具体包名
							String packageName = totalClass.substring(0, totalClass.length() - 2);
							BLACK_PACKAGES.add(packageName);
						} else {
							// 具体的类名，直接放入集合
							BLACK_CLASSES.add(totalClass);
						}
					}
				}
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		public static void main(String[] args) {
			for (String s : BLACK_CLASSES) {
				System.out.println(s);
			}

			for (String s : BLACK_PACKAGES) {
				System.out.println(s);
			}
		}
	}
}
