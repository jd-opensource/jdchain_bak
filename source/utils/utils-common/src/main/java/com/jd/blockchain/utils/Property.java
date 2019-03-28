package com.jd.blockchain.utils;

import java.io.ByteArrayInputStream;

import com.jd.blockchain.utils.io.BytesEncoding;
import com.jd.blockchain.utils.io.BytesSerializable;
import com.jd.blockchain.utils.io.BytesUtils;

public class Property implements BytesSerializable {

	private String name;

	private String value;

	public Property() {
	}

	public Property(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Property(byte[] nameValueBytes) {
		decode(nameValueBytes);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public byte[] toBytes() {
		byte[] nameBytes = BytesUtils.toBytes(name);
		byte[] valueBytes = value == null ? BytesUtils.EMPTY_BYTES : BytesUtils.toBytes(value);
		int totalSize = BytesEncoding.getOutputSizeInNormal(nameBytes.length)
				+ BytesEncoding.getOutputSizeInNormal(valueBytes.length);
		byte[] totalBytes = new byte[totalSize];
		int offset = 0;
		offset += BytesEncoding.writeInNormal(nameBytes, totalBytes, offset);
		offset += BytesEncoding.writeInNormal(valueBytes, totalBytes, offset);
		return totalBytes;
	}

	private void decode(byte[] nameValueBytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(nameValueBytes);
		byte[] nameBytes = BytesEncoding.readInNormal(in);
		byte[] valueBytes = BytesEncoding.readInNormal(in);
		this.name = BytesUtils.toString(nameBytes);
		this.value = valueBytes.length == 0 ? "" : BytesUtils.toString(valueBytes);
	}

}
