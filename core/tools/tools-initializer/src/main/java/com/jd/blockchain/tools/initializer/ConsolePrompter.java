package com.jd.blockchain.tools.initializer;

import com.jd.blockchain.utils.ConsoleUtils;

public class ConsolePrompter implements Prompter {

	private boolean debug = true;

	@Override
	public void info(String format, Object... args) {
		ConsoleUtils.info(format, args);
	}

	@Override
	public void error(String format, Object... args) {
		ConsoleUtils.error(format, args);
	}

	@Override
	public void error(Exception error, String format, Object... args) {
		if (debug) {
			error.printStackTrace();
		}
	}

	@Override
	public String confirm(String format, Object... args) {
		return confirm("", format, args);
	}

	@Override
	public String confirm(String tag, String format, Object... args) {
		String msg = String.format(format, args);
		return ConsoleUtils.confirm("[%s] %s", tag, msg);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}