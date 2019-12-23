package com.jd.blockchain.utils.io;

public interface BytesEncoder<T> {
	
	byte[] encode(T data);
	
	T decode(byte[] bytes);
}
