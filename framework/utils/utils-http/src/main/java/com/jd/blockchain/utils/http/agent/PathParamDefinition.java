package com.jd.blockchain.utils.http.agent;

import com.jd.blockchain.utils.http.StringConverter;

class PathParamDefinition {
	
	private String name;
	
	private StringConverter converter;

	public String getName() {
		return name;
	}

	public StringConverter getConverter() {
		return converter;
	}

	public PathParamDefinition(String name, StringConverter converter) {
		this.name = name;
		this.converter = converter;
	}
}
