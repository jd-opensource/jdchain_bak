package com.jd.blockchain.utils.web.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

public class JsonWebResponseMessageConverter extends FastJsonHttpMessageConverter{
	
	public JsonWebResponseMessageConverter() {
		this(false);
	}
	
	public JsonWebResponseMessageConverter(boolean jsonPretty) {
		if (jsonPretty) {
			getFastJsonConfig().setSerializerFeatures(SerializerFeature.PrettyFormat);
		}
	}

	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		//把返回结果自动转换为 WebResponse；
		if (obj instanceof WebResponse) {
			super.writeInternal(obj, outputMessage);
			return;
		} else if (obj.getClass().isArray()) {
			// 数组类型需要判断是否为代理对象
			Object[] objects = (Object[])obj;
			if (objects != null && objects.length > 0) {
				Object[] results = new Object[objects.length];
				for (int i = 0; i < objects.length; i++) {
					Object o = objects[i];
					if (o instanceof Proxy) {
						try {
							results[i] = proxy2Obj(o);
						} catch (Exception e) {
							super.writeInternal(WebResponse.createSuccessResult(obj), outputMessage);
							return;
						}
					} else {
						results[i] = o;
					}
				}
				super.writeInternal(WebResponse.createSuccessResult(results), outputMessage);
				return;
			}
		} else if (obj instanceof Proxy) {
			try {
				Object result = proxy2Obj(obj); //获取Proxy对象进行转换
				super.writeInternal(WebResponse.createSuccessResult(result), outputMessage);
				return;
			} catch (Exception e) {
				super.writeInternal(WebResponse.createSuccessResult(obj), outputMessage);
				return;
			}
		}
		super.writeInternal(WebResponse.createSuccessResult(obj), outputMessage);
	}

	private Object proxy2Obj(Object obj) throws Exception {
		Field field = obj.getClass().getSuperclass().getDeclaredField("h");
		field.setAccessible(true);
		//获取指定对象中此字段的值
		return field.get(obj); //获取Proxy对象中的此字段的值
	}
}
