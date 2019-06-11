package com.jd.blockchain.binaryproto;

/**
 * 基础类型标志；
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
	 * 整数；
	 */
	public static final byte INTEGER = (byte) 0x10;

	/**
	 * 8位整数；
	 */
	public static final byte INT8 = (byte) (INTEGER | 0x01);

	/**
	 * 16位整数；
	 */
	public static final byte INT16 = (byte) (INTEGER | 0x02);

	/**
	 * 32位整数；
	 */
	public static final byte INT32 = (byte) (INTEGER | 0x03);

	/**
	 * 64位整数；
	 */
	public static final byte INT64 = (byte) (INTEGER | 0x04);

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
