package com.jd.blockchain.utils;

import java.io.Console;

import com.jd.blockchain.utils.io.ByteArray;

public class ConsoleUtils {

	public static byte[] readPassword() {
		Console cs = getConsole();
		char[] pwdChars;
		do {
			pwdChars = cs.readPassword("\r\nInput password:");
		} while (pwdChars.length == 0);
		String pwd = new String(pwdChars);
		return ByteArray.fromString(pwd, "UTF-8");
	}

	public static void info(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	public static void error(String format, Object... args) {
		System.err.println(String.format(format, args));
	}

	public static String confirm(String fmt, Object...args) {
		Console cs = getConsole();
		return cs.readLine(fmt, args);
	}

	public static Console getConsole() {
		Console cs = System.console();
		if (cs == null) {
			throw new IllegalStateException("You are not running in console!");
		}
		return cs;
	}

}
