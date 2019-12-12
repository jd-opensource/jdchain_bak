package com.jd.blockchain.tools.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPrompter implements Prompter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogPrompter.class);

	private static final String ANSWER_DEFAULT = "Yes";

	private boolean debug = true;

	@Override
	public void info(String format, Object... args) {
		LOGGER.info(format, args);
	}

	@Override
	public void error(String format, Object... args) {
		LOGGER.error(format, args);
	}

	@Override
	public void error(Exception error, String format, Object... args) {
		if (debug) {
			error.printStackTrace();
			LOGGER.error(error.toString());
		}
	}

	@Override
	public String confirm(String format, Object... args) {
		return confirm("", format, args);
	}

	@Override
	public String confirm(String tag, String format, Object... args) {
		String msg = String.format(format, args);
		LOGGER.info(msg);
		return ANSWER_DEFAULT;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}