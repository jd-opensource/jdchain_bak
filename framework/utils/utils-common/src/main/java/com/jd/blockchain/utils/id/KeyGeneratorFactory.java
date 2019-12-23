package com.jd.blockchain.utils.id;

public class KeyGeneratorFactory {

	private static final char[] base36 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private char[][] baseChars;

	/**
	 * 
	 * @param baseChars
	 *            数位的基；一个二元数组，其长度表示生成的 key 的位数以及每一位的取值的字符范围；
	 */
	private KeyGeneratorFactory(char[][] baseChars) {
		if (baseChars.length == 0) {
			throw new IllegalArgumentException("Empty baseChars!");
		}
		for (char[] cs : baseChars) {
			if (cs == null || cs.length == 0) {
				throw new IllegalArgumentException("Empty baseChars!");
			}
		}
		this.baseChars = baseChars;
	}
	
	/**
	 * 创建基为 36 的 KeyGenerator 的工厂实例；
	 * 
	 * @param len key的长度；
	 * @return KeyGeneratorFactory
	 */
	public static KeyGeneratorFactory createBase36Instance(int len){
		if (len <= 0) {
			throw new IllegalArgumentException("The len is less than or equal zero!");
		}
		char[][] baseChars = new char[len][];
		for (int i = 0; i < baseChars.length; i++) {
			baseChars[i] = base36;
		}
		return new KeyGeneratorFactory(baseChars);
	}

	/**
	 * 创建一个 KeyGenerator 实例；
	 * 
	 * 注：此方法返回的 KeyGenerator 的实现不是线程安全的；
	 * 
	 * @return KeyGenerator
	 */
	public KeyGenerator createKeyGenerator() {
		return new CommonKeyGenerator();
	}

	private class CommonKeyGenerator implements KeyGenerator {

		private int[] indexes;

		/**
		 * 创建一个 KeyGenerator 的实例；
		 * 
		 * @param baseChars
		 *            数位的基；一个二元数组，其长度表示生成的 key 的位数以及每一位的取值的字符范围；
		 */
		private CommonKeyGenerator() {
			indexes = new int[baseChars.length];
		}

		@Override
		public String next() {
			StringBuilder key = new StringBuilder();
			for (int i = 0; i < baseChars.length; i++) {
				key.append(baseChars[i][indexes[i]]);
			}
			indexes[baseChars.length - 1]++;
			for (int i = indexes.length - 1; i > -1; i--) {
				if (indexes[i] == baseChars[i].length) {
					if (i > 0) {
						// 进位；
						indexes[i - 1]++;
					} else {
						// 溢出；忽略；
					}
					indexes[i] = 0;
				}
			}
			return key.toString();
		}

	}

}
