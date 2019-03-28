package com.jd.blockchain.utils.http.agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.ClassUtils;

import com.jd.blockchain.utils.http.RequestBodyConverter;
import com.jd.blockchain.utils.http.converters.ByteArrayBodyConverter;
import com.jd.blockchain.utils.http.converters.InputStreamBodyConverter;
import com.jd.blockchain.utils.http.converters.JsonBodyConverter;
import com.jd.blockchain.utils.http.converters.ObjectToStringBodyConverter;

/**
 * 类型自动匹配的 RequestBody 转换器；
 * @author haiq
 *
 */
class TypeAutoAdaptingRequestBodyConverter implements RequestBodyConverter{
	
	private static final RequestBodyConverter OBJECT_TO_STRING_CONVERTER = new ObjectToStringBodyConverter();
	private static final RequestBodyConverter INPUT_STREAM_CONVERTER = new InputStreamBodyConverter();
	private static final RequestBodyConverter BYTES_CONVERTER = new ByteArrayBodyConverter();
//	private static final RequestBodyConverter JSON_CONVERTER = new JsonBodyConverter();
	
	private RequestBodyConverter converter;
	
	public TypeAutoAdaptingRequestBodyConverter(Class<?> argType) {
		converter = createConverter(argType);
	}
	
	private RequestBodyConverter createConverter(Class<?> argType){
		if (ClassUtils.isAssignable(InputStream.class, argType)) {
			return INPUT_STREAM_CONVERTER;
		}
		if (ClassUtils.isAssignable(String.class, argType)) {
			return OBJECT_TO_STRING_CONVERTER;
		}
		if (ClassUtils.isAssignable(byte[].class, argType)) {
			return BYTES_CONVERTER;
		}
		if (ClassUtils.isPrimitiveOrWrapper(argType)) {
			return OBJECT_TO_STRING_CONVERTER;
		}
		if (ClassUtils.isAssignable(OutputStream.class, argType)) {
			throw new IllegalHttpServiceDefinitionException("Unsupported type for the request body argument!");
		}
		//默认按照 JSON 方式返回；
		return new JsonBodyConverter(argType);
//		return JSON_CONVERTER;
	}

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		if (param == null) {
			return;
		}
		converter.write(param, out);
	}

}
