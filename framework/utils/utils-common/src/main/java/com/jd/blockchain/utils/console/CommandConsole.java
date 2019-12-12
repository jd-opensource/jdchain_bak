package com.jd.blockchain.utils.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class CommandConsole {

	private Map<String, CommondProcessor> processors = new ConcurrentHashMap<String, CommondProcessor>();

	private CommondProcessor defaultProcessor;

	private boolean monitoring = false;

	private BufferedReader in;

	private PrintStream out;

	private String prompt;

	public PrintStream out() {
		return out;
	}

	/**
	 * @param input
	 *            命令输入流；
	 * @param output
	 *            结果输出流；
	 * @param prompt
	 *            提示符；
	 */
	public CommandConsole(InputStream input, PrintStream output, String prompt) {
		try {
			this.in = new BufferedReader(new InputStreamReader(input, "GBK"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		this.out = output;
		this.prompt = prompt;

		// 定义默认的命令处理器；
		defaultProcessor = new CommondProcessor() {
			@Override
			public void onEnter(String command, String[] args, CommandConsole console) {
				console.out().println("Unsupported command [" + command + "]!");
			}
		};
	}

	public void setDefaultCommandProcessor(CommondProcessor processor) {
		if (processor == null) {
			throw new IllegalArgumentException("Null argument!");
		}
		this.defaultProcessor = processor;
	}

	/**
	 * 注册命令处理器；
	 * 
	 * 一个命令只能有一个处理器，重复注册多个处理器将引发异常；
	 * 
	 * @param command 命令文本； 自动忽略大小写；
	 * @param processor processor
	 */
	public synchronized void register(String command, CommondProcessor processor) {
		if (StringUtils.containsWhitespace(command)) {
			throw new IllegalArgumentException("Can't register command with white space character!");
		}
		if (processors.containsKey(command)) {
			throw new IllegalStateException("The command[" + command + "] has been registered!");
		}
		processors.put(command.toLowerCase(), processor);
	}

	/**
	 * 开始监控命令；
	 * 
	 * 方法将堵塞当前线程；
	 * 
	 */
	public synchronized void open() {
		monitoring = true;
		while (monitoring) {
			// 输出提示；
			out.println();
			out.print(prompt);

			// 读入命令；
			String command;
			try {
				command = in.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			if (command == null) {
				continue;
			}
			String[] cmdArgs = StringUtils.tokenizeToStringArray(command, " ");
			if (cmdArgs.length == 0) {
				continue;
			}
			String cmd = cmdArgs[0];
			String[] args;
			if (cmdArgs.length > 1) {
				args = Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length);
			} else {
				args = new String[0];
			}
			CommondProcessor processor = processors.get(cmd.toLowerCase());
			try {
				if (processor != null) {
					processor.onEnter(cmd, args, this);
				} else {
					defaultProcessor.onEnter(cmd, args, this);
				}
			} catch (Exception e) {
				out.println("Error!!--" + e.getMessage());
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 停止命令控制台；
	 */
	public synchronized void close() {
		monitoring = false;
	}
	private Logger logger = LoggerFactory.getLogger(CommandConsole.class);
}
