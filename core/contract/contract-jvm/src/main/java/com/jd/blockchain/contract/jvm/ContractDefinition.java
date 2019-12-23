package com.jd.blockchain.contract.jvm;

import com.jd.blockchain.contract.ContractType;

public class ContractDefinition {

	private ContractType type;

	private Class<?> mainClass;

	public Class<?> getMainClass() {
		return mainClass;
	}

	public ContractType getType() {
		return type;
	}

	public ContractDefinition(ContractType type, Class<?> mainClass) {
		this.type = type;
		this.mainClass = mainClass;
	}

}
