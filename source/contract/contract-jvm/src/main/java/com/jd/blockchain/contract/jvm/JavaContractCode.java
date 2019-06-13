package com.jd.blockchain.contract.jvm;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.jd.blockchain.contract.ContractSerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAwire;
import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.runtime.Module;
import com.jd.blockchain.transaction.ContractType;
import com.jd.blockchain.utils.Bytes;

/**
 * contract code based jvm
 * 
 * @author zhaogw
 */
public class JavaContractCode implements ContractCode {
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaContractCode.class);
	private Module codeModule;
	private Bytes address;
	private long version;

	private Class<?> contractClass;
	private ContractType contractType;

	public JavaContractCode(Bytes address, long version, Module codeModule) {
		this.address = address;
		this.version = version;
		this.codeModule = codeModule;

		init();
	}

	private void init() {
		String contractClassName = codeModule.getMainClass();
		this.contractClass = codeModule.loadClass(contractClassName);
		this.contractType = ContractType.resolve(contractClass);
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
	public byte[] processEvent(ContractEventContext eventContext) {
		return codeModule.call(new ContractExecution(eventContext));
	}

	private class ContractExecution implements Callable<byte[]> {

		private ContractEventContext eventContext;

		public ContractExecution(ContractEventContext contractEventContext) {
			this.eventContext = contractEventContext;
		}

		@Override
		public byte[] call() throws Exception {
			EventProcessingAwire evtProcAwire = null;
			Object retn = null;
			Exception error = null;
			try {
				// 执行预处理;
				Object contractInstance = contractClass.newInstance();// 合约主类生成的类实例;
				if (contractInstance instanceof EventProcessingAwire) {
					evtProcAwire = (EventProcessingAwire) contractInstance;
				}

				if (evtProcAwire != null) {
					evtProcAwire.beforeEvent(eventContext);
				}

				// 反序列化参数；
				Method handleMethod = contractType.getHandleMethod(eventContext.getEvent());

				if (handleMethod == null) {
					throw new ContractException(
							String.format("Contract[%s:%s] has no handle method to handle event[%s]!",
									address.toString(), contractClass.getName(), eventContext.getEvent()));
				}

				Object[] args = resolveArgs(eventContext.getArgs());
				retn = ReflectionUtils.invokeMethod(handleMethod, contractInstance, args);
			} catch (Exception e) {
				error = e;
			}

			if (evtProcAwire != null) {
				try {
					evtProcAwire.postEvent(eventContext, error);
				} catch (Exception e) {
					LOGGER.error("Error occurred while posting contract event! --" + e.getMessage(), e);
				}
			}
			if (error != null) {
				// Rethrow error;
				throw error;
			}

			byte[] retnBytes = resolveResult(retn);
			return retnBytes;
		}

		private byte[] resolveResult(Object retn) {
			return ContractSerializeUtils.serialize(retn);
		}

		private Object[] resolveArgs(byte[] argBytes) {
			return ContractSerializeUtils.resolveArray(argBytes);
		}
	}

}
