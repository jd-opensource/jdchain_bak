package com.jd.blockchain.gateway.web;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.jd.blockchain.utils.web.model.WebResponse;

@RestControllerAdvice
public class GatewayJsonResponseAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		if (MappingJackson2HttpMessageConverter.class == converterType
				&& (returnType.getContainingClass().getName().startsWith("com.jd")
						|| returnType.getDeclaringClass().getName().startsWith("com.jd"))) {
			return true;
		}
		return false;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		if (body == null) {
			return WebResponse.createSuccessResult(null);
		}
		if (body instanceof ResponseEntity) {
			return body;
		}
		// 把返回结果自动转换为 WebResponse；
		if (body instanceof WebResponse) {
			return body;
		}
		return WebResponse.createSuccessResult(body);
	}

}