package com.jd.blockchain.utils.serialize.json;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

public class RuntimeDeserializer implements ObjectDeserializer {
	
	private Map<Class<?>, Class<?>> typeMap = new ConcurrentHashMap<Class<?>, Class<?>>();
	
	synchronized void addTypeMap(Class<?> fromClazz, Class<?> toClazz){
		typeMap.put(fromClazz, toClazz);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		if (type instanceof Class) {
			Class<?> toClazz = typeMap.get((Class<?>) type);
			if (toClazz != null) {
				return (T) parser.parseObject(toClazz);
			}
		}
		return (T) parser.parse(fieldName);
	}

	@Override
	public int getFastMatchToken() {
		return JSONToken.LBRACE;
	}

}
