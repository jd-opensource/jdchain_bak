package com.jd.blockchain.utils;

import org.springframework.util.StringUtils;

public abstract class PathUtils {

	public static final String PATH_SEPERATOR = "/";
	public static final char PATH_SEPERATOR_CHAR = '/';

	public static final String WINDOWS_PATH_SEPERATOR = "\\";

	public static final String SCHEMA_SEPERATOR = ":";

	/**
	 * 标准化指定的路径；
	 * 
	 * 标准化的过程包括： 
	 * 1、清理字符串中的 ".";
	 * 2、清除字符串中的 ".."以及路径中相应的上一级路径；(例如：ab/../cd 处理后变为 cd ) 
	 * 2、将 windows 的分隔符"\"替换为标准分隔符"/"；
	 * 3、将连续重复的分隔符替换为单个分隔符；
	 * 4、去除结尾的分隔符； 
	 * 5、去除其中的空白字符；
	 * 
	 * 注：以冒号":"分隔的 schema 头将被保留；
	 * 
	 * @param path path
	 * @return String
	 */
	public static String standardize(String path) {
		path = StringUtils.trimAllWhitespace(path);
		path = StringUtils.cleanPath(path);
		path = cleanRepeatlySeperator(path);
		if (path.endsWith(PATH_SEPERATOR)) {
			return path.substring(0, path.length() - 1);
		}
		return path;
	}

	/**
	 * 将指定的路径转换为绝对路径；
	 * 
	 * 方法将检测指定的路径如果既没有以路径分隔符"/"开头，也没有冒号":"分隔的 schema 开头(例如 file:)，
	 * 
	 * 则在开头加上路径分隔符"/"返回；
	 * 
	 * 注：方法不会检测路径是否标准，也不会自动将其标准化；
	 * 
	 * @param path path
	 * @return String
	 */
	public static String absolute(String path) {
		if (path.startsWith(PATH_SEPERATOR)) {
			return path;
		}
		if (path.indexOf(SCHEMA_SEPERATOR) >= 0) {
			return path;
		}
		return PATH_SEPERATOR + path;
	}
	
	/**
	 * 清除路径中的重复分隔符；
	 * 
	 * @param path path
	 * @return String
	 */
	public static String cleanRepeatlySeperator(String path) {
		// 去除重复的分隔符；
		String schema = "";
		String pathToProcess = path;
		int index = path.indexOf("://");
		if (index >= 0) {
			schema = path.substring(0, index + 3);
			for (index = index + 3; index < path.length(); index++) {
				if (path.charAt(index) != PATH_SEPERATOR_CHAR) {
					break;
				}
			}
			pathToProcess = path.substring(index);
		}
		StringBuilder pathToUse = new StringBuilder();
		boolean hit = false;
		char ch;
		for (int i = 0; i < pathToProcess.length(); i++) {
			ch = pathToProcess.charAt(i);
			if (ch == PATH_SEPERATOR_CHAR) {
				if (hit) {
					continue;
				} else {
					hit = true;
				}
			} else {
				hit = false;
			}
			pathToUse.append(ch);
		}
		return schema + pathToUse;
	}

	/**
	 * concatPaths
	 * @param paths path
	 * @return String
	 */
	public static String concatPaths(String... paths){
		if (paths == null || paths.length == 0) {
			return "";
		}
		StringBuilder path = new StringBuilder();
		for (String p : paths) {
			path.append(p);
			path.append(PATH_SEPERATOR_CHAR);
		}
		return standardize(path.toString());
	}
}
