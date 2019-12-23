package com.jd.blockchain.consensus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huanghaiquan
 *
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionMessage {
	
	/**
	 * 请求参数转换器；
	 * <p>
	 * 指定一个 {@link BinaryMessageConverter} 接口的实现类；
	 * 
	 * @return
	 */
	Class<?> converter();
	
}
