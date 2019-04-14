package com.jd.blockchain.binaryproto.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import com.jd.blockchain.binaryproto.BinarySliceSpec;
import com.jd.blockchain.binaryproto.FieldSpec;
import com.jd.blockchain.utils.io.BytesInputStream;
import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesSlices;
import com.jd.blockchain.utils.io.FixedBytesSliceArray;
import com.jd.blockchain.utils.io.NumberMask;

public class FixedArrayFieldEncoder extends AbstractFieldEncoder {
	
	private FixedValueConverter valueConverter;

	public FixedArrayFieldEncoder(BinarySliceSpec sliceSpec, FieldSpec fieldSpec, Method reader, FixedValueConverter valueConverter) {
		super(sliceSpec, fieldSpec, reader);
		this.valueConverter = valueConverter;
	}

	@Override
	public int encode(Object dataContract, BytesOutputBuffer buffer) {
		int size = 0;

		Object[] values = readArrayValue(dataContract);
		size += encodeArray(values, buffer);

		return size;
	}

	/**
	 * 对数组类型的值进行固定长度的编码；
	 * 
	 * @param values
	 * @param buffer
	 * @return
	 */
	private int encodeArray(Object[] values, BytesOutputBuffer buffer) {
		int count = values == null ? 0 : values.length;

		int counterSize = NumberMask.NORMAL.getMaskLength(count);
		int elementSize = sliceSpec.getLength();

		int size = counterSize + elementSize * count;
		byte[] outbuff = new byte[size];
		NumberMask.NORMAL.writeMask(count, outbuff, 0);

		for (int i = 0; i < count; i++) {
			valueConverter.encodeValue(values[i], outbuff, counterSize + elementSize * i);
		}

		buffer.write(outbuff);
		return size;
	}

	@Override
	public BytesSlices decode(BytesInputStream bytesStream) {
		return FixedBytesSliceArray.resolve(bytesStream, sliceSpec.getLength());
	}

	@Override
	public Object decodeField(BytesSlices fieldBytes) {
		Object[] values = (Object[]) Array.newInstance(valueConverter.getValueType(), fieldBytes.getCount());
		BytesSlice itemSlice;
		for (int i = 0; i < values.length; i++) {
			itemSlice = fieldBytes.getDataSlice(i);
			if (itemSlice.getSize() == 0) {
				values[i] = valueConverter.getDefaultValue();
			} else {
				values[i] = valueConverter.decodeValue(itemSlice);
			}
		}
		return values;
	}

}
