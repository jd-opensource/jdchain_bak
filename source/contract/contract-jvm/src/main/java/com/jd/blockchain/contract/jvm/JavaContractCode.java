package com.jd.blockchain.contract.jvm;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.runtime.Module;
import com.jd.blockchain.transaction.ContractType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.IllegalDataException;

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
	private ContractEventContext contractEventContext;

	private ContractType contractType;

	public JavaContractCode(Bytes address, long version, Module codeModule) {
		this.address = address;
		this.version = version;
		this.codeModule = codeModule;
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
	public void processEvent(ContractEventContext eventContext) {
		this.contractEventContext = eventContext;
		codeModule.execute(new ContractExecution());
	}

	private Object resolveArgs(byte[] args, List<DataContract> dataContractList) {
		if(args == null || args.length == 0){
			return null;
		}
		//TODO: Not implemented;
		throw new IllegalStateException("Not implemented!");
//		return ContractSerializeUtils.deserializeMethodParam(args,dataContractList);
	}

	class ContractExecution implements Runnable {
		public void run() {
			LOGGER.info("ContractThread execute().");
			try {
				// 执行预处理;
				long startTime = System.currentTimeMillis();

				String contractClassName = codeModule.getMainClass();
				Class myClass = codeModule.loadClass(contractClassName);
				Object contractMainClassObj = myClass.newInstance();// 合约主类生成的类实例;

				Method beforeMth_ = myClass.getMethod("beforeEvent",
						codeModule.loadClass(ContractEventContext.class.getName()));
				ReflectionUtils.invokeMethod(beforeMth_, contractMainClassObj, contractEventContext);
				LOGGER.info("beforeEvent,耗时:" + (System.currentTimeMillis() - startTime));

//				Method eventMethod = this.getMethodByAnno(contractMainClassObj, contractEventContext.getEvent());
				startTime = System.currentTimeMillis();

				// 反序列化参数；
				contractType = ContractType.resolve(myClass);
				Method handleMethod = contractType.getHandleMethod(contractEventContext.getEvent());
				if (handleMethod == null){
					throw new IllegalDataException("don't get this method by it's @ContractEvent.");
				}
				//TODO: Not implemented;
//				Object args = resolveArgs(contractEventContext.getArgs(),
//						contractType.getDataContractMap().get(handleMethod));
//
//				Object[] params = null;
//				if(args.getClass().isArray()){
//					params = (Object[])args;
//				}
//				ReflectionUtils.invokeMethod(handleMethod, contractMainClassObj, params);

				LOGGER.info("合约执行,耗时:" + (System.currentTimeMillis() - startTime));

				Method mth2 = myClass.getMethod("postEvent");
				startTime = System.currentTimeMillis();
				ReflectionUtils.invokeMethod(mth2, contractMainClassObj);
				LOGGER.info("postEvent,耗时:" + (System.currentTimeMillis() - startTime));
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(e.getMessage());
			} catch (Exception e) {
				throw new IllegalDataException(e.getMessage());
			}
		}
	}

}
