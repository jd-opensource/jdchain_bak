package com.jd.blockchain.binaryproto.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jd.blockchain.binaryproto.BinarySliceSpec;
import com.jd.blockchain.binaryproto.DataSpecification;
import com.jd.blockchain.binaryproto.FieldSpec;

public class DataContractSpecification implements DataSpecification {

	private int code;
	private long version;
	private String name;
	private String description;

	private List<FieldSpec> fieldList;
	private List<BinarySliceSpec> sliceList;

	public DataContractSpecification(int code, long version, String name, String description, BinarySliceSpec[] slices, FieldSpec[] fields) {
		this.code = code;
		this.version = version;
		this.name = name;
		this.description = description;
		this.fieldList = Collections.unmodifiableList(Arrays.asList(fields));
		this.sliceList = Collections.unmodifiableList(Arrays.asList(slices));
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public long getVersion() {
		return version;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<FieldSpec> getFields() {
		return fieldList;
	}

	@Override
	public List<BinarySliceSpec> getSlices() {
		return sliceList;
	}

	@Override
	public String toHtml() {
		throw new IllegalStateException("Not implemented!");
	}
}
