package com.jd.blockchain.utils.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个方法参数如何映射为 http 请求的 body ；
 * 
 * 注意：在一个方法中，最多只允许有一个参数被标识为 RequestBody；
 * 
 * @author haiq
 *
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
	
	public boolean required() default true;
	
	/**
	 * 参数值转换器的类型；
	 * 
	 * 指定的参数值转换器必须实现 RequestBodyConverter 接口；
	 * 
	 * 如果未指定，
	 * 
	 * 对于 InputStream 或 byte 数组类型，则直接输出字节内容；
	 * 
	 * 对于除此之外的其它类型，则通过 Object.toString() 方法获取文本值按照指定的编码；
	 * 
	 * @return
	 */
	public Class<?> converter() default RequestBodyConverter.class;
	
}
