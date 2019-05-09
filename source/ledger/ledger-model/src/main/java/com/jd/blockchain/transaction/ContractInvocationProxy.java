package com.jd.blockchain.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ContractInvocationProxy  implements InvocationHandler {
	
	
	private String contractMessage;
	

	private ContractEventSendOperationBuilder sendOpBuilder;
	
	

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
