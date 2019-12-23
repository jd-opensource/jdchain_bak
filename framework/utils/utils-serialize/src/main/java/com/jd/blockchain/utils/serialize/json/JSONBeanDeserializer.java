package com.jd.blockchain.utils.serialize.json;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

public class JSONBeanDeserializer implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		if (type instanceof Class && JSONBean.class.isAssignableFrom((Class<?>) type)) {
			Class<?> clazz = (Class<?>) type;
			try {
				JSONBean jsonBean = (JSONBean) clazz.newInstance();
				JSONObject jsonObj = (JSONObject) parser.parseObject(jsonBean.getJsonObject());
				jsonBean.setJsonObject(jsonObj);

				return (T) jsonBean;
			} catch (InstantiationException e) {
				throw new JSONException(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new JSONException(e.getMessage(), e);
			}
		}
		return (T) parser.parse(fieldName);
	}

	@Override
	public int getFastMatchToken() {
		return JSONToken.LBRACE;
	}

}
