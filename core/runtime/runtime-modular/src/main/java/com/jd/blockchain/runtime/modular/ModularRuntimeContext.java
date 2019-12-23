package com.jd.blockchain.runtime.modular;

import java.net.URL;
import java.net.URLClassLoader;

import com.jd.blockchain.runtime.RuntimeContext;

public class ModularRuntimeContext extends RuntimeContext {

	private String runtimeDir;

	private JarsModule libModule;
	
	private EnvSettings environment;

	public ModularRuntimeContext(String runtimeDir, JarsModule libModule, 
			boolean productMode) {
		this.environment = new EnvSettings();
		this.environment.setProductMode(productMode);

		this.runtimeDir = runtimeDir;
		this.libModule = libModule;
	}
	
	void register() {
		RuntimeContext.set(this);
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
		return new URLClassLoader(new URL[] {jarURL}, libModule.getModuleClassLoader());
	}

	// --------------------------- inner types -----------------------------

	private static class EnvSettings implements Environment {

		private boolean productMode;

		@Override
		public boolean isProductMode() {
			return productMode;
		}

		public void setProductMode(boolean productMode) {
			this.productMode = productMode;
		}

	}
}
