package com.jd.blockchain.utils.http.agent;

import java.io.OutputStream;

interface RequestBodyResolver {
	
	void resolve(Object[] args, OutputStream out);
	
}
