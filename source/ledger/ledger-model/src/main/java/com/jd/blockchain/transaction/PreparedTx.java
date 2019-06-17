package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.contract.ContractSerializeUtils;
import com.jd.blockchain.contract.EventResult;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.*;

import java.util.Map;

public class PreparedTx implements PreparedTransaction {

	private TransactionRequestBuilder txReqBuilder;

	private TransactionService txProcessor;

	private Map<Integer, EventResult> eventResults;

	public PreparedTx(TransactionRequestBuilder txReqBuilder, TransactionService txProcessor) {
		this(txReqBuilder, txProcessor, null);
	}

	public PreparedTx(TransactionRequestBuilder txReqBuilder, TransactionService txProcessor, Map<Integer, EventResult> eventResults) {
		this.txReqBuilder = txReqBuilder;
		this.txProcessor = txProcessor;
		this.eventResults = eventResults;
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
		if (operationResults != null && operationResults.length > 0 &&
				eventResults != null && !eventResults.isEmpty()) {
			for (OperationResult operationResult : operationResults) {
				int opIndex = operationResult.getIndex();
				EventResult eventResult = eventResults.get(opIndex);
				if (eventResult != null) {
					eventResult.done(ContractSerializeUtils.resolve(operationResult.getResult()));
				}
			}
		}
		return txResponse;
	}


}
