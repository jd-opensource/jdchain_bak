package com.jd.blockchain.ledger.resolver;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface BytesValueResolver {

	/**
	 * Boolean相关的可转换Class集合
	 */
	Class<?>[] supportBooleanConvertClasses = { boolean.class, Boolean.class };

	/**
	 * Int相关的可转换Class集合
	 */
	Class<?>[] supportIntConvertClasses = { short.class, Short.class, int.class, Integer.class, long.class,
			Long.class };

	/**
	 * 字节数组（字符串）相关可转换的Class集合
	 */
	Class<?>[] supportByteConvertClasses = { String.class, Bytes.class, byte[].class };
	
	default Set<Class<?>> initBooleanConvertSet() {
		return new HashSet<>(Arrays.asList(supportBooleanConvertClasses));
	}

	default Set<Class<?>> initIntConvertSet() {
		return new HashSet<>(Arrays.asList(supportIntConvertClasses));
	}

	default Set<Class<?>> initByteConvertSet() {
		return new HashSet<>(Arrays.asList(supportByteConvertClasses));
	}

	/**
	 * 将对象转换为BytesValue
	 *
	 * @param value
	 * @return
	 */
	BytesValue encode(Object value);

	/**
	 * 将对象转换为BytesValue
	 *
	 * @param value
	 * @param type
	 * @return
	 */
	BytesValue encode(Object value, Class<?> type);

	/**
	 * 当前解析器支持的Class列表
	 *
	 * @return
	 */
	Class<?>[] supportClasses();

	/**
	 * 当前解析器支持的DataType列表
	 *
	 * @return
	 */
	DataType[] supportDataTypes();

	/**
	 * 将BytesValue解析为对应的Object
	 *
	 * @param value
	 * @return
	 */
	Object decode(BytesValue value);

	/**
	 * 将BytesValue转换为指定Class的Object
	 *
	 * @param value
	 * @param clazz
	 * @return
	 */
	Object decode(BytesValue value, Class<?> clazz);
}
