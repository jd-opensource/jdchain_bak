package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.*;

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
	public DigitalSignature sign(AsymmetricKeypair keyPair) {
		SignatureFunction signatureFunction = Crypto.getSignatureFunction(keyPair.getAlgorithm());
		PrivKey privKey = keyPair.getPrivKey();
		byte[] content = BinaryProtocol.encode(getTransactionContent(), TransactionContent.class);
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
		// 重新包装操作集合
		OperationResult[] operationResults = txResponse.getOperationResults();
		if (operationResults != null && operationResults.length > 0) {
			OperationResult[] wrapOpResults = new OperationResult[operationResults.length];
			for (int i = 0; i < operationResults.length; i++) {
				wrapOpResults[i] = new OperationResultData(operationResults[i]);
			}
			return new TxResponseMessage(txResponse, wrapOpResults);
		}
		return txResponse;
	}


}
