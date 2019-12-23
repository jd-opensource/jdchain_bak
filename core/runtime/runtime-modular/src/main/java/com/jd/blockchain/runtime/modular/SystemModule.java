package com.jd.blockchain.runtime.modular;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.jd.blockchain.runtime.AbstractModule;

public class SystemModule extends AbstractModule {

	private ClassLoader classloader;

	private String mainClassName;

	private ModularRuntimeContext modularRuntimeContext;

	public SystemModule(String mainClassName, ClassLoader classloader, ModularRuntimeContext modularRuntimeContext) {
		this.mainClassName = mainClassName;
		this.classloader = classloader;
		this.modularRuntimeContext = modularRuntimeContext;
	}

	@Override
	public String getName() {
		return "SystemModule";
	}

	public ModularRuntimeContext getModularRuntimeContext() {
		return modularRuntimeContext;
	}

	@Override
	protected ClassLoader getModuleClassLoader() {
		return classloader;
	}

	public void start(String[] args) {
		execute(new Runnable() {
			@Override
			public void run() {
				runMainClass(args);
			}
		});
	}

	private void runMainClass(String[] args) {
		try {
			Class<?> mainClass = loadClass(mainClassName);
			Method mainMethod = mainClass.getMethod("main", String[].class);
			if (!Modifier.isStatic(mainMethod.getModifiers())) {
				throw new IllegalArgumentException("Miss static main method in class[" + mainClassName + "]!");
			}
			mainMethod.invoke(null, (Object) args);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new IllegalStateException(
					String.format("Error occurred on running %s! --%s", mainClassName, e.getMessage()), e);
		}
	}


}
