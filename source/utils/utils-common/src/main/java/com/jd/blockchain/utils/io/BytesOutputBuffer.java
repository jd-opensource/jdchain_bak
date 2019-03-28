package com.jd.blockchain.utils.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 字节输出缓冲区；<br>
 * 
 * 提供一种避免字节数组在内存复制的缓冲写入的实现；<br>
 * 
 * BytesOutputBuffer 是线程安全的；
 * 
 * @author huanghaiquan
 *
 */
public class BytesOutputBuffer {

	private static final int DEFAULT_CAPACITY = 8;

	private volatile int size = 0;

	private int capacity = 0;

	private int cursor = 0;

	private byte[][] buffers;

	public int getSize() {
		return size;
	}

	public BytesOutputBuffer() {
		this(DEFAULT_CAPACITY);
	}

	public BytesOutputBuffer(int initCapacity) {
		if (initCapacity < 0) {
			throw new IllegalArgumentException("Init capacity is negative!");
		}
		this.capacity = initCapacity;
		buffers = new byte[initCapacity][];
	}

	/**
	 * 直接写入；<br>
	 * 
	 * 此方法直接引用参数指定的数组作为缓冲区的组成部分，调用者需要避免在执行之后对参数引用的字节数组再做任何更改；<br>
	 * 
	 * 设计此方法的目的是提供一种通过避免字节数组复制操作来提升写入性能的方法；
	 * 
	 * @param data data
	 */
	public synchronized void write(byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException("data is null!");
		}
		int idx = cursor;
		if (idx == capacity) {
			growCapacity();
		}
		buffers[idx] = data;
		cursor++;
		size += data.length;
	}

	private void growCapacity() {
		int newCapacity = capacity + capacity / 2;
		if (newCapacity == capacity) {
			newCapacity++;
		}
		byte[][] newBuffers = new byte[newCapacity][];
		System.arraycopy(buffers, 0, newBuffers, 0, capacity);
		buffers = newBuffers;
		capacity = newCapacity;
	}
	
	/**
	 * @param buffer buffer
	 */
	public void write(BytesOutputBuffer buffer) {
		byte[][] fromBuffers = buffer.buffers;
		for (int i = 0; i < buffer.cursor; i++) {
			write(fromBuffers[i]);
		}
	}

	/**
	 * 复制写入；<br>
	 * 
	 * 此方法复制参数指定的数组的一个副本作为写入缓冲区的组成部分；
	 * 
	 * @param data data
	 */
	public void writeCopy(byte[] data) {
		int len = data.length;
		byte[] copy = new byte[len];
		System.arraycopy(data, 0, copy, 0, len);
		write(copy);
	}

	/**
	 * * 复制写入；<br>
	 *
	 * 此方法复制参数指定的数组的一个副本作为写入缓冲区的组成部分；
	 * @param data data
	 * @param offset offset
	 * @param len len
	 */
	public void writeCopy(byte[] data, int offset, int len) {
		byte[] copy = new byte[len];
		System.arraycopy(data, offset, copy, 0, len);
		write(copy);
	}

	/**
	 * 把结果输出到指定的缓冲区，并返回写入的长度；
	 * <p>
	 * 如果指定的缓冲区的空间足够，则将写入全部数据，返回值等于 {@link #getSize()} ；<br>
	 * 如果指定的缓冲区的空间足够，则写满为止，返回实际写入的数据大小；<br>
	 * 
	 * 调用者可以把返回值与 {@link #getSize()} 比较，以判断是否完成写入；
	 * 
	 * @param outBuffer outBuffer
	 * @param offset offset
	 * @return int
	 */
	public synchronized int writeTo(byte[] outBuffer, int offset) {
		int len = Math.min(size, outBuffer.length - offset);
		int t = len;
		int s;
		for (int i = 0; i < cursor & t > 0; i++) {
			s = Math.min(buffers[i].length, t);
			System.arraycopy(buffers[i], 0, outBuffer, offset, s);
			offset += s;
			t -= s;
		}
		return len;
	}
	
	public synchronized int writeTo(OutputStream out) {
		try {
			for (int i = 0; i < cursor; i++) {
				out.write(buffers[i]);
			}
			return size;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	/**
	 * 返回所有内容的副本；
	 * 
	 * @return byte array;
	 */
	public byte[] toBytes() {
		byte[] data =new byte[size];
		writeTo(data, 0);
		return data;
	}

}
