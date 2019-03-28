package com.jd.blockchain.utils.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP 服务方法；
 * 
 * @author haiq
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpAction {

	/**
	 * 请求路径；
	 * 
	 * 默认为空；
	 * 
	 * 此时按照 service 的 path 属性+方法名 产生最终的请求路径；
	 * 
	 * @return
	 */
	public String path() default "";

	/**
	 * HTTP 请求方法;
	 * 
	 * @return
	 */
	public HttpMethod method();

	/**
	 * HTTP 的 Content-Type;
	 * @return
	 */
	public String contentType() default "";
	
	
	
	/**
	 * 自定义的返回值转换器；必须实现 RequestParamFilter 接口；
	 * 
	 * @return
	 */
	public Class<?> requestParamFilter() default RequestParamFilter.class;

	/**
	 * 自定义的返回值转换器；必须实现 ResponseConverter 接口；
	 * 
	 * @return
	 */
	public Class<?> responseConverter() default ResponseConverter.class;

	/**
	 * 当检测到 http 错误时是否在引发的 HttpStatusException 中包含回复的内容；
	 * 
	 * 默认为 false；
	 * 
	 * @return
	 */
	public boolean resolveContentOnHttpError() default false;
}
