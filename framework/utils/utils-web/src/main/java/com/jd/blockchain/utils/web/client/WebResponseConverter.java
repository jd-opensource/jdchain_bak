package com.jd.blockchain.utils.web.client;

import java.io.InputStream;
import java.lang.reflect.Proxy;

import com.alibaba.fastjson.JSONObject;
import com.jd.blockchain.utils.PrimitiveUtils;
import com.jd.blockchain.utils.http.HttpServiceContext;
import com.jd.blockchain.utils.http.ResponseConverter;
import com.jd.blockchain.utils.http.agent.ServiceRequest;
import com.jd.blockchain.utils.http.converters.JsonResponseConverter;
import com.jd.blockchain.utils.http.converters.StringResponseConverter;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;
import com.jd.blockchain.utils.web.model.WebResponse;

public class WebResponseConverter implements ResponseConverter {
	
	private JsonResponseConverter jsonConverter = new JsonResponseConverter(WebResponse.class);
	
	private Class<?> dataClazz;
	
	public WebResponseConverter(Class<?> dataClazz) {
		this.dataClazz = dataClazz;
	}

	@Override
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception {
//		if (dataClazz.isInterface()) {
//			String json = (String) StringResponseConverter.INSTANCE.getResponse(request, responseStream, null);
//			if (json == null) {
//				return null;
//			}
//			JSONObject jsonObj = JSONSerializeUtils.deserializeAs(json, JSONObject.class);
//			return Proxy.newProxyInstance(dataClazz.getClassLoader(), new Class[] {dataClazz}, jsonObj);
//		}
		WebResponse response = (WebResponse) jsonConverter.getResponse(request, responseStream, null);
		if (response == null) {
			return null;
		}
		if (response.getError() != null) {
			throw new WebServiceException(response.getError().getErrorCode(), response.getError().getErrorMessage());
		}
		if (response.getData() == null) {
			return null;
		}
		if (dataClazz.isAssignableFrom(response.getData().getClass())) {
			return response.getData();
		}
		if (dataClazz.isAssignableFrom(String.class)) {
			return response.getData().toString();
		}
		if (PrimitiveUtils.isPrimitiveType(dataClazz)) {
			return PrimitiveUtils.castTo(response.getData(), dataClazz);
		}
		return JSONSerializeUtils.deserializeAs(response.getData(), dataClazz);
//		return response.getData();
		
//		JSONString jsonData = response.getData();
//		Object data = SerializeUtils.deserializeAs(jsonData, dataClazz);
//		return data;

//		if (response.getData() instanceof JSONObject) {
//			JSONObject jsonObj = (JSONObject) response.getData();
//			data = jsonObj.toJavaObject(dataClazz);
//		}else if (response.getData() instanceof JSONArray) {
//			JSONArray jsonObj = (JSONArray) response.getData();
//			data = jsonObj.toJavaObject(dataClazz);
//		}else{
//			data = response.getData();
//		}
//		return data;
	}
}
