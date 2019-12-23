package com.jd.blockchain.utils.http;

import java.lang.reflect.Method;

public interface ResponseBodyConverterFactory {

	ResponseConverter createResponseConverter(HttpAction actionDef, Method mth);
	
}
