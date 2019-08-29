package com.jd.blockchain.transaction;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.springframework.cglib.proxy.UndeclaredThrowableException;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
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

	private OperationResultHandle[] opReturnValueHandlers;

	private TxStateManager stateManager;

	/**
	 * 创建一个“就绪交易”对象；
	 * 
	 * @param txReqBuilder             交易请求构建器；
	 * @param txProcessor              交易处理服务；
	 * @param opReturnValueHandlerList 操作返回值处理器列表；
	 */
	public PreparedTx(TxStateManager stateManager, TransactionRequestBuilder txReqBuilder,
			TransactionService txProcessor, Collection<OperationResultHandle> opReturnValueHandlerList) {
		this.stateManager = stateManager;
		this.txReqBuilder = txReqBuilder;
		this.txProcessor = txProcessor;

		this.opReturnValueHandlers = opReturnValueHandlerList
				.toArray(new OperationResultHandle[opReturnValueHandlerList.size()]);
		// 按照操作索引升序排列;
		Arrays.sort(opReturnValueHandlers, new Comparator<OperationResultHandle>() {
			@Override
			public int compare(OperationResultHandle o1, OperationResultHandle o2) {
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
		stateManager.commit();
		TransactionResponse txResponse = null;
		try {
			TransactionRequest txReq = txReqBuilder.buildRequest();
			// 发起交易请求；
			txResponse = txProcessor.process(txReq);

			stateManager.complete();

		} catch (Exception ex) {
			stateManager.close();
			handleError(ex);
			throw new UndeclaredThrowableException(ex);
		}

		if (txResponse != null) {
			handleResults(txResponse);
		}

		return txResponse;
	}

	@Override
	public void close() throws IOException {
		if (!stateManager.close()) {
			TransactionCancelledExeption error = new TransactionCancelledExeption(
					"Prepared transaction has been cancelled!");
			handleError(error);
		}
	}

	private void handleError(Throwable error) {
		for (OperationResultHandle handle : opReturnValueHandlers) {
			handle.complete(error);
		}
	}

	private void handleResults(TransactionResponse txResponse) {
		// 解析返回值；正常的情况下，返回结果列表与结果处理器列表中元素对应的操作索引是一致的；
		OperationResult[] opResults = txResponse.getOperationResults();
		if (opResults != null && opResults.length > 0) {
			if (opResults.length != opReturnValueHandlers.length) {
				throw new IllegalStateException(String.format(
						"The operation result list of tx doesn't match it's return value handler list! --[TX.Content.Hash=%s][NumOfResults=%s][NumOfHandlers=%s]",
						txResponse.getContentHash(), opResults.length, opReturnValueHandlers.length));
			}
			for (int i = 0; i < opResults.length; i++) {
				if (opResults[i].getIndex() != opReturnValueHandlers[i].getOperationIndex()) {
					throw new IllegalStateException(
							"The operation indexes of the items in the result list and in the handler list don't match!");
				}
				opReturnValueHandlers[i].complete(opResults[i].getResult());
			}
		}
	}

}
