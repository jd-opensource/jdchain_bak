package com.jd.blockchain.contract.jvm;

import com.jd.blockchain.contract.engine.ContractEngine;
import com.jd.blockchain.contract.engine.ContractServiceProvider;

public class JVMContractServiceProvider implements ContractServiceProvider {
	@Override
	public String getName() {
		return JVMContractServiceProvider.class.getName();
	}

	@Override
	public ContractEngine getEngine() {
		return InnerEngine.INSTANCE;
	}

	private static class InnerEngine {
		private static final ContractEngine INSTANCE = new JVMContractEngine();
	}
}
