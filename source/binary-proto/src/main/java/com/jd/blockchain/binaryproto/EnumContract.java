package com.jd.blockchain.binaryproto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumContract {

	/**
	 * 类型编号；
	 * <p>
	 * 
	 * 而且，不同类型不能声明相同的编号； <p>
	 * 
	 * @return
	 */
	int code() ;

	String name() default "";

	String decription() default "";
	
}
