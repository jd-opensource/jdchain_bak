package com.jd.blockchain.utils.web.server;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ServletSetting {
	
	private Set<String> mappings = new LinkedHashSet<String>();
	
	private Integer loadOnStartup;
	
	private Boolean asyncSupported;
	

	public Set<String> getMappings() {
		return Collections.unmodifiableSet(mappings);
	}

	public void addMapping(String mapping) {
		this.mappings.add(mapping);
	}

	public Integer getLoadOnStartup() {
		return loadOnStartup;
	}

	public void setLoadOnStartup(Integer loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public Boolean getAsyncSupported() {
		return asyncSupported;
	}

	public void setAsyncSupported(Boolean asyncSupported) {
		this.asyncSupported = asyncSupported;
	}
	
}
