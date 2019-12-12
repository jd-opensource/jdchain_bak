package com.jd.blockchain.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * @Author zhaogw date 2018/11/26 20:46
 */
public class StringUtils {

	public static final String[] EMPTY_ARRAY = {};

	public static boolean isEmpty(Object str) {
		return str == null || "".equals(str);
	}

	/*
	 * 判断是否为整数
	 * 
	 * @param str 传入的字符串
	 * 
	 * @return 是整数返回true,否则返回false
	 */

	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 按照指定的分隔符把字符串分解为字符数组，同时截掉每一个元素两端的空白字符，并忽略掉空字符元素；
	 * 
	 * @param str       要被截断的字符串；
	 * @param delimiter 分隔符；
	 * @return
	 */
	public static String[] splitToArray(String str, String delimiter) {
		return splitToArray(str, delimiter, true, true);
	}

	/**
	 * 按照指定的分隔符把字符串分解为字符数组
	 * 
	 * @param str                要被截断的字符串；
	 * @param delimiter          分隔符；
	 * @param trimElement        是否截断元素两端的空白字符；
	 * @param ignoreEmptyElement 是否忽略空字符元素；
	 * @return
	 */
	public static String[] splitToArray(String str, String delimiter, boolean trimElement, boolean ignoreEmptyElement) {
		if (str == null) {
			return EMPTY_ARRAY;
		}
		if (trimElement) {
			str = str.trim();
		}
		if (str.length() == 0) {
			return EMPTY_ARRAY;
		}
		StringTokenizer tokenizer = new StringTokenizer(str, delimiter);
		List<String> tokens = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (trimElement) {
				token = token.trim();
			}
			if ((!ignoreEmptyElement) || token.length() > 0) {
				tokens.add(token);
			}
		}
		return tokens.toArray(new String[tokens.size()]);
	}

	public static String trim(String str) {
		return str == null ? "" : str.trim();
	}
}