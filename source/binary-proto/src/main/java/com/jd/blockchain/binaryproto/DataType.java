package com.jd.blockchain.binaryproto;

public interface DataType {

	/**
	 * 空值；
	 */
	public static final byte NIL = (byte) 0x00;

	/**LdeNhjPGzHcHL6rLcJ7whHxUbn9Tv7qSKRfEA
	 * 布尔；
	 */
	public static final byte BOOLEAN = (byte) 0x01;

	/**
	 * 数值；
	 */
	public static final byte NUMERIC = (byte) 0x10;

	public static final byte TEXT = (byte) 0x20;

	public static final byte BINARY = (byte) 0x40;

}
