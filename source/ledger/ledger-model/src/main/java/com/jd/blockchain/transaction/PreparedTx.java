package com.jd.blockchain.transaction;

import java.io.IOException;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionResponse;

public class PreparedTx implements PreparedTransaction {

	private TransactionRequestBuilder txReqBuilder;

	private TransactionService txService;

	/**
	 * 创建一个“就绪交易”对象；
	 * 
	 * @param txReqBuilder 交易请求构建器；
	 * @param txService  交易处理服务；
	 */
	public PreparedTx(TransactionRequestBuilder txReqBuilder, TransactionService txService) {
		this.txReqBuilder = txReqBuilder;
		this.txService = txService;
	}

	@Override
	public HashDigest getHash() {
		return txReqBuilder.getHash();
	}

	@Override
	public TransactionContent getTransactionContent() {
		return txReqBuilder.getTransactionContent();
	}

	@Override
	public DigitalSignature sign(AsymmetricKeypair keyPair) {
		DigitalSignature signature = SignatureUtils.sign(getTransactionContent(), keyPair);
		addSignature(signature);
		return signature;
	}

	@Override
	public void addSignature(DigitalSignature signature) {
		txReqBuilder.addEndpointSignature(signature);
	}

	@Override
	public TransactionResponse commit() {
		// 生成请求；
		TransactionRequest txReq = txReqBuilder.buildRequest();
		// 发起交易请求；
		return txService.process(txReq);
	}

	@Override
	public void close() throws IOException {
	}

}
