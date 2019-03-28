package com.jd.blockchain.utils.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jd.blockchain.utils.http.converters.ObjectToStringConverter;

/**
 * 标识方法的一个参数映射为请求路径的一部分；
 * 
 * 一般情况下，应该使用 String 类型作为路径参数并以此类进行标注；
 * 
 * 但如果被标注的参数的类型不是 String 类型而是其它，则通过 toString 方法来获得实际的路径参数值；
 * 
 * @author haiq
 *
 */
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathParam {

	/**
	 * 路径中的变量名；
	 * 
	 * @return
	 */
	public String name();
	
	/**
	 * 参数值转换器的类型；
	 * 
	 * 指定的参数值转换器必须实现 StringConverter 接口；
	 * 
	 * 如果未指定，则默认通过 toString() 方法获取参数最终的文本值；
	 * 
	 * @return
	 */
	public Class<?> converter() default ObjectToStringConverter.class;

}
