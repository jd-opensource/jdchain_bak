package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.binaryproto.DataContractEncoder;
import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSlice;

public class DataContractValueConverter extends AbstractDynamicValueConverter {
	
	private DataContractEncoder contractEncoder;
	
	public DataContractValueConverter(DataContractEncoder contractEncoder) {
		super(contractEncoder.getContractType());
		this.contractEncoder =contractEncoder;
	}

	@Override
	public int encodeDynamicValue(Object value, BytesOutputBuffer buffer) {
		BytesOutputBuffer contractBuffer = new BytesOutputBuffer();
		int size = contractEncoder.encode(value, contractBuffer);
		
		size += writeSize(size, buffer);
		
		buffer.write(contractBuffer);
		return size;
	}

	@Override
	public Object decodeValue(BytesSlice dataSlice) {
		return contractEncoder.decode(dataSlice.getInputStream());
	}
	
}
