package com.jd.blockchain.contract.jvm;

import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.utils.Bytes;

public class InstantiatedContractCode<T> extends AbstractContractCode {

		private T instance;

		public InstantiatedContractCode(Bytes address, long version, Class<T> delaredInterface, T instance) {
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