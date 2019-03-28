package com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionResponse;

public class PreparedTx implements PreparedTransaction {

	private TransactionRequestBuilder txReqBuilder;

	private TransactionService txProcessor;

	public PreparedTx(TransactionRequestBuilder txReqBuilder, TransactionService txProcessor) {
		this.txReqBuilder = txReqBuilder;
		this.txProcessor = txProcessor;
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
	public DigitalSignature sign(CryptoKeyPair keyPair) {
//		SignatureFunction signatureFunction = new ED25519SignatureFunction();
		SignatureFunction signatureFunction = CryptoUtils.sign(keyPair.getPubKey().getAlgorithm());
		PrivKey privKey = keyPair.getPrivKey();
		byte[] content = BinaryEncodingUtils.encode(getTransactionContent(), TransactionContent.class);
		SignatureDigest signatureDigest = signatureFunction.sign(privKey, content);
		DigitalSignature signature = new DigitalSignatureBlob(keyPair.getPubKey(), signatureDigest);
		addSignature(signature);
		return signature;
	}

	@Override
	public void addSignature(DigitalSignature signature) {
		txReqBuilder.addEndpointSignature(signature);
	}

	@Override
	public TransactionResponse commit() {
		TransactionRequest txReq = txReqBuilder.buildRequest();
		// 发起交易请求；
		TransactionResponse txResponse = txProcessor.process(txReq);
		return txResponse;
	}
}
