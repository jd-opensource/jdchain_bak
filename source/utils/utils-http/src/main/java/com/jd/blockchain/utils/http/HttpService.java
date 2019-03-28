package com.jd.blockchain.utils.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpService {

	/**
	 * 服务的路径；
	 * <br>
	 * 它是所有服务方法的根路径，默认为 /；
	 * 
	 * @return
	 */
	public String path() default "/";

	/**
	 * 默认的回复转换器；
	 * <br>
	 * 当服务方法未指明自定义的回复转换器时，如果采用此默认回复转换器，如果服务的默认回复转换器也未设置，则采用系统预定义的默认回复转换器；
	 * 
	 * @return
	 */
	public Class<?>defaultResponseConverter() default ResponseConverter.class;
	
	/**
	 * 回复转换器工厂；
	 * <br>
	 * 当服务方法上未指定的回复转换器，同时服务上也未指定默认回复转换器，则通过此属性指定的回复转换器工厂创建每个方法的回复转换器；
	 * <br>
	 * 如果此属性也未设置，则采用系统预定义的默认回复转换器；

	 * @return
	 */
	public Class<?> responseConverterFactory() default ResponseBodyConverterFactory.class;
	
	/**
	 * 默认的请求体参数转换器；
	 * <br>
	 * 当 {@link RequestBody} 标注未指定转换器实现时，则采用此处的默认配置；
	 * 
	 * @return
	 */
	public Class<?> defaultRequestBodyConverter() default RequestBodyConverter.class;

}
