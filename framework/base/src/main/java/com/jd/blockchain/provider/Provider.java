package com.jd.blockchain.provider;

public interface Provider<S> {
	
	String getShortName();
	
	String getFullName();
	
	S getService();
	
}
