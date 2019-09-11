package test.com.jd.blockchain.ledger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.contract.jvm.AbstractContractCode;
import com.jd.blockchain.contract.jvm.ContractDefinition;
import com.jd.blockchain.ledger.core.ContractAccount;
import com.jd.blockchain.ledger.core.handles.AbtractContractEventSendOperationHandle;
import com.jd.blockchain.utils.Bytes;

public class ContractInvokingHandle extends AbtractContractEventSendOperationHandle {

	private Map<Bytes, ContractCode> contractInstances = new ConcurrentHashMap<Bytes, ContractCode>();

	@Override
	protected ContractCode loadContractCode(ContractAccount contract) {
		return contractInstances.get(contract.getAddress());
	}

	public <T> ContractCode setup(Bytes address, Class<T> contractIntf, T instance) {
		ContractCodeInstance<T> contract = new ContractCodeInstance<T>(address, 0, contractIntf, instance);
		contractInstances.put(address, contract);
		return contract;
	}

	private static class ContractCodeInstance<T> extends AbstractContractCode {

		private T instance;

		public ContractCodeInstance(Bytes address, long version, Class<T> delaredInterface, T instance) {
			super(address, version, resolveContractDefinition(delaredInterface, instance.getClass()));
			this.instance = instance;
		}

		private static ContractDefinition resolveContractDefinition(Class<?> declaredIntf, Class<?> implementedClass) {
			ContractType contractType = ContractType.resolve(declaredIntf);
			return new ContractDefinition(contractType, implementedClass);
		}

		@Override
		protected T getContractInstance() {
			return instance;
		}

	}

}
