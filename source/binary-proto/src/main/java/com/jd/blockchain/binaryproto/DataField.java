package com.jd.blockchain.binaryproto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个接口的字段作为数据契约的字段；
 * <p>
 * 
 * 字段的数据类型需要需要显式通过
 * {@link #primitiveType()}、{@link #refEnum()}、{@link #refContract()}
 * 3个属性之一标注（只能标记一种）；
 * 
 * @author huanghaiquan
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataField {

	/**
	 * 字段顺序；
	 * <p>
	 * 
	 * 顺序编号实际并不会输出，仅用于对字段进行升序排列；
	 * <p>
	 * 
	 * 注：对于已经发布使用的数据契约，都不应该调整顺序，否则会导致无法正确解析已有数据；
	 * 
	 * @return
	 */
	int order();

	/**
	 * 基本数据类型；
	 * <p>
	 * 
	 * 如果字段的类型属于 {@link PrimitiveType} 枚举中的基本数据类型，则需要显式指定一种具体的类型；
	 * 
	 * @return
	 */
	PrimitiveType primitiveType() default PrimitiveType.NIL;

	/**
	 * 是否是枚举类型；
	 * <p>
	 * 如果为 true，则属性的声明类型必须是枚举类型，且该枚举类型已经标记 {@link EnumContract}；
	 * 
	 * @return
	 */
	boolean refEnum() default false;

	/**
	 * 嵌套的数据契约类型；
	 * 
	 * 如果为 true，则属性的声明类型必须是接口类型，且该类型已经标记了 {@link DataContract};
	 * 
	 * @return
	 */
	boolean refContract() default false;

	/**
	 * 嵌套的契约类型是否根据实际的对象实现的契约接口动态写入；
	 * 
	 * @return
	 */
	boolean genericContract() default false;

	/**
	 * 列表；
	 * 
	 * @return
	 */
	boolean list() default false;

	/**
	 * 最大长度，单位为“byte”
	 * <p>
	 * 仅对于文本、字节数组、大整数等相关的数据类型有效（即：{@link PrimitiveType} 枚举中编码大于等于 0x20
	 * {@link PrimitiveType#TEXT}的数据类型）；
	 * 
	 * @return
	 */
	int maxSize() default -1;

	/**
	 * 名称；
	 * <p>
	 * 默认为属性的名称；
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 关于字段的说明；
	 * <p>
	 * 
	 * 说明内容将输出到数据段的数据结构描述文件；
	 * 
	 * @return
	 */
	String decription() default "";

}
