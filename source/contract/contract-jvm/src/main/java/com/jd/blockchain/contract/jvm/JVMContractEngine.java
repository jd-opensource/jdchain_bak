package com.jd.blockchain.contract.jvm;

import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.contract.engine.ContractEngine;
import com.jd.blockchain.runtime.Module;
import com.jd.blockchain.runtime.RuntimeContext;

public class JVMContractEngine implements ContractEngine {

	private RuntimeContext runtimeContext = RuntimeContext.get();
//    private RuntimeContext runtimeContext = ModularRuntimeContext.setup(System.getProperty("user.dir"));
	
	private String getCodeName(String address, long version) {
		return address + "_" + version;
	}
	
	@Override
	public ContractCode getContract(String address, long version) {
		String codeName = getCodeName(address, version);
		Module module = runtimeContext.getDynamicModule(codeName);
		if (module == null) {
			return null;
		}
 		return new JavaContractCode(address, version, module);
	}

	@Override
	public ContractCode setupContract(String address, long version, byte[] code) {
	    //is there the contractCode before setup? if yes ,then return;
        ContractCode contractCode = getContract(address,version);
        if(contractCode != null){
            return contractCode;
        }
		String codeName = getCodeName(address, version);
		Module module = runtimeContext.createDynamicModule(codeName,code);
		if (module == null) {
			return null;
		}
		return new JavaContractCode(address, version, module);
	}
}
