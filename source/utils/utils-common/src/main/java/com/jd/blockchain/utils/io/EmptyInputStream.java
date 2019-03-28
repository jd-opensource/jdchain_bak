package com.jd.blockchain.utils.io;

import java.io.IOException;
import java.io.InputStream;

public class EmptyInputStream extends InputStream{
	
	public static final EmptyInputStream INSTANCE = new EmptyInputStream();
	
	private EmptyInputStream() {
	}

	@Override
	public int read() throws IOException {
		return -1;
	}

}
