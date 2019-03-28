package com.jd.blockchain.binaryproto;

import java.util.Set;

import com.jd.blockchain.utils.ValueType;

public interface EnumSpecification {
	
	int getCode();
	
	String getName();

	String getDescription();
	
	long getVersion();
	
	ValueType getValueType();
	
	int[] getItemValues();
	
	String[] getItemNames();


}
