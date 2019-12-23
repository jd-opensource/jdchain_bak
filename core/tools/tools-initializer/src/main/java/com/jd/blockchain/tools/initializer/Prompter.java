package com.jd.blockchain.tools.initializer;

public interface Prompter {
	
	//定义一些常用的问答常量；

	/**
	 * 询问是或否；
	 */
	public static final String QUESTION_YESNO = "YES/NO";

	/**
	 * 提示按任意键继续；
	 */
	public static final String PROMPT_ANYKEY_TO_CONTINUE = "ANYKEY";

	/**
	 * 
	 */
	public static final String ANSWER_YES = "Y";

	public static final String ANSWER_NO = "N";
	
	
	
	
	void info(String format, Object... args);

	void error(String format, Object... args);
	
	void error(Exception error, String format, Object... args);
	
	String confirm(String format, Object... args);
	
	String confirm(String tag, String format, Object... args);
	
}
