package com.jd.blockchain.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArgumentSet {
	
//	private static Pattern ARG_PATTERN = Pattern.compile("\\-.+\\={1}.*");

	private ArgEntry[] args;

	private Map<String, ArgEntry> prefixArgs = new HashMap<>();

	private Set<String> options = new HashSet<>();

	private ArgumentSet(ArgEntry[] args) {
		this.args = args;
		for (ArgEntry arg : args) {
			if (arg.prefix != null) {
				prefixArgs.put(arg.prefix, arg);
			}
			if (arg.option != null) {
				options.add(arg.option);
			}
		}
	}
	
	public static boolean hasOption(String[] args, String option) {
		boolean contains = false;
		if (args != null) {
			for (String a : args) {
				if (option.equalsIgnoreCase(a)) {
					contains = true;
					break;
				}
			}
		}
		return contains;
	}
	
	public static Setting setting() {
		return new Setting();
	}

	public static ArgumentSet resolve(String[] args, Setting setting) {
		if (args == null) {
			return new ArgumentSet(new ArgEntry[0]);
		}
		List<ArgEntry> argEntries = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			if (setting.prefixes.contains(args[i])) {
				ArgEntry ae = new ArgEntry();
				ae.prefix = args[i];
				if (i+1 >= args.length) {
					throw new IllegalArgumentException(String.format("缺少 %s 参数！", args[i]));
				}
				i++;
				ae.value = args[i];
				argEntries.add(ae);
				continue;
			}
			
			if(setting.options.contains(args[i])){
				ArgEntry ae = new ArgEntry();
				ae.option = args[i];
				argEntries.add(ae);
				continue;
			}
		}
		return new ArgumentSet(argEntries.toArray(new ArgEntry[argEntries.size()]));
	}

	/**
	 * 按照原始顺序排列的参数列表；
	 * 
	 * @return ArgEntry[]
	 */
	public ArgEntry[] getArgs() {
		return args;
	}

	public ArgEntry getArg(String prefix) {
		return prefixArgs.get(prefix);
	}

	public boolean hasOption(String option) {
		return options.contains(option);
	}

	/**
	 * @author huanghaiquan
	 *
	 */
	public static class ArgEntry {

		private String prefix;

		private String value;

		private String option;

		/**
		 * 前缀； <br>
		 * 如果不是前缀参数，则为 null；
		 * 
		 * @return String
		 */
		public String getPrefix() {
			return prefix;
		}

		/**
		 * 参数值；<br>
		 * 
		 * 如果只是选项参数，则返回 null;
		 * 
		 * @return String
		 */
		public String getValue() {
			return value;
		}

		/**
		 * 选项；
		 * 
		 * @return String
		 */
		public String getOption() {
			return option;
		}

	}

	public static class Setting {

		private Set<String> prefixes = new HashSet<>();

		private Set<String> options = new HashSet<>();
		
		private Setting() {
		}
		
		
		public Setting prefix(String... prefixes) {
			this.prefixes.addAll(Arrays.asList(prefixes));
			return this;
		}
		
		public Setting option(String... options) {
			this.options.addAll(Arrays.asList(options));
			return this;
		}
		
	}

}
