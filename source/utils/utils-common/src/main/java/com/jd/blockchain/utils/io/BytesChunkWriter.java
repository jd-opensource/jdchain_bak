package com.jd.blockchain.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 字节块写入；
 * 
 * BytesChunkWriter 定义了魔术字节(magic bytes)作为字节块流的开端，写入在所有的内容之前；
 * 
 * 在写入每一个字节块之前，先把块长度作为一个 4 字节的二进制数组写入，然后跟着写入块的内容；
 * 
 * 当完成全部写入之后，写入 -1 的 4 字节二进制格式作为结尾标识；
 * 
 * 注：BytesChunkReader 不是线程安全的；
 * 
 * @author haiq
 *
 */
public class BytesChunkWriter implements Closeable {
	
	private static byte[] END_BYTES = BytesUtils.toBytes(-1);
	
	private byte[] magicBytes;
	
	private OutputStream out;
	
	private boolean enclose = false;

	/**
	 * 创建一个 BytesChunkWriter 实例；
	 * 
	 * @param magicString
	 *            魔术字符；作为字节块流的起始标识；
	 * @param out
	 *            要写入的流；
	 * @throws IOException exception
	 */
	public BytesChunkWriter(String magicString, OutputStream out) throws IOException {
		try {
			this.out = out;
			this.magicBytes = magicString.getBytes("UTF-8");
			writeMagic();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 创建一个 BytesChunkWriter 实例；
	 * 
	 * @param magicBytes
	 *            魔术字符；作为字节块流的起始标识；
	 * @param out out
	 * @throws IOException exception
	 */
	public BytesChunkWriter(byte[] magicBytes, OutputStream out) throws IOException {
		this.magicBytes = magicBytes;
		this.out = out;
		writeMagic();
	}

	private void writeMagic() throws IOException {
		if (magicBytes.length == 0) {
			throw new IllegalArgumentException("The magicBytes is empty!");
		}
		out.write(magicBytes);
	}
	
	/**
	 * 写入字节块；
	 * 
	 * 在写入字节块之前先写入 4 个字节的块长度头，然后写入内容；
	 * 
	 * @param bytes bytes
	 * @throws IOException  exception
	 */
	public void write(byte[] bytes) throws IOException{
		checkEncosed();
		byte[] lenHeader = BytesUtils.toBytes(bytes.length);
		out.write(lenHeader);
		out.write(bytes);
	}
	
	/**
	 * 从指定的流读入指定长度的块并写入流；
	 * 
	 * 如果读入的长度不足指定的值，将抛出 IllegalArgumentException 异常；
	 * 
	 * @param len 块长度；
	 * @param in 要读入数据的流；
	 * @throws IOException  exception
	 */
	public void write(int len, InputStream in) throws IOException{
		if (len < 1) {
			throw new IllegalArgumentException("The len must be positive!");
		}
		byte[] lenHeader = BytesUtils.toBytes(len);
		out.write(lenHeader);
		int wrLen = BytesUtils.copy(in, out, len);
		if (wrLen < len) {
			throw new IllegalArgumentException("The length of the input stream is less than the specified len of chunk!");
		}
	}
	
	public void flush() throws IOException{
		out.flush();
	}
	
	private void encloseChunk() throws IOException{
		if (enclose) {
			return;
		}
		out.write(END_BYTES);
		enclose = true;
	}
	
	private void checkEncosed(){
		if (enclose) {
			throw new IllegalStateException("This BytesChunkWriter instance is enclosed!");
		}
	}

	/**
	 * 结束并关闭流；
	 * 
	 * @throws IOException exception
	 */
	@Override
	public void close() throws IOException {
		if (!enclose) {
			encloseChunk();
			flush();
		}
		out.close();
	}
	
	public static byte[] compact(byte[] magicBytes, byte[]... dataBytes) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BytesChunkWriter writer = new BytesChunkWriter(magicBytes, out);
		try {
			if (dataBytes != null) {
				for (byte[] bs : dataBytes) {
					writer.write(bs);;
				}
			}
			writer.flush();
		} finally{
			writer.close();
		}
		return out.toByteArray();
	}
}
