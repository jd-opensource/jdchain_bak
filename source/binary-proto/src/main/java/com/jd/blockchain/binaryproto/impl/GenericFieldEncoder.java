package com.jd.blockchain.binaryproto.impl;
//package com.jd.blockchain.binaryproto.impl2;
//
//import java.lang.reflect.Array;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
//import com.jd.blockchain.binaryproto.BinarySliceSpec;
//import com.jd.blockchain.binaryproto.FieldSpec;
//
//import my.utils.io.BytesInputStream;
//import my.utils.io.BytesOutputBuffer;
//import my.utils.io.BytesSlice;
//import my.utils.io.BytesSlices;
//import my.utils.io.DynamicBytesSliceArray;
//import my.utils.io.FixedBytesSliceArray;
//import my.utils.io.NumberMask;
//import my.utils.io.SingleBytesSliceArray;
//
//public abstract class GenericFieldEncoder implements FieldEncoder {
//
//	protected BinarySliceSpec sliceSpec;
//
//	protected FieldSpec fieldSpec;
//
//	protected Method reader;
//
//	public GenericFieldEncoder(BinarySliceSpec sliceSpec, FieldSpec fieldSpec, Method reader) {
//		this.sliceSpec = sliceSpec;
//		this.fieldSpec = fieldSpec;
//		this.reader = reader;
//	}
//
//	@Override
//	public BinarySliceSpec getSliceSpecification() {
//		return sliceSpec;
//	}
//
//	@Override
//	public FieldSpec getFieldSpecification() {
//		return fieldSpec;
//	}
//
//	@Override
//	public Method getReader() {
//		return reader;
//	}
//
//	@Override
//	public int encode(Object dataContract, BytesOutputBuffer buffer) {
//		int size = 0;
//		if (sliceSpec.isRepeatable()) {
//			Object[] values = readArrayValue(dataContract);
//			if (sliceSpec.isDynamic()) {
//				size += encodeArrayDynamic(values, buffer);
//			} else {
//				size += encodeArray(values, buffer);
//			}
//		} else {
//			Object value = readValue(dataContract);
//			if (sliceSpec.isDynamic()) {
//				size += encodeDynamicValue(value, buffer);
//			} else {
//				size += encodeValue(value, buffer);
//			}
//		}
//
//		return size;
//	}
//
//	/**
//	 * 把固定长度的值序列化到指定的缓冲区；
//	 * 
//	 * @param value
//	 * @param buffer
//	 * @return
//	 */
//	private int encodeValue(Object value, BytesOutputBuffer buffer) {
//		byte[] valueBytes = new byte[sliceSpec.getLength()];
//		return encodeValue(value, valueBytes, 0);
//	}
//
//	/**
//	 * 将把固定长度的值序列化到指定的缓冲区；
//	 * <p>
//	 * 
//	 * 注：实现者应用确保写入的范围不要越界（超出 {@link #getSliceSpecification()} 属性指定的长度）；
//	 * 
//	 * @param value
//	 *            要序列化的值；
//	 * @param buffer
//	 *            保存结果的缓冲区；
//	 * @param offset
//	 *            缓冲区的写入起始位置；
//	 * @return 返回写入的长度；
//	 */
//	abstract int encodeValue(Object value, byte[] buffer, int offset);
//
//	abstract int encodeDynamicValue(Object value, BytesOutputBuffer buffer);
//
//	/**
//	 * 对数组类型的值进行固定长度的编码；
//	 * 
//	 * @param values
//	 * @param buffer
//	 * @return
//	 */
//	private int encodeArray(Object[] values, BytesOutputBuffer buffer) {
//		int count = values == null ? 0 : values.length;
//
//		int counterSize = NumberMask.NORMAL.getMaskLength(count);
//		int elementSize = sliceSpec.getLength();
//
//		int size = counterSize + elementSize * count;
//		byte[] outbuff = new byte[size];
//		NumberMask.NORMAL.writeMask(count, outbuff, 0);
//
//		for (int i = 0; i < count; i++) {
//			encodeValue(values[i], outbuff, counterSize + elementSize * i);
//		}
//
//		buffer.write(outbuff);
//		return size;
//	}
//
//	/**
//	 * 对数组类型的值进行非固定长度的编码；
//	 * 
//	 * @param values
//	 * @param buffer
//	 * @return
//	 */
//	private int encodeArrayDynamic(Object[] values, BytesOutputBuffer buffer) {
//		int size = 0;
//
//		int count = values == null ? 0 : values.length;
//		byte[] countBytes = NumberMask.NORMAL.generateMask(count);
//		buffer.write(countBytes);
//		size += countBytes.length;
//
//		for (int i = 0; i < count; i++) {
//			size += encodeDynamicValue(values[i], buffer);
//		}
//
//		return size;
//	}
//
//	private Object readValue(Object dataContract) {
//		try {
//			return reader.invoke(dataContract);
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		}
//	}
//
//	private Object[] readArrayValue(Object dataContract) {
//		return (Object[]) readValue(dataContract);
//	}
//
//	@Override
//	public BytesSlices decode(BytesInputStream bytesStream) {
//		if (sliceSpec.isRepeatable()) {
//			if (sliceSpec.isDynamic()) {
//				return DynamicBytesSliceArray.resolve(bytesStream);
//			}else {
//				return FixedBytesSliceArray.resolve(bytesStream, sliceSpec.getLength());		
//			}
//		}else {
//			if (sliceSpec.isDynamic()) {
//				return SingleBytesSliceArray.resolveDynamic(bytesStream);
//			}else {
//				return SingleBytesSliceArray.create(bytesStream, sliceSpec.getLength());
//			}
//		}
//	}
//
//	@Override
//	public Object decodeField(BytesSlices fieldBytes) {
//		if (sliceSpec.isRepeatable()) {
//			Object[] values = (Object[]) Array.newInstance(getFieldType(), fieldBytes.getCount());
//			for (int i = 0; i < values.length; i++) {
//				values[i] = decodeValue(fieldBytes.getDataSlice(i));
//			}
//			return values;
//		}
//		//非数组的字段，最多只有一个数据片段；
//		if (fieldBytes.getCount() == 0) {
//			return getNullValue();
//		}
//		return decodeValue(fieldBytes.getDataSlice(0));
//	}
//	
//	
//	abstract Class<?> getFieldType();
//	
//	abstract Object getNullValue();
//	
//	abstract Object decodeValue(BytesSlice dataSlice);
//	
//}
