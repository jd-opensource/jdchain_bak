package com.jd.blockchain.utils.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.utils.IllegalDataException;

/**
 * 字节块读取；
 * 
 * 执行与 {@link BytesChunkWriter} 的写入过程相对应的读取逻辑，分块读出内容；
 * 
 * 在创建 BytesChunkReader 实例时，会先校验魔术字节，如果模式字节校验失败，将抛出 IllegalDataException ;
 * 
 * 注：BytesChunkReader 不是线程安全的；
 * 
 * @author haiq
 *
 */
public class BytesChunkReader implements Closeable {

	private byte[] magicBytes;

	private InputStream in;
	
	/**
	 * 创建 BytesChunkReader 实例；
	 * 
	 * 如果模式字节校验失败，将抛出 IllegalDataException ;
	 * 
	 * @param magicString
	 *            魔术字符；将被以 UTF-8 编码转码为二进制字节参与校验；
	 * @param in
	 *            输入流；
	 * @throws IOException exception
	 */
	public BytesChunkReader(String magicString, InputStream in) throws IOException {
		this.magicBytes = magicString.getBytes("UTF-8");
		this.in = in;
		
		checkMagic();
	}

	/**
	 * 创建 BytesChunkReader 实例；
	 * 
	 * 如果模式字节校验失败，将抛出 IllegalDataException ;
	 * 
	 * @param magicBytes
	 *            魔术字节；
	 * @param in
	 *            输入流；
	 * @throws IOException exception
	 */
	public BytesChunkReader(byte[] magicBytes, InputStream in) throws IOException {
		this.magicBytes = magicBytes;
		this.in = in;

		checkMagic();
	}

	private void checkMagic() throws IOException {
		byte[] buff = new byte[magicBytes.length];
		int len = in.read(buff);
		if (len <= 0) {
			throw new IllegalDataException("No data to read!");
		}
		if (len < magicBytes.length) {
			throw new IllegalDataException("Mismatch magic bytes!");
		}
		if (!BytesUtils.equals(magicBytes, buff)) {
			throw new IllegalDataException("Mismatch magic bytes!");
		}
	}

	/**
	 * 读取下一个数据块；
	 * @return 数据块；如果已经没有数据块可读，则返回 null，并关闭输入流;
	 * @throws IOException exception
	 */
	public byte[] read() throws IOException {
		int len = readNextLengthHeader();
		if (len < 0) {
			// No chunk;
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int reallyLen = BytesUtils.copy(in, out, len);
		if (reallyLen < len) {
			throw new IllegalDataException(
					"No enough data as the length header indicated to read from the input stream !");
		}
		return out.toByteArray();// ByteArrayOutputStream is not necessary to
									// close;
	}

	/**
	 * 读取下一个数据块；
	 * @param out 输出流；
	 * @return 数据块；如果已经没有数据块可读，则返回 -1，并关闭输入流;
	 * @throws IOException exception
	 */
	public int read(OutputStream out) throws IOException {
		int len = readNextLengthHeader();
		if (len < 0) {
			// No chunk;
			return -1;
		}
		int cpLen = BytesUtils.copy(in, out, len);
		if (cpLen < len) {
			throw new IllegalDataException(
					"No enough data as the length header indicated to read from the input stream!");
		}
		return len;
	}
	
	/**
	 * 返回下一个长度头；
	 * 
	 * 如果已到结尾，则返回 -1，并关闭输入流；
	 * 
	 * @return
	 * @throws IOException 
	 */
	private int readNextLengthHeader() throws IOException{
		int len = BytesUtils.readInt(in);
		if (len <= 0) {
			// No chunk;
			close();
			return -1;
		}
		return len;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public static byte[][] extract(byte[] magicBytes, byte[] compactedBytes) throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(compactedBytes);
		BytesChunkReader reader = new BytesChunkReader(magicBytes, in);
		List<byte[]> dataBytesList;
		try {
			dataBytesList = new ArrayList<byte[]>();
			byte[] dataBytes = null;
			while ((dataBytes = reader.read()) != null) {
				dataBytesList.add(dataBytes);
			}
		} finally{
			reader.close();
		}
		
		return dataBytesList.toArray(new byte[dataBytesList.size()][]);
	}
}
