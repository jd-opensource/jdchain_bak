package com.jd.blockchain.ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.ledger.resolver.BooleanToBytesValueResolver;
import com.jd.blockchain.ledger.resolver.BytesToBytesValueResolver;
import com.jd.blockchain.ledger.resolver.BytesValueResolver;
import com.jd.blockchain.ledger.resolver.IntegerToBytesValueResolver;
import com.jd.blockchain.ledger.resolver.LongToBytesValueResolver;
import com.jd.blockchain.ledger.resolver.ShortToBytesValueResolver;
import com.jd.blockchain.ledger.resolver.StringToBytesValueResolver;

public class BytesValueEncoding {

	private static final Map<Class<?>, BytesValueResolver> CLASS_RESOLVER_MAP = new ConcurrentHashMap<>();

	private static final Map<DataType, BytesValueResolver> DATA_TYPE_RESOLVER_MAP = new ConcurrentHashMap<>();

	private static final Object[] EMPTY_OBJECTS = {};

	static {
		init();
	}

	private static void init() {
		BytesValueResolver[] resolvers = new BytesValueResolver[] { new BooleanToBytesValueResolver(),
				new BytesToBytesValueResolver(), new IntegerToBytesValueResolver(), new LongToBytesValueResolver(),
				new ShortToBytesValueResolver(), new StringToBytesValueResolver() };

		for (BytesValueResolver currResolver : resolvers) {
			// 填充classMAP
			Class<?>[] supportClasses = currResolver.supportClasses();
			if (supportClasses != null && supportClasses.length > 0) {
				for (Class<?> clazz : supportClasses) {
					CLASS_RESOLVER_MAP.put(clazz, currResolver);
				}
			}

			// 填充dataTypeMap
			DataType[] supportDataTypes = currResolver.supportDataTypes();
			if (supportDataTypes != null && supportDataTypes.length > 0) {
				for (DataType dataType : supportDataTypes) {
					DATA_TYPE_RESOLVER_MAP.put(dataType, currResolver);
				}
			}
		}
	}

	public static BytesValue encodeSingle(Object value, Class<?> type) {
		if (value == null) {
			return null;
		}
		if (type == null) {
			type = value.getClass();
		} else if (type.equals(void.class) || type.equals(Void.class)) {
			return null;
		}
		if (type.isInterface()) {
			// 判断是否含有DataContract注解
			if (!type.isAnnotationPresent(DataContract.class)) {
				throw new IllegalStateException(
						String.format("Interface[%s] can not be serialize !!!", type.getName()));
			}
			// 将对象序列化
			byte[] serialBytes = BinaryProtocol.encode(value, type);
			return TypedValue.fromType(DataType.DATA_CONTRACT, serialBytes);
		}
		BytesValueResolver bytesValueResolver = CLASS_RESOLVER_MAP.get(type);
		if (bytesValueResolver == null) {
			throw new IllegalStateException(String.format("Class[%s] can not find encoder !!!", type.getName()));
		}
		return bytesValueResolver.encode(value, type);
	}

	public static BytesValueList encodeArray(Object[] values, Class<?>[] types) {
		if (values == null || values.length == 0) {
			return null;
		}
		if (types != null && types.length != values.length) {
			throw new IllegalStateException("Types can be null, or types's length must be equal BytesValue[]'s !!!");
		}

		BytesValueListData bytesValueListData = new BytesValueListData();
		for (int i = 0; i < values.length; i++) {
			BytesValue bytesValue = encodeSingle(values[i], types == null ? null : types[i]);
			bytesValueListData.add(bytesValue);
		}
		return bytesValueListData;
	}

	public static Object decode(BytesValue value) {
		return decode(value, null);
	}

	public static Object decode(BytesValue value, Class<?> type) {
		DataType dataType = value.getType();
		BytesValueResolver valueResolver = DATA_TYPE_RESOLVER_MAP.get(dataType);
		if (valueResolver == null) {
			throw new IllegalStateException(String.format("DataType[%s] can not find encoder !!!", dataType.name()));
		}
		return type == null ? valueResolver.decode(value) : valueResolver.decode(value, type);
	}

