package com.jd.blockchain.utils.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * 通过 {@link ForwardReadonlyInputStream} 对输入流进行包装，限制只能对输入流进行向前读取的操作，不允许调用 {@link #mark(int)}、 {@link #reset()}、{@link #close()}操作；
 * 
 * @author huanghaiquan
 *
 */
public class ForwardReadonlyInputStream extends FilterInputStream{

	protected ForwardReadonlyInputStream(InputStream in) {
		super(in);
	}

	
	@Override
	public boolean markSupported() {
		return false;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		// forward readonly, so do nothing instead here;
	}
	
	@Override
	public synchronized void reset() throws IOException {
		// forward readonly, so do nothing instead here;
	}
	
	@Override
	public void close() throws IOException {
		// forward readonly, cann't be closed, so do nothing instead here;
	}
}
