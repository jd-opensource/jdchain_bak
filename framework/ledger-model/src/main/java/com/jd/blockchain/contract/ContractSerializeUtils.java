//package com.jd.blockchain.contract;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.jd.blockchain.binaryproto.BinaryProtocol;
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataContractRegistry;
//import com.jd.blockchain.contract.param.WRAP_BYTES;
//import com.jd.blockchain.contract.param.WRAP_INT;
//import com.jd.blockchain.contract.param.WRAP_LONG;
//import com.jd.blockchain.contract.param.WRAP_SHORT;
//import com.jd.blockchain.contract.param.WRAP_STRING;
//import com.jd.blockchain.ledger.BytesValue;
//import com.jd.blockchain.ledger.BytesValueList;
//import com.jd.blockchain.utils.io.BytesUtils;
//
//public class ContractSerializeUtils {
//
//	final static int INT_LENGTH = 4;
//
//	static Map<Class<?>, Class<?>> MAP = new HashMap<>();
//
//	static {
//		MAP.put(byte[].class, WRAP_BYTES.class);
//		MAP.put(Short.class, WRAP_SHORT.class);
//		MAP.put(short.class, WRAP_SHORT.class);
//		MAP.put(Integer.class, WRAP_INT.class);
//		MAP.put(int.class, WRAP_INT.class);
//		MAP.put(Long.class, WRAP_LONG.class);
//		MAP.put(long.class, WRAP_LONG.class);
//		MAP.put(String.class, WRAP_STRING.class);
//
//		DataContractRegistry.register(WRAP_BYTES.class);
//		DataContractRegistry.register(WRAP_SHORT.class);
//		DataContractRegistry.register(WRAP_INT.class);
//		DataContractRegistry.register(WRAP_LONG.class);
//		DataContractRegistry.register(WRAP_STRING.class);
//	}
//
//	public static boolean support(Class<?> clazz) {
//		if (clazz.isAnnotationPresent(DataContract.class) || MAP.containsKey(clazz)) {
//			return true;
//		}
//		return false;
//	}
//
//	public static BytesValue serialize(Object data) {
//
//		if (data == null) {
//			return null;
//		}
//
//		Class<?> clazz = data.getClass();
//		Class<?> serialClass;
//		Object wrapData = data;
//		if (clazz.isAnnotationPresent(DataContract.class)) {
//			serialClass = clazz;
//		} else {
//			// 判断类型是否可以序列化
//			Class<?> wrapClass = MAP.get(clazz);
//			if (wrapClass == null) {
//				throw new IllegalArgumentException("There are Un-Support Type !!!");
//			}
//			serialClass = wrapClass;
//
//			if (wrapClass.equals(WRAP_BYTES.class)) {
//				wrapData = (WRAP_BYTES) () -> (byte[]) data;
//			} else if (wrapClass.equals(WRAP_INT.class)) {
//				wrapData = (WRAP_INT) () -> (int) data;
//			} else if (wrapClass.equals(WRAP_LONG.class)) {
//				wrapData = (WRAP_LONG) () -> (long) data;
//			} else if (wrapClass.equals(WRAP_SHORT.class)) {
//				wrapData = (WRAP_SHORT) () -> (short) data;
//			} else if (wrapClass.equals(WRAP_STRING.class)) {
//				wrapData = (WRAP_STRING) () -> (String) data;
//			}
//		}
//		// 按照对应接口进行序列化
//		// 生成该接口对应的对象
//		return BinaryProtocol.encode(wrapData, serialClass);
//	}
//
//	public static BytesValueList serializeArray(Object[] datas) {
//		if (datas == null || datas.length == 0) {
//			return null;
//		}
//		int contentBytesSize = 0;
//		byte[] header = new byte[(datas.length + 1) * 4];
//		System.arraycopy(BytesUtils.toBytes(datas.length), 0, header, 0, INT_LENGTH);
//		int offset = INT_LENGTH;
//
//		List<byte[]> serialBytesList = new ArrayList<>();
//		for (Object data : datas) {
//			// 按照对应接口进行序列化
//			byte[] currBytes = serialize(data);
//			// 长度写入
//			System.arraycopy(BytesUtils.toBytes(currBytes.length), 0, header, offset, INT_LENGTH);
//			serialBytesList.add(currBytes);
//			contentBytesSize += currBytes.length;
//			offset += INT_LENGTH;
//		}
//
//		// 填充content
//		byte[] content = new byte[contentBytesSize];
//		offset = 0;
//		for (byte[] bytes : serialBytesList) {
//			System.arraycopy(bytes, 0, content, offset, bytes.length);
//			offset += bytes.length;
//		}
//		// 将header和content组装
//		return BytesUtils.concat(header, content);
//	}
//
//	public static Object[] resolveArray(byte[] bytes) {
//		if (bytes == null || bytes.length == 0) {
//			return null;
//		}
//		byte[] lengthBytes = new byte[INT_LENGTH];
//		System.arraycopy(bytes, 0, lengthBytes, 0, INT_LENGTH);
//		int length = BytesUtils.toInt(lengthBytes);
//		Object[] datas = new Object[length];
//
//		int headerOffset = INT_LENGTH;
//		int contentStart = (length + 1) * INT_LENGTH;
//
//		for (int i = 0; i < length; i++) {
//			byte[] currDataLengthBytes = new byte[INT_LENGTH];
//			System.arraycopy(bytes, headerOffset, currDataLengthBytes, 0, INT_LENGTH);
//			int currDataLength = BytesUtils.toInt(currDataLengthBytes);
//			// 读取
//			byte[] dataBytes = new byte[currDataLength];
//			System.arraycopy(bytes, contentStart, dataBytes, 0, currDataLength);
//
//			datas[i] = resolve(dataBytes);
//
//			contentStart += currDataLength;
//			headerOffset += INT_LENGTH;
//		}
//
//		return datas;
//	}
//
//	public static Object resolve(BytesValue bytes) {
//		// 反序列化该接口
//		Object currData = BinaryProtocol.decode(bytes);
//
//		// 代理对象类型不能使用class判断，只能使用instanceof
//		if (currData instanceof WRAP_STRING) {
//			return ((WRAP_STRING) currData).getValue();
//		} else if (currData instanceof WRAP_INT) {
//			return ((WRAP_INT) currData).getValue();
//		} else if (currData instanceof WRAP_LONG) {
//			return ((WRAP_LONG) currData).getValue();
//		} else if (currData instanceof WRAP_BYTES) {
//			return ((WRAP_BYTES) currData).getValue();
//		} else if (currData instanceof WRAP_SHORT) {
//			return ((WRAP_SHORT) currData).getValue();
//		} else {
//			return currData;
//		}
//	}
//}
