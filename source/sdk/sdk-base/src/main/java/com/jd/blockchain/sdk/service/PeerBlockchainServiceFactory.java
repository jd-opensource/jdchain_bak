package com.jd.blockchain.sdk.service;

import com.jd.blockchain.consensus.*;
import com.jd.blockchain.consensus.client.ClientFactory;
import com.jd.blockchain.consensus.client.ClientSettings;
import com.jd.blockchain.consensus.client.ConsensusClient;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.sdk.*;
import com.jd.blockchain.sdk.proxy.HttpBlockchainQueryService;
import com.jd.blockchain.setting.GatewayIncomingSetting;
import com.jd.blockchain.setting.LedgerIncomingSetting;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TransactionService;
import com.jd.blockchain.utils.http.agent.HttpServiceAgent;
import com.jd.blockchain.utils.http.agent.ServiceConnection;
import com.jd.blockchain.utils.http.agent.ServiceConnectionManager;
import com.jd.blockchain.utils.http.agent.ServiceEndpoint;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.net.NetworkAddress;
import com.jd.blockchain.utils.security.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerBlockchainServiceFactory implements BlockchainServiceFactory, Closeable {

	private static Logger LOGGER = LoggerFactory.getLogger(PeerBlockchainServiceFactory.class);

	private static final Map<NetworkAddress, PeerBlockchainServiceFactory> peerBlockchainServiceFactories = new ConcurrentHashMap<>();

	private static final Map<NetworkAddress, PeerManageService> peerManageServices = new ConcurrentHashMap<>();

	private static final Map<HashDigest, LedgerAccessContextImpl> accessContextMap = new ConcurrentHashMap<>();

	private ServiceConnectionManager httpConnectionManager;

	private PeerServiceProxy peerServiceProxy;


	/**
	 * @param httpConnectionManager
	 *            Http请求管理器；
	 * @param accessAbleLedgers
	 *            可用账本列表；
	 */
	protected PeerBlockchainServiceFactory(ServiceConnectionManager httpConnectionManager,
			LedgerAccessContextImpl[] accessAbleLedgers) {
		this.httpConnectionManager = httpConnectionManager;
		this.peerServiceProxy = new PeerServiceProxy(accessAbleLedgers);
	}

	public void addLedgerAccessContexts(LedgerAccessContextImpl[] accessContexts) {
		this.peerServiceProxy.addLedgerAccessContexts(accessContexts);
	}

	@Override
	public BlockchainService getBlockchainService() {
		return peerServiceProxy;
	}

	/**
	 * 返回交易服务；
	 * 
	 * <br>
	 * 
	 * 返回的交易服务聚合了该节点绑定的多个账本的交易服务，并根据交易请求中指定的目标账本选择相应的交易服务进行转发；
	 * 
	 * @return
	 */
	public TransactionService getTransactionService() {
		return peerServiceProxy;
	}

	/**
	 * 连接到指定的共识节点；
	 * 
	 * @param peerAddr
	 *            提供对网关接入认证的节点的认证地址列表； <br>
	 *            按列表的先后顺序连接节点进行认证，从第一个成功通过的节点请求整个区块链网络的拓扑配置，并建立起和整个区块链网络的连接；<br>
	 *            此参数指定的节点列表可以是整个区块链网络的全部节点的子集，而不必包含所有节点；
	 * 
	 * @return 区块链服务工厂实例；
	 */
	public static PeerBlockchainServiceFactory connect(AsymmetricKeypair gatewayKey, NetworkAddress peerAddr, List<String> peerProviders) {

		if (peerProviders == null || peerProviders.isEmpty()) {
			throw new AuthenticationException("No peer Provider was set!");
		}
		ClientIdentificationsProvider authIdProvider = authIdProvider(gatewayKey, peerProviders);

		GatewayIncomingSetting incomingSetting = auth(peerAddr, authIdProvider);

		if (incomingSetting == null) {
			throw new AuthenticationException("No peer was succeed authenticating from!");
		}

		PeerBlockchainServiceFactory factory = null;

		ServiceConnectionManager httpConnectionManager;

		PeerManageService peerManageService;

		if (peerBlockchainServiceFactories.containsKey(peerAddr)) {
			factory = peerBlockchainServiceFactories.get(peerAddr);
			httpConnectionManager = factory.httpConnectionManager;
		} else {
			httpConnectionManager = new ServiceConnectionManager();
		}

		if (peerManageServices.containsKey(peerAddr)) {
			peerManageService = peerManageServices.get(peerAddr);
		} else {
			ServiceConnection httpConnection = httpConnectionManager.create(new ServiceEndpoint(peerAddr));
			peerManageService = new PeerManageService(httpConnection,
					HttpServiceAgent.createService(HttpBlockchainQueryService.class,
							httpConnection, null));
			peerManageServices.put(peerAddr, peerManageService);
		}

		LedgerIncomingSetting[] ledgerSettings = incomingSetting.getLedgers();
		// 判断当前节点对应账本是否一致
		List<LedgerIncomingSetting> needInitSettings = new ArrayList<>();
		for (LedgerIncomingSetting setting : ledgerSettings) {
			HashDigest currLedgerHash = setting.getLedgerHash();
			if (!accessContextMap.containsKey(currLedgerHash)) {
				needInitSettings.add(setting);
			}
		}

		if (!needInitSettings.isEmpty()) {
			LedgerAccessContextImpl[] accessAbleLedgers = new LedgerAccessContextImpl[needInitSettings.size()];
			BlockchainQueryService queryService = peerManageService.getQueryService();

			for (int i = 0; i < needInitSettings.size(); i++) {
				LedgerIncomingSetting ledgerSetting = needInitSettings.get(i);
				String providerName = ledgerSetting.getProviderName();
				ConsensusProvider provider = ConsensusProviders.getProvider(providerName);
				byte[] clientSettingBytes = ByteArray.fromBase64(ledgerSetting.getClientSetting());

				ClientIncomingSettings clientIncomingSettings = provider.getSettingsFactory().getIncomingSettingsEncoder().decode(clientSettingBytes);
				ClientFactory clientFactory = provider.getClientFactory();
				ClientSettings clientSettings = clientFactory.buildClientSettings(clientIncomingSettings);
				ConsensusClient consensusClient = clientFactory.setupClient(clientSettings);

				TransactionService autoSigningTxProcService = enableGatewayAutoSigning(gatewayKey,
						ledgerSetting.getCryptoSetting(), consensusClient);

				LedgerAccessContextImpl accCtx = new LedgerAccessContextImpl();
				accCtx.ledgerHash = ledgerSetting.getLedgerHash();
				accCtx.cryptoSetting = ledgerSetting.getCryptoSetting();
				accCtx.queryService = queryService;
				accCtx.txProcService = autoSigningTxProcService;
				accCtx.consensusClient = consensusClient;

				accessAbleLedgers[i] = accCtx;

				accessContextMap.put(accCtx.ledgerHash, accCtx);
			}
			if (factory == null) {
				factory = new PeerBlockchainServiceFactory(httpConnectionManager,
						accessAbleLedgers);
				peerBlockchainServiceFactories.put(peerAddr, factory);
			} else {
				factory.addLedgerAccessContexts(accessAbleLedgers);
			}
//			PeerBlockchainServiceFactory factory = new PeerBlockchainServiceFactory(httpConnectionManager,
//					accessAbleLedgers);
		}



//		ServiceConnectionManager httpConnectionManager = new ServiceConnectionManager();
//		ServiceConnection httpConnection = httpConnectionManager.create(new ServiceEndpoint(peerAddr));
//		BlockchainQueryService queryService = HttpServiceAgent.createService(HttpBlockchainQueryService.class,
//				httpConnection, null);
//
//		LedgerIncomingSetting[] ledgerSettings = incomingSetting.getLedgers();
//
//		LedgerAccessContextImpl[] accessAbleLedgers = new LedgerAccessContextImpl[ledgerSettings.length];
//		for (int i = 0; i < ledgerSettings.length; i++) {
//			LedgerIncomingSetting ledgerSetting = ledgerSettings[i];
//			String providerName = ledgerSetting.getProviderName();
//			ConsensusProvider provider = ConsensusProviders.getProvider(providerName);
//			byte[] clientSettingBytes = ByteArray.fromBase64(ledgerSetting.getClientSetting());
//
//			ClientIncomingSettings clientIncomingSettings = provider.getSettingsFactory().getIncomingSettingsEncoder().decode(clientSettingBytes);
//			ClientFactory clientFactory = provider.getClientFactory();
//			ClientSettings clientSettings = clientFactory.buildClientSettings(clientIncomingSettings);
//			ConsensusClient consensusClient = clientFactory.setupClient(clientSettings);
//
//			TransactionService autoSigningTxProcService = enableGatewayAutoSigning(gatewayKey,
//					ledgerSetting.getCryptoSetting(), consensusClient);
//
//
//			LedgerAccessContextImpl accCtx = new LedgerAccessContextImpl();
//			accCtx.ledgerHash = ledgerSetting.getLedgerHash();
//			accCtx.cryptoSetting = ledgerSetting.getCryptoSetting();
//			accCtx.queryService = queryService;
//			accCtx.txProcService = autoSigningTxProcService;
//			accCtx.consensusClient = consensusClient;
//
//			accessAbleLedgers[i] = accCtx;
//
//			accessContextMap.put(accCtx.ledgerHash, accCtx);
//		}
//
//		PeerBlockchainServiceFactory factory = new PeerBlockchainServiceFactory(httpConnectionManager,
//				accessAbleLedgers);
		return factory;
	}

	private static GatewayIncomingSetting auth(NetworkAddress peerAuthAddr, ClientIdentifications authIds) {
		try {
			ManagementHttpService gatewayMngService = getGatewayManageService(peerAuthAddr);

			// 接入认证，获得接入配置；
			// 传递网关账户地址及签名；
			GatewayIncomingSetting incomingSetting = gatewayMngService.authenticateGateway(authIds);
			return incomingSetting;
		} catch (Exception e) {
			LOGGER.warn("Cann't authenticate gateway incoming from peer[" + peerAuthAddr.toString() + "]!--"
					+ e.getMessage(), e);
			return null;
		}
	}

	private static ManagementHttpService getGatewayManageService(NetworkAddress peer) {
		ServiceEndpoint peerServer = new ServiceEndpoint(peer.getHost(), peer.getPort(), false);
		ManagementHttpService gatewayMngService = HttpServiceAgent.createService(ManagementHttpService.class,
				peerServer);
		return gatewayMngService;
	}

	/**
	 * 启用网关自动签名；
	 * 
	 * @param nodeKeyPair
	 * @param cryptoSetting
	 * @return
	 */
	private static TransactionService enableGatewayAutoSigning(AsymmetricKeypair nodeKeyPair, CryptoSetting cryptoSetting,
			ConsensusClient consensusClient) {
		NodeSigningAppender signingAppender = new NodeSigningAppender(cryptoSetting.getHashAlgorithm(),
				nodeKeyPair, consensusClient);
		return signingAppender.init();
	}

	@Override
	public void close() {
		try {
			for (Map.Entry<HashDigest, LedgerAccessContextImpl> entry : accessContextMap.entrySet()) {
				LedgerAccessContextImpl ctx = entry.getValue();
				ctx.consensusClient.close();
			}
			httpConnectionManager.close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static ClientIdentificationsProvider authIdProvider(AsymmetricKeypair gatewayKey, List<String> peerProviders) {
		ClientIdentificationsProvider authIdProvider = new ClientIdentificationsProvider();
		for (String peerProvider : peerProviders) {
			ConsensusProvider provider = ConsensusProviders.getProvider(peerProvider);
			ClientFactory clientFactory = provider.getClientFactory();
			ClientIdentification authId = clientFactory.buildAuthId(gatewayKey);
			authIdProvider.add(authId);
		}
		return authIdProvider;
	}

	private static class LedgerAccessContextImpl implements LedgerAccessContext {

		private HashDigest ledgerHash;

		private CryptoSetting cryptoSetting;

		private TransactionService txProcService;

		private BlockchainQueryService queryService;

		private ConsensusClient consensusClient;

		@Override
		public HashDigest getLedgerHash() {
			return ledgerHash;
		}

		@Override
		public CryptoSetting getCryptoSetting() {
			return cryptoSetting;
		}

		@Override
		public TransactionService getTransactionService() {
			return txProcService;
		}

		@Override
		public BlockchainQueryService getQueryService() {
			return queryService;
		}

	}

	private static final class PeerManageService {

		public PeerManageService(ServiceConnection httpConnection, BlockchainQueryService queryService) {
			this.httpConnection = httpConnection;
			this.queryService = queryService;
		}

		ServiceConnection httpConnection;

		BlockchainQueryService queryService;

		public ServiceConnection getHttpConnection() {
			return httpConnection;
		}

		public void setHttpConnection(ServiceConnection httpConnection) {
			this.httpConnection = httpConnection;
		}

		public BlockchainQueryService getQueryService() {
			return queryService;
		}

		public void setQueryService(BlockchainQueryService queryService) {
			this.queryService = queryService;
		}
	}

}
