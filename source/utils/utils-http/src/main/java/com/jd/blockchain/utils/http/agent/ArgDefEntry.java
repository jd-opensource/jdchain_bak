package com.jd.blockchain.utils.http.agent;

class ArgDefEntry<TDef> {
	private int index;
	
	private Class<?> argType;

	private TDef definition;

	public int getIndex() {
		return index;
	}

	public Class<?> getArgType() {
		return argType;
	}
	
	public TDef getDefinition() {
		return definition;
	}

	public ArgDefEntry(int index, Class<?> argType, TDef definition) {
		this.index = index;
		this.argType = argType;
		this.definition = definition;
	}


}