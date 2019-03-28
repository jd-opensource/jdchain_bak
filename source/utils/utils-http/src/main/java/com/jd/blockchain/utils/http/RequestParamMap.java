package com.jd.blockchain.utils.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识一个 POJO 类型的参数将其成员转换为请求参数表；
 * 
 * 此标注应用在方法参数中，并将参数对象中标注为 RequestParam 的属性作为请求参数加入参数表；
 * 
 * @author haiq
 *
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParamMap {

	/**
	 * 属性前缀；
	 * 
	 * 当一个方法中通过 RequestParams 定义了多个参数表时，可通过定义不同的前缀以防止属性名称冲突；
	 * 
	 * @return
	 */
	public String prefix() default "";

	/**
	 * 属性分隔符；
	 * 
	 * 最终的参数名由前缀+分隔符+原始参数名构成；
	 * 
	 * @return
	 */
	public String seperator() default ".";

	/**
	 * 参数是否必须提供；
	 * 
	 * 运行时如果必须的参数为 null 将引发异常；
	 * 
	 * 默认为 true；
	 * 
	 * @return
	 */
	public boolean required() default true;

	/**
	 * 自定义的转换器；
	 * 
	 * 必须实现 PropertiesConverter 接口；
	 * 
	 * @return
	 */
	public Class<?>converter() default PropertiesConverter.class;

}
