package com.jd.blockchain.binaryproto;

public interface EnumSpecification {
	
	int getCode();
	
	String getName();

	String getDescription();
	
	long getVersion();
	
	PrimitiveType getValueType();
	
	int[] getItemValues();
	
	String[] getItemNames();


}
