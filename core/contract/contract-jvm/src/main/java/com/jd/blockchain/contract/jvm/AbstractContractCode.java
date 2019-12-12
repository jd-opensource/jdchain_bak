package com.jd.blockchain.contract.jvm;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueEncoding;
import com.jd.blockchain.ledger.BytesValueList;
import com.jd.blockchain.utils.Bytes;

/**
 * @author huanghaiquan
 *
 */
public abstract class AbstractContractCode implements ContractCode {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractContractCode.class);
	private Bytes address;
	private long version;

	private ContractDefinition contractDefinition;

	public AbstractContractCode(Bytes address, long version, ContractDefinition contractDefinition) {
		this.address = address;
		this.version = version;
		this.contractDefinition = contractDefinition;
	}

	public ContractDefinition getContractDefinition() {
		return contractDefinition;
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
		EventProcessingAware evtProcAwire = null;
		Object retn = null;
		Method handleMethod = null;
		Exception error = null;
		try {
			// 执行预处理;
			Object contractInstance = getContractInstance();
			if (contractInstance instanceof EventProcessingAware) {
				evtProcAwire = (EventProcessingAware) contractInstance;
			}

			if (evtProcAwire != null) {
				evtProcAwire.beforeEvent(eventContext);
			}

			// 反序列化参数；
			handleMethod = contractDefinition.getType().getHandleMethod(eventContext.getEvent());

			if (handleMethod == null) {
				throw new ContractException(
						String.format("Contract[%s:%s] has no handle method to handle event[%s]!", address.toString(),
								contractDefinition.getType().getName(), eventContext.getEvent()));
			}
			
			BytesValueList bytesValues = eventContext.getArgs();
			Object[] args = BytesValueEncoding.decode(bytesValues, handleMethod.getParameterTypes());
			
			retn = ReflectionUtils.invokeMethod(handleMethod, contractInstance, args);
			
		} catch (Exception e) {
			error = e;
		}

		if (evtProcAwire != null) {
			try {
				evtProcAwire.postEvent(eventContext, error);
			} catch (Exception e) {
				String errorMessage = "Error occurred while posting contract event! --" + e.getMessage();
				LOGGER.error(errorMessage, e);
				throw new ContractException(errorMessage, e);
			}
		}
		if (error != null) {
			// Rethrow error;
			throw new ContractException(String.format("Error occurred while processing event[%s] of contract[%s]! --%s",
					eventContext.getEvent(), address.toString(), error.getMessage()), error);
		}

		BytesValue retnBytes = BytesValueEncoding.encodeSingle(retn, handleMethod.getReturnType());
		return retnBytes;
	}

	protected abstract Object getContractInstance();

}
