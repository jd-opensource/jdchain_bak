package com.jd.blockchain.sdk.service;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.consensus.BinaryMessageConverter;
import com.jd.blockchain.ledger.TransactionRequest;

public class TransactionRequestMessageConverter implements BinaryMessageConverter {

	@Override
	public byte[] encode(Object message) {
		return BinaryProtocol.encode(message, TransactionRequest.class);
	}

	@Override
	public Object decode(byte[] messageBytes) {
		return BinaryProtocol.decode(messageBytes);
	}

}
