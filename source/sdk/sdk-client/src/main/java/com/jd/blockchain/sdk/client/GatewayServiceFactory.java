package com.jd.blockchain.sdk.client;

import java.io.Closeable;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.ClientIdentification;
import com.jd.blockchain.consensus.ClientIdentifications;
import com.jd.blockchain.consensus.action.ActionRequest;
import com.jd.blockchain.consensus.action.ActionResponse;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.BlockchainServiceFactory;
import com.jd.blockchain.sdk.proxy.HttpBlockchainQueryService;
import com.jd.blockchain.transaction.*;
import com.jd.blockchain.utils.http.agent.HttpServiceAgent;
import com.jd.blockchain.utils.http.agent.ServiceConnection;
import com.jd.blockchain.utils.http.agent.ServiceConnectionManager;
import com.jd.blockchain.utils.http.agent.ServiceEndpoint;
import com.jd.blockchain.utils.net.NetworkAddress;
import com.jd.blockchain.web.serializes.ByteArrayObjectUtil;

public class GatewayServiceFactory implements BlockchainServiceFactory, Closeable {

	private ServiceConnectionManager httpConnectionManager;

	private BlockchainKeypair userKey;

	private BlockchainService blockchainService;

	static {
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(TransactionContentBody.class);
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(NodeRequest.class);
		DataContractRegistry.register(EndpointRequest.class);
		DataContractRegistry.register(TransactionResponse.class);
		DataContractRegistry.register(DataAccountKVSetOperation.class);
		DataContractRegistry.register(DataAccountKVSetOperation.KVWriteEntry.class);

		DataContractRegistry.register(Operation.class);
		DataContractRegistry.register(ContractCodeDeployOperation.class);
		DataContractRegistry.register(ContractEventSendOperation.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(ParticipantRegisterOperation.class);
		DataContractRegistry.register(ParticipantStateUpdateOperation.class);

		DataContractRegistry.register(ActionRequest.class);
		DataContractRegistry.register(ActionResponse.class);
		DataContractRegistry.register(ClientIdentifications.class);
		DataContractRegistry.register(ClientIdentification.class);
		DataContractRegistry.register(BytesValueList.class);

		// 注册角色/权限相关接口
		DataContractRegistry.register(RolesConfigureOperation.class);
		DataContractRegistry.register(RolesConfigureOperation.RolePrivilegeEntry.class);
		DataContractRegistry.register(UserAuthorizeOperation.class);
		DataContractRegistry.register(UserAuthorizeOperation.UserRolesEntry.class);
		DataContractRegistry.register(PrivilegeSet.class);
		DataContractRegistry.register(RoleSet.class);
		DataContractRegistry.register(SecurityInitSettings.class);
		DataContractRegistry.register(RoleInitSettings.class);
		DataContractRegistry.register(UserAuthInitSettings.class);
		DataContractRegistry.register(LedgerMetadata_V2.class);

		ByteArrayObjectUtil.init();
	}

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

	/**
	 * 连接网关节点；
	 * 
	 * @param gatewayAddress 网关节点的网络地址；
	 * @return 网关服务工厂的实例；
	 */
	public static GatewayServiceFactory connect(NetworkAddress gatewayAddress) {
		return connect(gatewayAddress.getHost(), gatewayAddress.getPort(), gatewayAddress.isSecure(), null);
	}

	/**
	 * 网关服务工厂的实例；
	 * 
	 * @param gatewayAddress 网关节点的网络地址；
	 * @param userKey        自动交易签名的用户密钥；可选参数，如果不为 null，则在提交交易时，自动以参数指定的密钥签署交易；
	 * @return 网关服务工厂的实例；
	 */
	public static GatewayServiceFactory connect(NetworkAddress gatewayAddress, BlockchainKeypair userKey) {
		return connect(gatewayAddress.getHost(), gatewayAddress.getPort(), gatewayAddress.isSecure(), userKey);
	}

	/**
	 * 连接网关节点；
	 * 
	 * @param gatewayHost 网关节点的地址；
	 * @param gatewayPort 网关节点的端口；
	 * @param secure      是否采用安全的通讯协议(HTTPS)；
	 * @return 网关服务工厂的实例；
	 */
	public static GatewayServiceFactory connect(String gatewayHost, int gatewayPort, boolean secure) {
		return connect(gatewayHost, gatewayPort, secure, null);
	}

	/**
	 * 连接网关节点；
	 * 
	 * @param gatewayHost 网关节点的地址；
	 * @param gatewayPort 网关节点的端口；
	 * @param secure      是否采用安全的通讯协议(HTTPS)；
	 * @param userKey     自动交易签名的用户密钥；可选参数，如果不为 null，则在提交交易时，自动以参数指定的密钥签署交易；
	 * @return 网关服务工厂的实例；
	 */
	public static GatewayServiceFactory connect(String gatewayHost, int gatewayPort, boolean secure,
			BlockchainKeypair userKey) {
//		if (userKey == null) {
//			throw new IllegalArgumentException("User key is null!");
//		}
		ServiceEndpoint gatewayEndpoint = new ServiceEndpoint(gatewayHost, gatewayPort, secure);
		GatewayServiceFactory factory = new GatewayServiceFactory(gatewayEndpoint, userKey);
		factory.setMaxConnections(100);
		// TODO: 未实现网关对用户的认证；
		// TODO: 未实现加载不同账本的密码算法配置；
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
			// TODO: 未实现按不同的账本的密码参数配置，采用不同的哈希算法和签名算法；
			if (!reqMsg.containsEndpointSignature(userKey.getAddress())) {
				// TODO: 优化上下文对此 TransactionContent 的多次序列化带来的额外性能开销；
				DigitalSignature signature = SignatureUtils.sign(txRequest.getTransactionContent(), userKey);
				reqMsg.addEndpointSignatures(signature);
//
//
//
//				byte[] txContentBytes = BinaryProtocol.encode(txRequest.getTransactionContent(),
//						TransactionContent.class);
//				PrivKey userPrivKey = userKey.getPrivKey();
//				SignatureFunction signatureFunction = Crypto.getSignatureFunction(userKey.getAlgorithm());
//				if (signatureFunction != null) {
//					SignatureDigest signatureDigest = signatureFunction.sign(userPrivKey, txContentBytes);
//					DigitalSignature signature = new DigitalSignatureBlob(userKey.getPubKey(), signatureDigest);
//					reqMsg.addEndpointSignatures(signature);
//				}
			}
			return innerService.process(txRequest);
		}
	}

}
