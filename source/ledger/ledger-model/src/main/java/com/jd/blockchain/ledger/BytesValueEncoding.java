package com.jd.blockchain.ledger;

public class BytesValueEncoding {
	
	
	
	
	public static BytesValue encode(Object value, Class<?> type) {
		throw new IllegalStateException("Not implemented!");
	}
	
	public static BytesValueList encode(Object[] values, Class<?>[] types) {
		throw new IllegalStateException("Not implemented!");
	}
	
	public static Object decode(BytesValue value, Class<?> type) {
		throw new IllegalStateException("Not implemented!");
	}
	
	public static Object[] decode(BytesValueList values, Class<?>[] types) {
		throw new IllegalStateException("Not implemented!");
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
		// TODO Auto-generated method stub
		return false;
	}
}
