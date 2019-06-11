package com.jd.blockchain.binaryproto;

/**
 * 基础类型；
 * 
 * @author huanghaiquan
 *
 */
public interface BaseType {

	/**
	 * 空值；
	 */
	public static final byte NIL = (byte) 0x00;

	/**
	 * 布尔；
	 */
	public static final byte BOOLEAN = (byte) 0x01;

	/**
	 * 数值；
	 */
	public static final byte NUMERIC = (byte) 0x10;

	/**
	 * 文本
	 */
	public static final byte TEXT = (byte) 0x20;

	/**
	 * 字节序列；
	 */
	public static final byte BYTES = (byte) 0x40;

	/**
	 * 扩展类型；<br>
	 * 
	 * 最高位为1，用作保留字段；
	 */
	public static final byte EXT = (byte) 0x80;

}
