package com.jd.blockchain.tools.initializer;

public interface Prompter {
	
	void info(String format, Object... args);

	void error(String format, Object... args);
	
	void error(Exception error, String format, Object... args);
	
	String confirm(String format, Object... args);
	
	String confirm(String tag, String format, Object... args);
	
}
