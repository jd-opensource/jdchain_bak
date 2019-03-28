package com.jd.blockchain.contract.jvm;

import com.jd.blockchain.contract.ContractCode;
import com.jd.blockchain.contract.model.ContractEvent;
import com.jd.blockchain.contract.model.ContractEventContext;
import com.jd.blockchain.runtime.Module;
import com.jd.blockchain.utils.BaseConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import static com.jd.blockchain.utils.BaseConstant.CONTRACT_MAIN_CLASS_KEY;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * contract code based jvm
 *  @author zhaogw
 */
public class JavaContractCode implements ContractCode {
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaContractCode.class);
	private Module codeModule;
	private String address;
	private long version;
	private ContractEventContext contractEventContext;
	
	public JavaContractCode(String address, long version, Module codeModule) {
		this.address = address;
		this.version = version;
		this.codeModule = codeModule;
	}

	@Override
	public String getAddress() {
		return address;
	}
	
	@Override
	public long getVersion() {
		return version;
	}

	@Override
	public void processEvent(ContractEventContext eventContext) {
		this.contractEventContext = eventContext;
		codeModule.execute(new ContractThread());
	}

	class ContractThread implements Runnable{
		@Override
		public void run(){
			LOGGER.info("ContractThread execute().");
			try {
				//Perform pretreatment;
				long startTime = System.currentTimeMillis();

				String contractClassName = codeModule.getMainClass();
				Class myClass = codeModule.loadClass(contractClassName);
				Object contractMainClassObj = myClass.newInstance();

				Method beforeMth_ = myClass.getMethod("beforeEvent",codeModule.loadClass(ContractEventContext.class.getName()));
				ReflectionUtils.invokeMethod(beforeMth_,contractMainClassObj,contractEventContext);
				LOGGER.info("beforeEvent,spend time:"+(System.currentTimeMillis()-startTime));

				Method eventMethod = this.getMethodByAnno(contractMainClassObj,contractEventContext.getEvent());
				startTime = System.currentTimeMillis();

				ReflectionUtils.invokeMethod(eventMethod,contractMainClassObj,contractEventContext);

				LOGGER.info("execute contract,spend time:"+(System.currentTimeMillis()-startTime));

				Method mth2 = myClass.getMethod("postEvent");
				startTime = System.currentTimeMillis();
				ReflectionUtils.invokeMethod(mth2,contractMainClassObj);
				LOGGER.info("postEvent,spend time:"+(System.currentTimeMillis()-startTime));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//get the relation between the methods and annotations
		Method getMethodByAnno(Object classObj, String eventName){
			Class<?> c = classObj.getClass();
			Class <ContractEvent> contractEventClass = null;
			try {
				contractEventClass = (Class <ContractEvent>)c.getClassLoader().loadClass(ContractEvent.class.getName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Method[] classMethods = c.getMethods();
			Map<Method, Annotation[]> methodAnnoMap = new HashMap<Method, Annotation[]>();
			Map<String,Method> annoMethodMap = new HashMap<String,Method>();
			for(int i = 0;i<classMethods.length;i++){
				Annotation[] a = classMethods[i].getDeclaredAnnotations();
				methodAnnoMap.put(classMethods[i], a);
				//if contain @ContractEvent in the method,then put it to the map;
				for(Annotation annotation_ : a){
					//if is the type of contractEvent,then put it to the map;
					if(classMethods[i].isAnnotationPresent(contractEventClass)){
						Object obj = classMethods[i].getAnnotation(contractEventClass);
						String annoAllName = obj.toString();
						//format:@com.jd.blockchain.contract.model.ContractEvent(name=transfer-asset)
						String eventName_ = obj.toString().substring(BaseConstant.CONTRACT_EVENT_PREFIX.length(),annoAllName.length()-1);
						annoMethodMap.put(eventName_,classMethods[i]);
						break;
					}
				}
			}
			if(annoMethodMap.containsKey(eventName)){
				return annoMethodMap.get(eventName);
			}else {
				return null;
			}
		}
	}
}
