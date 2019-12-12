package com.jd.blockchain.consensus;

public interface BinaryMessageConverter {
	
	byte[] encode(Object message);
	
	Object decode(byte[] messageBytes);
	
}
