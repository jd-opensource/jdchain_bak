package com.jd.blockchain.binaryproto.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.binaryproto.EnumSpecification;

/**
 * Created by zhangshuang3 on 2018/6/21.
 */
public class EnumSpecificationInfo implements EnumSpecification {

	private PrimitiveType valueType;

	private Class<?> dataType;

	private int code;
	private long version;
	private String name;
	private String description;

	private Set<EnumConstant> items = new LinkedHashSet<>();
	// private Map<Object, Object> itemCodeMapping = new HashMap<>();
	// private Map<Object, Object> codeItemMapping = new HashMap<>();

	public EnumSpecificationInfo(PrimitiveType valueType, int code, long version, String name, String description, Class<?> dataType) {
		this.valueType = valueType;
		this.code = code;
		this.version = version;
		this.name = name;
		this.description = description;
		this.dataType = dataType;
	}

	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public long getVersion() {
		return this.version;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public PrimitiveType getValueType() {
		return this.valueType;
	}

	@Override
	public int[] getItemValues() {
		int[] values = new int[items.size()];
		int i = 0;
		for (EnumConstant it : items) {
			values[i] = it.code;
			i++;
		}
		return values;
	}

	@Override
	public String[] getItemNames() {
		String[] names = new String[items.size()];
		int i = 0;
		for (EnumConstant it : items) {
			names[i] = it.name;
			i++;
		}
		return names;
	}
	
	public Class<?> getDataType() {
		return dataType;
	}

	public Object[] getConstants() {
		Object[] constants = new Object[items.size()];
		int i = 0;
		for (EnumConstant it : items) {
			constants[i] = it.constant;
			i++;
		}
		return constants;
	}

	public void addConstant(EnumConstant item) {
		items.add(item);
	}

	public static class EnumConstant {

		private int code;

		private String name;

		private Object constant;

		public int getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public Object getConstant() {
			return constant;
		}

		public EnumConstant(int code, String name, Object constant) {
			this.code = code;
			this.name = name;
			this.constant = constant;
		}
	}
}
