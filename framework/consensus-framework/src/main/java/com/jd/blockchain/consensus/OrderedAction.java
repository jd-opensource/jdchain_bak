package com.jd.blockchain.consensus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个共识方法调用模式为“有序的消息调用”；
 * 
 * @author huanghaiquan
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderedAction {
	
	/**
	 * 请求分组的索引器；<br>
	 * 
	 * 指定一个 {@link GroupIndexer} 接口的实现类，用于根据请求消息列表来生成共识的分组ID；
	 * @return
	 */
	Class<?> groupIndexer() ;
	
	/**
	 * 回复消息转换器；
	 * <p>
	 * 指定一个 {@link BinaryMessageConverter} 接口的实现类；
	 * 
	 * @return
	 */
	Class<?> responseConverter();
	
}
