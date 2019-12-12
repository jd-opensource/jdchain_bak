package com.jd.blockchain.runtime.modular;

import com.jd.blockchain.runtime.AbstractModule;

public class JarsModule extends AbstractModule {
	
	private String name;
	
	private ClassLoader classLoader;
	
	public JarsModule(String name, ClassLoader classLoader) {
		this.name = name;
		this.classLoader = classLoader;
	}
	

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected ClassLoader getModuleClassLoader() {
		return classLoader;
	}

	
}
