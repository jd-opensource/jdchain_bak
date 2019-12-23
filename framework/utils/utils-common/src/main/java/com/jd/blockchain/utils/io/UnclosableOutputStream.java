package com.jd.blockchain.utils.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link UnclosableOutputStream} 限制了对输出流执行关闭操作，调用 {@link #close()} 方法将不起任何作用，用在需要防止对输出流的使用中误关闭的情形；
 * 
 * @author huanghaiquan
 *
 */
public class UnclosableOutputStream extends FilterOutputStream {

	public UnclosableOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void close() throws IOException {
		// do nothing for avoiding closing the inner outputstream;
	}
}
