package com.jd.blockchain.consensus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huanghaiquan
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UnorderedAction {
	/**
	 * 请求分组的索引器；<br>
	 * 
	 * 指定一个 {@link GroupIndexer} 接口的实现类，用于根据请求消息列表来生成共识的分组ID；
	 * @return
	 */
	Class<?> groupIndexer() ;
}
