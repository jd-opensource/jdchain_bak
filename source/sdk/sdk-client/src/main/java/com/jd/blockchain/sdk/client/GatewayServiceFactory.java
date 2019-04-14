package com.jd.blockchain.sdk.client;

import java.io.Closeable;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.data.DigitalSignatureBlob;
import com.jd.blockchain.ledger.data.TransactionService;
import com.jd.blockchain.ledger.data.TxRequestMessage;
import com.jd.blockchain.sdk.BlockchainQueryService;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.BlockchainServiceFactory;
import com.jd.blockchain.sdk.proxy.HttpBlockchainQueryService;
import com.jd.blockchain.utils.http.agent.HttpServiceAgent;
import com.jd.blockchain.utils.http.agent.ServiceConnection;
import com.jd.blockchain.utils.http.agent.ServiceConnectionManager;
import com.jd.blockchain.utils.http.agent.ServiceEndpoint;
import com.jd.blockchain.utils.net.NetworkAddress;

public class GatewayServiceFactory implements BlockchainServiceFactory, Closeable {

	private ServiceConnectionManager httpConnectionManager;

	private BlockchainKeypair userKey;

	private BlockchainService blockchainService;

	protected GatewayServiceFactory(ServiceEndpoint gatewayEndpoint, BlockchainKeypair userKey) {
		httpConnectionManager = new ServiceConnectionManager();
		this.userKey = userKey;

		BlockchainQueryService queryService = createQueryService(gatewayEndpoint);
		TransactionService txProcSrv = createConsensusService(gatewayEndpoint);
		this.blockchainService = new GatewayBlockchainServiceProxy(txProcSrv, queryService);
	}
	
	@Override
	public BlockchainService getBlockchainService() {
		return blockchainService;
	}

	// TODO:暂未实现基于“口令”的认证方式；
	/**
	 * 基于“口令”连接；
	 * 
	 * @param gatewayHost
	 * @param gatewayPort
	 * @param secure
	 * @param userName
	 * @param password
	 * @return
	 */
	// public static BlockchainServiceFactory connect(String gatewayHost, int
	// gatewayPort, boolean secure, String userName,
	// String password) {
	// ServiceEndpoint gatewayEndpoint = new ServiceEndpoint(gatewayHost,
	// gatewayPort, secure);
	// BlockchainServiceFactory factory = new
	// BlockchainServiceFactory(gatewayEndpoint);
	// factory.setMaxConnections(100);
	// return factory;
	// }
	
	public static GatewayServiceFactory connect(NetworkAddress gatewayAddress) {
		return connect(gatewayAddress.getHost(), gatewayAddress.getPort(), gatewayAddress.isSecure(), null);
	}

	public static GatewayServiceFactory connect(NetworkAddress gatewayAddress, BlockchainKeypair userKey) {
		return connect(gatewayAddress.getHost(), gatewayAddress.getPort(), gatewayAddress.isSecure(), userKey);
	}
	
	public static GatewayServiceFactory connect(String gatewayHost, int gatewayPort, boolean secure) {
		return connect(gatewayHost, gatewayPort, secure, null);
	}

	public static GatewayServiceFactory connect(String gatewayHost, int gatewayPort, boolean secure,
			BlockchainKeypair userKey) {
//		if (userKey == null) {
//			throw new IllegalArgumentException("User key is null!");
//		}
		ServiceEndpoint gatewayEndpoint = new ServiceEndpoint(gatewayHost, gatewayPort, secure);
		GatewayServiceFactory factory = new GatewayServiceFactory(gatewayEndpoint, userKey);
		factory.setMaxConnections(100);
		//TODO: 未实现网关对用户的认证；
		//TODO: 未实现加载不同账本的密码算法配置；
		return factory;
	}

	public void setMaxConnections(int maxCount) {
		httpConnectionManager.setMaxTotal(maxCount).setDefaultMaxPerRoute(maxCount);
	}

	private TransactionService createConsensusService(ServiceEndpoint gatewayEndpoint) {
		ServiceConnection connection = httpConnectionManager.create(gatewayEndpoint);
		TransactionService gatewayConsensusService = HttpServiceAgent.createService(HttpConsensusService.class,
				connection, null);
		if (userKey != null) {
			gatewayConsensusService = new EndpointAutoSigner(gatewayConsensusService, userKey);
		}
		return gatewayConsensusService;
	}

	private BlockchainQueryService createQueryService(ServiceEndpoint gatewayEndpoint) {
		ServiceConnection conn = httpConnectionManager.create(gatewayEndpoint);
		return HttpServiceAgent.createService(HttpBlockchainQueryService.class, conn, null);
	}

	@Override
	public void close() {
		httpConnectionManager.close();
	}

	private static class EndpointAutoSigner implements TransactionService {

		private TransactionService innerService;

		private BlockchainKeypair userKey;

		public EndpointAutoSigner(TransactionService innerService, BlockchainKeypair userKey) {
			this.innerService = innerService;
			this.userKey = userKey;
		}

		@Override
		public TransactionResponse process(TransactionRequest txRequest) {
			TxRequestMessage reqMsg = (TxRequestMessage) txRequest;
			//TODO: 未实现按不同的账本的密码参数配置，采用不同的哈希算法和签名算法；
			if (!reqMsg.containsEndpointSignature(userKey.getAddress())) {
				// TODO: 优化上下文对此 TransactionContent 的多次序列化带来的额外性能开销；
				byte[] txContentBytes = BinaryEncodingUtils.encode(txRequest.getTransactionContent(),
						TransactionContent.class);
				PrivKey userPrivKey = userKey.getPrivKey();
				SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(userKey.getAlgorithm());
				if (signatureFunction != null) {
					SignatureDigest signatureDigest = signatureFunction.sign(userPrivKey, txContentBytes);
					DigitalSignature signature = new DigitalSignatureBlob(userKey.getPubKey(), signatureDigest);
					reqMsg.addEndpointSignatures(signature);
				}
			}
			return innerService.process(txRequest);
		}
	}

}