	public static Object[] decode(BytesValueList values, Class<?>[] types) {
		if (values == null) {
			return EMPTY_OBJECTS;
		}
		BytesValue[] bytesValues = values.getValues();
		if (bytesValues == null || bytesValues.length == 0) {
			return EMPTY_OBJECTS;
		}
		// 允许types为null，此时每个BytesValue按照当前的对象来处理
		// 若types不为null，则types's长度必须和bytesValues一致
		if (types != null && types.length != bytesValues.length) {
			throw new IllegalStateException("Types can be null, or types's length must be equal BytesValue[]'s !!!");
		}
		Object[] resolveObjs = new Object[bytesValues.length];
		if (types == null) {
			// 按照默认方式解析
			for (int i = 0; i < bytesValues.length; i++) {
				BytesValue bytesValue = bytesValues[i];
				DataType dataType = bytesValue.getType();
				BytesValueResolver valueResolver = DATA_TYPE_RESOLVER_MAP.get(dataType);
				if (valueResolver == null) {
					throw new IllegalStateException(
							String.format("DataType[%s] can not find encoder !!!", dataType.name()));
				}
				resolveObjs[i] = valueResolver.decode(bytesValue);
			}
			return resolveObjs;
		}
		// 按照输入的Class进行解析
		for (int i = 0; i < bytesValues.length; i++) {
			resolveObjs[i] = decode(bytesValues[i], types[i]);
		}
		return resolveObjs;
	}

	public static Object getDefaultValue(Class<?> type) {
		if (type == void.class || type == Void.class) {
			return null;
		}

		if (!type.isPrimitive()) {
			// 非基本类型
			return null;
		} else {
			// 基本类型需要处理返回值，目前采用枚举遍历方式
			// 八种基本类型：int, double, float, long, short, boolean, byte, char， void
			if (type.equals(int.class)) {
				return 0;
			} else if (type.equals(double.class)) {
				return 0.0D;
			} else if (type.equals(float.class)) {
				return 0F;
			} else if (type.equals(long.class)) {
				return 0L;
			} else if (type.equals(short.class)) {
				return (short) 0;
			} else if (type.equals(boolean.class)) {
				return Boolean.FALSE;
			} else if (type.equals(byte.class)) {
				return (byte) 0;
			} else if (type.equals(char.class)) {
				return (char) 0;
			}
			return null;
		}
	}

	public static boolean supportType(Class<?> currParamType) {
		// 支持空返回值
		if (currParamType.equals(void.class) || currParamType.equals(Void.class)) {
			return true;
		}

		if (currParamType.isInterface()) {
			// 接口序列化必须实现DataContract注解
			if (!currParamType.isAnnotationPresent(DataContract.class)) {
				throw new IllegalStateException(
						String.format("Interface[%s] can not be annotated as a DataContract!!!", currParamType.getName()));
			}
			return true;
		}
		
		if (currParamType.isArray() ) {
			Class<?> componentType = currParamType.getComponentType();
			if (componentType.isInterface()) {
				// 接口序列化必须实现DataContract注解
				if (!componentType.isAnnotationPresent(DataContract.class)) {
					throw new IllegalStateException(
							String.format("Interface[%s] can not be annotated as a DataContract!!!", currParamType.getName()));
				}
				return true;
			}
		}
		
		return CLASS_RESOLVER_MAP.containsKey(currParamType);
	}

	public static class BytesValueListData implements BytesValueList {

		private List<BytesValue> bytesValues = new ArrayList<>();

		public void add(BytesValue bytesValue) {
			bytesValues.add(bytesValue);
		}

		@Override
		public BytesValue[] getValues() {
			BytesValue[] bytesValueArray = new BytesValue[bytesValues.size()];
			return bytesValues.toArray(bytesValueArray);
		}
	}
}
