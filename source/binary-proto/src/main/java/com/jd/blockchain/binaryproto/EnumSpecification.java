package com.jd.blockchain.binaryproto;

public interface EnumSpecification {
	
	int getCode();
	
	String getName();

	String getDescription();
	
	long getVersion();
	
	DataType getValueType();
	
	int[] getItemValues();
	
	String[] getItemNames();


}
