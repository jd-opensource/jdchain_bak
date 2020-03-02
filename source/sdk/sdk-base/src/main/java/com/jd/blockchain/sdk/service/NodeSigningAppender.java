package com.jd.blockchain.sdk.service;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.MessageService;
import com.jd.blockchain.consensus.client.ConsensusClient;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.transaction.SignatureUtils;
import com.jd.blockchain.transaction.TransactionService;
import com.jd.blockchain.transaction.TxRequestMessage;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link NodeSigningAppender} 以装饰者模式实现，为交易请求附加上节点签名；
 * 
 * @author huanghaiquan
 *
 */
public class NodeSigningAppender implements TransactionService {

	private static Logger LOGGER = LoggerFactory.getLogger(NodeSigningAppender.class);

	static {
		DataContractRegistry.register(NodeRequest.class);
	}

//	private TransactionService consensusService;

	private MessageService messageService;

	private ConsensusClient consensusClient;

	private AsymmetricKeypair nodeKeyPair;
	
	private short hashAlgorithm;

	public NodeSigningAppender(short hashAlgorithm, AsymmetricKeypair nodeKeyPair, ConsensusClient consensusClient) {
		this.hashAlgorithm = hashAlgorithm;
		this.nodeKeyPair = nodeKeyPair;
		this.consensusClient = consensusClient;
	}

//	public NodeSigningAppender(CryptoAlgorithm hashAlgorithm, TransactionService reallyService, CryptoKeyPair nodeKeyPair) {
//		this.hashAlgorithm = hashAlgorithm;
//		this.consensusService = reallyService;
//		this.nodeKeyPair = nodeKeyPair;
//	}

	public NodeSigningAppender init() {
		consensusClient.connect();
		messageService = consensusClient.getMessageService();
		return this;
	}

	@Override
	public TransactionResponse process(TransactionRequest txRequest) {
		TxRequestMessage txMessage = new TxRequestMessage(txRequest);

//		// 生成网关签名；
//		byte[] endpointRequestBytes = BinaryProtocol.encode(txMessage, TransactionRequest.class);
//
//		short signAlgorithm = nodeKeyPair.getAlgorithm();
//		SignatureFunction signFunc = Crypto.getSignatureFunction(signAlgorithm);
//		SignatureDigest signDigest = signFunc.sign(nodeKeyPair.getPrivKey(), endpointRequestBytes);

		DigitalSignature nodeSign = SignatureUtils.sign(txRequest.getTransactionContent(), nodeKeyPair);

		txMessage.addNodeSignatures(nodeSign);

		// 计算交易哈希；
		byte[] nodeRequestBytes = BinaryProtocol.encode(txMessage, TransactionRequest.class);
		HashFunction hashFunc = Crypto.getHashFunction(this.hashAlgorithm);
		HashDigest txHash = hashFunc.hash(nodeRequestBytes);
		txMessage.setHash(txHash);

		try {
			AsyncFuture<byte[]> asyncFuture =  messageService.sendOrdered(BinaryProtocol.encode(txMessage, TransactionRequest.class));
			byte[] result = asyncFuture.get();
			if (result == null) {
				LOGGER.error("Gateway receive [{}]'s result is null!", txRequest.getHash());
				return new ErrorTransactionResponse(txRequest.getTransactionContent().getHash());
			}
			return BinaryProtocol.decode(result);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			LOGGER.error("Gateway send tx [{}] error {} !", txRequest.getHash(), e);
			return new ErrorTransactionResponse(txRequest.getTransactionContent().getHash());
		}
	}


	private static class ErrorTransactionResponse implements TransactionResponse {

		HashDigest contentHash;

		public ErrorTransactionResponse(HashDigest contentHash) {
			this.contentHash = contentHash;
		}

		@Override
		public HashDigest getContentHash() {
			return contentHash;
		}

		@Override
		public TransactionState getExecutionState() {
			return TransactionState.TIMEOUT;
		}

		@Override
		public HashDigest getBlockHash() {
			return null;
		}

		@Override
		public long getBlockHeight() {
			return -1L;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public OperationResult[] getOperationResults() {
			return null;
		}
	}
}
