package com.jd.blockchain.utils;

import java.util.Properties;
import java.util.Set;

public class Attributes extends Properties implements AttributeMap {

	private static final long serialVersionUID = 142263972661078077L;

	@Override
	public Set<String> getAttributeNames() {
		return stringPropertyNames();
	}

	@Override
	public boolean containAttribute(String name) {
		return containsKey(name);
	}

	@Override
	public String getAttribute(String name) {
		return getProperty(name);
	}

}
