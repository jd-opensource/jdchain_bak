package test.com.jd.blockchain.ledger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.contract.jvm.InstantiatedContractCode;
import com.jd.blockchain.ledger.core.ContractAccount;
import com.jd.blockchain.ledger.core.impl.handles.AbtractContractEventHandle;
import com.jd.blockchain.utils.Bytes;

public class ContractInvokingHandle extends AbtractContractEventHandle {

	private Map<Bytes, ContractCode> contractInstances = new ConcurrentHashMap<Bytes, ContractCode>();

	@Override
	protected ContractCode loadContractCode(ContractAccount contract) {
		return contractInstances.get(contract.getAddress());
	}

	public <T> ContractCode setup(Bytes address, Class<T> contractIntf, T instance) {
		InstantiatedContractCode<T> contract = new InstantiatedContractCode<T>(address, 0, contractIntf, instance);
		contractInstances.put(address, contract);
		return contract;
	}


}
