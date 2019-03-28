package com.jd.blockchain.binaryproto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhangshuang3 on 2018/7/19.
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldSetter {

    /**
     * 名称；
     * <p>
     * 默认为属性的名称；
     *
     * @return
     */
    String name() default "";
    String type() default "";

}
