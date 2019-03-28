package com.jd.blockchain.binaryproto;

/**
 * 表示一个二进制数据片段的格式标准；
 * <p>
 * 
 * 一个数据契约的实例输出生成的二进制数据段{@link BinarySegmentHeader}是由一系列小的标准化的数据片段组成；
 * 
 * @author huanghaiquan
 *
 */
public class BinarySliceSpec {

	private boolean repeatable;

	private int length;

	private boolean dynamic;

	private String name;

	private String description;

	/**
	 * 是否重复多次；true 表示以一个头部表示接下来的片段将重复的次数；
	 * 
	 * @return
	 */
	public boolean isRepeatable() {
		return repeatable;
	}

	/**
	 * 字节长度；
	 * 
	 * @return
	 */
	public int getLength() {
		return length;
	}

	/**
	 * 长度是动态扩展的；
	 * 
	 * @return
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	private BinarySliceSpec(String name, String description, boolean repeatable, int length, boolean dynamic) {
		this.name = name;
		this.description = description;
		this.repeatable = repeatable;
		this.length = length;
		this.dynamic = dynamic;
	}

	public static BinarySliceSpec newFixedSlice(int length, String name, String description) {
		return new BinarySliceSpec(name, description, false, length, false);
	}

	public static BinarySliceSpec newRepeatableFixedSlice(int length, String name, String description) {
		return new BinarySliceSpec(name, description, true, length, false);
	}

	public static BinarySliceSpec newDynamicSlice(String name, String description) {
		return new BinarySliceSpec(name, description, false, -1, true);
	}
	
	public static BinarySliceSpec newRepeatableDynamicSlice(String name, String description) {
		return new BinarySliceSpec(name, description, true, -1, true);
	}
}