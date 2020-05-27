package com.jd.blockchain.contract.jvm;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.Callable;

import com.jd.blockchain.ledger.ContractExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.runtime.Module;
import com.jd.blockchain.utils.Bytes;

/**
 * 基于 java jar 包并且以模块化方式独立加载的合约代码；
 * 
 * @author huanghaiquan
 *
 */
public class JavaContractCode extends AbstractContractCode {
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaContractCode.class);
	private Module codeModule;
	private Bytes address;
	private long version;

	public JavaContractCode(Bytes address, long version, Module codeModule) {
		super(address, version, resolveContractDefinition(codeModule));
		this.address = address;
		this.version = version;
		this.codeModule = codeModule;
	}

	protected static ContractDefinition resolveContractDefinition(Module codeModule) {
		String mainClassName = codeModule.getMainClass();
		Class<?> mainClass = codeModule.loadClass(mainClassName);
		Class<?>[] interfaces = mainClass.getInterfaces();
		Class<?> contractInterface = null;
		for (Class<?> itf : interfaces) {
			Contract annoContract = itf.getAnnotation(Contract.class);
			if (annoContract != null) {
				if (contractInterface == null) {
					contractInterface = itf;
				} else {
					throw new ContractException(
							"One contract definition is only allowed to implement one contract type!");
				}
			}
		}
		if (contractInterface == null) {
			throw new ContractException("No contract type is implemented!");
		}
		ContractType type = ContractType.resolve(contractInterface);
		return new ContractDefinition(type, mainClass);
	}

	@Override
	public Bytes getAddress() {
		return address;
	}

	@Override
	public long getVersion() {
		return version;
	}

	@Override
	public BytesValue processEvent(ContractEventContext eventContext) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Start processing event{} of contract{}...", eventContext.getEvent(), address.toString());
		}
		try {
			return codeModule.call(new ContractExecution(eventContext));
		} catch (Exception ex) {
			LOGGER.error(String.format("Error occurred while processing event[%s] of contract[%s]! --%s",
					eventContext.getEvent(), address.toString(), ex.getMessage()), ex);
			throw ex;
		} finally {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End processing event{} of contract{}. ", eventContext.getEvent(), address.toString());
			}
		}
	}

	protected Object getContractInstance() {
		try {
			// 每一次调用都通过反射创建合约的实例；
			return getContractDefinition().getMainClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private class ContractExecution implements Callable<BytesValue> {
		private ContractEventContext eventContext;

		public ContractExecution(ContractEventContext contractEventContext) {
			this.eventContext = contractEventContext;
		}

		@Override
		public BytesValue call() {
			try {
				return JavaContractCode.super.processEvent(eventContext);
			} catch (Exception e) {
				throw new UndeclaredThrowableException(new ContractExecuteException());
			}
		}
	}

}
