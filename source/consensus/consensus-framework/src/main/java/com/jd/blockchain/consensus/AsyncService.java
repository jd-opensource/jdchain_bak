package com.jd.blockchain.consensus;

import java.lang.reflect.Method;

import com.jd.blockchain.utils.concurrent.AsyncFuture;

public interface AsyncService {
	
	AsyncFuture<Object> invoke(Method method, Object[] args);
	
}
