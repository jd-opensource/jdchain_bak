package com.jd.blockchain.binaryproto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jd.blockchain.utils.ValueType;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumField {

	/**
	 * 枚举值的类型；
	 * 
	 * <p>
	 * 注：只支持 {@link ValueType#INT8} ~ {@link ValueType#INT32} 这几种类型；
	 * 
	 * 
	 * @return
	 */
	ValueType type();
	
}
