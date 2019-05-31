package com.jd.blockchain.binaryproto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link DataContract} 表示数据契约，用于把一个接口类型声明为一份标准化“数据契约”；
 * <p>
 * “数据契约”的定义由类型编码（{@link #code()}）和属性列表（由标注
 * {@link DataField}定义）构成，其中属性列表中有唯一一个“主键字段”， 主键字段的值用于标识数据契约实例的唯一性；
 * <p>
 * “数据契约”通过属性可以引用其它的“数据契约”（由 {@link DataField#refContract()} = true
 * 定义），这样便构成了“数据契约”嵌套定义的关系图；
 * <p>
 * 当对一个“数据契约”进行二进制序列化输出时，从根对象出发，对关系图中的每一个“数据契约”实例都输出为一个单独的二进制数据段{@link BinarySegmentHeader}，
 * 
 * 父的数据段中会将子数据段的内容合并输出到对应字段的位置；
 * 
 * <p>
 * 在序列化输出数据段时，将按顺序先后输出类型编号({@link #code()})、版本标识、属性值列表；<p>
 * 其中，“版本标识”是根据类型编号和属性列表（由 {@link DataField} 顺序和类型决定，与名称无关) 进行 SHA256 哈希后映射到 64
 * 位值空间的值，占用 8 字节空间；
 * “版本标识” 用于在反序列化时校验数据格式的版本是否匹配，并允许数据契约升级后多版本数据并存；
 * 
 * @author huanghaiquan
 *
 */
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataContract {

	/**
	 * 类型编号；
	 * <p>
	 * 不同类型不能声明相同的编号；
	 * <p>
	 * 
	 * @return
	 */
	int code();

	String name() default "";

	String description() default "";

}
