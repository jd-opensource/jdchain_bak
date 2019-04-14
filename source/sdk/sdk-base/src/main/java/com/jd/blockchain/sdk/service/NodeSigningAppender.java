package com.jd.blockchain.sdk.service;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.MessageService;
import com.jd.blockchain.consensus.client.ConsensusClient;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.data.DigitalSignatureBlob;
import com.jd.blockchain.ledger.data.TransactionService;
import com.jd.blockchain.ledger.data.TxRequestMessage;
import com.jd.blockchain.utils.concurrent.AsyncFuture;

/**
 * {@link NodeSigningAppender} 以装饰者模式实现，为交易请求附加上节点签名；
 * 
 * @author huanghaiquan
 *
 */
public class NodeSigningAppender implements TransactionService {

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

		// 生成网关签名；
		byte[] endpointRequestBytes = BinaryEncodingUtils.encode(txMessage, TransactionRequest.class);

		short signAlgorithm = nodeKeyPair.getAlgorithm();
		SignatureFunction signFunc = CryptoServiceProviders.getSignatureFunction(signAlgorithm);
		SignatureDigest signDigest = signFunc.sign(nodeKeyPair.getPrivKey(), endpointRequestBytes);
		txMessage.addNodeSignatures(new DigitalSignatureBlob(nodeKeyPair.getPubKey(), signDigest));

		// 计算交易哈希；
		byte[] nodeRequestBytes = BinaryEncodingUtils.encode(txMessage, TransactionRequest.class);
		HashFunction hashFunc = CryptoServiceProviders.getHashFunction(signAlgorithm);
		HashDigest txHash = hashFunc.hash(nodeRequestBytes);
		txMessage.setHash(txHash);

		AsyncFuture<byte[]> result =  messageService.sendOrdered(BinaryEncodingUtils.encode(txMessage, TransactionRequest.class));

		return BinaryEncodingUtils.decode(result.get());
	}
}
