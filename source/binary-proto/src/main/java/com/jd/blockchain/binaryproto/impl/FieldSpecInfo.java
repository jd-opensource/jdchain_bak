package com.jd.blockchain.binaryproto.impl;

import com.jd.blockchain.binaryproto.DataSpecification;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.binaryproto.EnumSpecification;
import com.jd.blockchain.binaryproto.FieldSpec;

public class FieldSpecInfo implements FieldSpec {

	private int order;

	private String name;

	private String description;

	private boolean repeatable;

	private PrimitiveType primitiveType;

	private EnumSpecification enumSpec;

	private DataSpecification contractTypeSpec;

	private int maxSize;

	private Class<?> dataType;

	private boolean isGenericContract = false;

	public FieldSpecInfo(int order, String name, String decription, PrimitiveType primitiveType, boolean repeatable,
			int maxSize, Class<?> dataType) {
		if (primitiveType == null) {
			throw new IllegalArgumentException("primitiveType is null!");
		}
		this.order = order;
		this.name = name;
		this.description = decription;
		this.primitiveType = primitiveType;
		this.repeatable = repeatable;
		this.maxSize = maxSize;
		this.dataType = dataType;
	}

	public FieldSpecInfo(int order, String name, String decription, EnumSpecification enumSpec, boolean repeatable,
			Class<?> enumType) {
		if (enumSpec == null) {
			throw new IllegalArgumentException("enum specification is null!");
		}
		this.order = order;
		this.name = name;
		this.description = decription;
		this.enumSpec = enumSpec;
		this.repeatable = repeatable;
		this.maxSize = -1;
		this.dataType = enumType;
	}

	public FieldSpecInfo(int order, String name, String decription, DataSpecification contractTypeSpec,
			boolean repeatable, Class<?> contractType, boolean isGenericContract) {
		if (contractTypeSpec == null) {
			throw new IllegalArgumentException("contractType is null!");
		}
		this.order = order;
		this.name = name;
		this.description = decription;
		this.contractTypeSpec = contractTypeSpec;
		this.repeatable = repeatable;
		this.maxSize = -1;
		this.dataType = contractType;
		this.isGenericContract = isGenericContract;
	}

	@Override
	public PrimitiveType getPrimitiveType() {
		return primitiveType;
	}

	@Override
	public EnumSpecification getRefEnum() {
		return enumSpec;
	}

	@Override
	public DataSpecification getRefContract() {
		return contractTypeSpec;
	}

	@Override
	public boolean isRepeatable() {
		return repeatable;
	}

	@Override
	public int getMaxSize() {
		return maxSize;
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
	public boolean isGenericContract() {
		return isGenericContract;
	}

	public int getOrder() {
		return order;
	}

	public Class<?> getDataType() {
		return dataType;
	}

}
