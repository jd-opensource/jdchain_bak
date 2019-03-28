package com.jd.blockchain.consensus;

import com.jd.blockchain.utils.concurrent.AsyncFuture;

public interface MessageService {
	
	AsyncFuture<byte[]> sendOrdered(byte[] message);
	
	AsyncFuture<byte[]> sendUnordered(byte[] message);
	
}
