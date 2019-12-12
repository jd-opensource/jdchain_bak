package com.jd.blockchain.utils.http.agent;

import java.lang.reflect.Method;
import java.nio.charset.Charset;

import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.http.RequestParamFilter;
import com.jd.blockchain.utils.http.ResponseConverter;

/**
 * 服务操作上下文；
 * 
 * 维持了一个特定的服务操作相关的参数定义；
 * 
 * @author haiq
 *
 */
class ServiceActionContext {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private Method serviceMethod;

	private HttpMethod requestMethod;

	private RequestPathTemplate pathTemplate;

	private PathParamResolver pathParamResolver;
	
	private RequestParamFilter requestParamFilter;

	private RequestParamResolver requestParamResolver;

	private RequestBodyResolver requestBodyResolver;

	private ResponseConverter responseConverter;
	
	private Class<?>[] thrownExceptionTypes;
	
	private String contentType;

	private boolean resolveContentOnHttpError;

	/**
	 * 创建服务操作上下文；
	 * 
	 * @param serviceMethod
	 *            服务接口的方法；
	 * @param requestMethod
	 *            服务操作调用所采用的 HTTP 方法；
	 * @param pathTemplate
	 *            服务操作的 HTTP 路径模板；
	 * @param pathParamResolver
	 *            服务操作的 HTTP URL 路径参数解析器；
	 * @param requestParamResolver
	 *            服务操作的 HTTP URL 查询参数解析器；
	 * @param requestBodyResolver
	 *            服务操作的 HTTP 请求体解析器；
	 * @param responseConverter
	 *            服务操作的 HTTP 成功回复结果转换器；
	 * @param thrownExceptionTypes
	 *            服务接口的方法通过 throws 关键字声明的异常的类型列表；
	 * @param resolveContentOnHttpError
	 *            是否在 HTTP 错误中包含回复的 HTTP 内容；
	 */
	public ServiceActionContext(Method serviceMethod, HttpMethod requestMethod, String contentType, RequestPathTemplate pathTemplate,
			PathParamResolver pathParamResolver, RequestParamFilter requestParamFilter, 
			RequestParamResolver requestParamResolver, RequestBodyResolver requestBodyResolver, 
			ResponseConverter responseConverter, Class<?>[] thrownExceptionTypes,
			boolean resolveContentOnHttpError) {
		this.serviceMethod = serviceMethod;
		this.requestMethod = requestMethod;
		this.contentType = contentType;
		this.pathTemplate = pathTemplate;
		this.pathParamResolver = pathParamResolver;
		this.requestParamFilter = requestParamFilter;
		this.requestParamResolver = requestParamResolver;
		this.requestBodyResolver = requestBodyResolver;
		this.responseConverter = responseConverter;
		this.thrownExceptionTypes = thrownExceptionTypes;
		this.resolveContentOnHttpError = resolveContentOnHttpError;
	}

	/**
	 * 请求路径模板；
	 * 
	 * @return
	 */
	public RequestPathTemplate getPathTemplate() {
		return pathTemplate;
	}

	/**
	 * 路径参数解析器；
	 * 
	 * @return
	 */
	public PathParamResolver getPathParamResolver() {
		return pathParamResolver;
	}

	/**
	 * 请求参数解析器；
	 * 
	 * @return
	 */
	public RequestParamResolver getRequestParamResolver() {
		return requestParamResolver;
	}

	/**
	 * 回复结果转换器；
	 * 
	 * @return
	 */
	public ResponseConverter getResponseConverter() {
		return responseConverter;
	}

	public Method getServiceMethod() {
		return serviceMethod;
	}

	public HttpMethod getRequestMethod() {
		return requestMethod;
	}

	public RequestBodyResolver getRequestBodyResolver() {
		return requestBodyResolver;
	}

	public boolean isResolveContentOnHttpError() {
		return resolveContentOnHttpError;
	}

	public Class<?>[] getThrownExceptionTypes() {
		return thrownExceptionTypes;
	}

	public RequestParamFilter getRequestParamFilter() {
		return requestParamFilter;
	}

	public String getContentType() {
		return contentType;
	}

}
