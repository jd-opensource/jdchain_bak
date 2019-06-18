package com.jd.blockchain.transaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionResponse;

public class PreparedTx implements PreparedTransaction {

	private TransactionRequestBuilder txReqBuilder;

	private TransactionService txProcessor;

	private OperationReturnValueHandler[] opReturnValueHandlers;

	/**
	 * 创建一个“就绪交易”对象；
	 * 
	 * @param txReqBuilder             交易请求构建器；
	 * @param txProcessor              交易处理服务；
	 * @param opReturnValueHandlerList 操作返回值处理器列表；
	 */
	public PreparedTx(TransactionRequestBuilder txReqBuilder, TransactionService txProcessor,
			Collection<OperationReturnValueHandler> opReturnValueHandlerList) {
		this.txReqBuilder = txReqBuilder;
		this.txProcessor = txProcessor;

		this.opReturnValueHandlers = opReturnValueHandlerList
				.toArray(new OperationReturnValueHandler[opReturnValueHandlerList.size()]);
		// 按照操作索引升序排列;
		Arrays.sort(opReturnValueHandlers, new Comparator<OperationReturnValueHandler>() {
			@Override
			public int compare(OperationReturnValueHandler o1, OperationReturnValueHandler o2) {
				return o1.getOperationIndex() - o2.getOperationIndex();
			}
		});
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
		try {
			TransactionRequest txReq = txReqBuilder.buildRequest();
			// 发起交易请求；
			TransactionResponse txResponse = txProcessor.process(txReq);

			// 解析返回值；正常的情况下，返回结果列表与结果处理器列表中元素对应的操作索引是一致的；
			OperationResult[] opResults = txResponse.getOperationResults();
			if (opResults != null && opResults.length > 0) {
				if (opResults.length != opReturnValueHandlers.length) {
					throw new IllegalStateException(String.format(
							"The operation result list of tx doesn't match it's return value handler list! --[TX.Content.Hash=%s][NumOfResults=%s][NumOfHandlers=%s]",
							txReq.getTransactionContent().getHash(), opResults.length, opReturnValueHandlers.length));
				}
				for (int i = 0; i < opResults.length; i++) {
					if (opResults[i].getIndex() != opReturnValueHandlers[i].getOperationIndex()) {
						throw new IllegalStateException(
								"The operation indexes of the items in the result list and in the handler list don't match!");
					}
					opReturnValueHandlers[i].setReturnValue(opResults[i].getResult());
				}
			}
			return txResponse;
		} catch (Exception e) {
			//TODO: 出错时清理交易上下文，释放与交易关联对异步等待资源，避免当前线程死锁；
			
		}
	}

}
