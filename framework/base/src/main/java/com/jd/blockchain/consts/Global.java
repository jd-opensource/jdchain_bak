package com.jd.blockchain.consts;

import java.util.TimeZone;

public class Global {
	
	public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
	
	public static final String DEFAULT_TIME_ZONE = "GMT+08:00";
	
	static {
		initialize();
	}
	
	public static void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
	}
	
}
