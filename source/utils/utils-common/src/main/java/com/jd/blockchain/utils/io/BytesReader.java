package com.jd.blockchain.utils.io;

import java.io.IOException;
import java.io.InputStream;

public interface BytesReader {
	
	void resolvFrom(InputStream in) throws IOException;
	
}
