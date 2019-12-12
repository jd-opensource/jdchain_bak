package com.jd.blockchain.utils.web.client;

import java.lang.reflect.Method;

import com.jd.blockchain.utils.http.HttpAction;
import com.jd.blockchain.utils.http.ResponseBodyConverterFactory;
import com.jd.blockchain.utils.http.ResponseConverter;

public class WebResponseConverterFactory implements ResponseBodyConverterFactory{

	@Override
	public ResponseConverter createResponseConverter(HttpAction actionDef, Method mth) {
		return new WebResponseConverter(mth.getReturnType());
	}

}
