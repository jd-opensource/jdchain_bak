package com.jd.blockchain.peer.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.jd.blockchain.ledger.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.ClientIdentification;
import com.jd.blockchain.consensus.ClientIdentifications;
import com.jd.blockchain.consensus.ClientIncomingSettings;
import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.action.ActionResponse;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartNodeSettings;
import com.jd.blockchain.consensus.mq.server.MsgQueueMessageDispatcher;
import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.consensus.service.NodeServer;
import com.jd.blockchain.consensus.service.ServerSettings;
import com.jd.blockchain.consensus.service.StateMachineReplicate;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.core.LedgerAdminAccount;
import com.jd.blockchain.ledger.core.LedgerManage;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.manage.GatewayIncomingSetting;
import com.jd.blockchain.manage.LedgerIncomingSetting;
import com.jd.blockchain.peer.ConsensusRealm;
import com.jd.blockchain.peer.LedgerBindingConfigAware;
import com.jd.blockchain.peer.PeerManage;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.web.converters.BinaryMessageConverter;

/**
 * 网关管理服务；
 * 
 * 提供
 * 
 * @author huanghaiquan
 *
 */
@RestController
@RequestMapping(path = "/management")
public class ManagementController implements LedgerBindingConfigAware, PeerManage {

	private static Logger LOGGER = LoggerFactory.getLogger(ManagementController.class);

	public static final String GATEWAY_PUB_EXT_NAME = ".gw.pub";

	public static final int MIN_GATEWAY_ID = 10000;

	// @Autowired
	// private PeerSettings peerSetting;

//	@Autowired
//	private ConsensusTransactionService consensusService;

	// private ConsensusPeer consensusReplica;

	@Autowired
	private LedgerManage ledgerManager;

	@Autowired
	private DbConnectionFactory connFactory;

	// private Map<HashDigest, DbConnection> ledgerConns = new
	// ConcurrentHashMap<>();

	private Map<HashDigest, MsgQueueMessageDispatcher> ledgerTxConverters = new ConcurrentHashMap<>();

	private Map<HashDigest, NodeServer> ledgerPeers = new ConcurrentHashMap<>();
	private Map<HashDigest, CryptoSetting> ledgerCryptoSettings = new ConcurrentHashMap<>();

	// private Map<ConsensusNode, ConsensusRealm> nodeRealms = new
	// ConcurrentHashMap<>();

	// private Map<HashDigest, ConsensusRealm> ledgerRealms = new
	// ConcurrentHashMap<>();
	// private Map<HashDigest, ConsensusRealm> ledgerRealmsNoConflict = new
	// ConcurrentHashMap<>();

	private LedgerBindingConfig config;

	@Autowired
	private MessageHandle consensusMessageHandler;

	@Autowired
	private StateMachineReplicate consensusStateManager;

	// private static int step = 0;
	// private static int temp = 0;

	static {
        DataContractRegistry.register(LedgerInitOperation.class);
		DataContractRegistry.register(LedgerBlock.class);
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

		DataContractRegistry.register(ActionResponse.class);

		DataContractRegistry.register(BftsmartConsensusSettings.class);
		DataContractRegistry.register(BftsmartNodeSettings.class);

	}

	@PostConstruct
	private void init() {

	}

	@PreDestroy
	private void destroy() {
//		DbConnection[] conns = ledgerConns.values().toArray(new DbConnection[ledgerConns.size()]);
//		ledgerConns.clear();
//		for (DbConnection conn : conns) {
//			try {
//				conn.close();
//			} catch (Exception e) {
//				// Ignore;
//			}
//		}
	}

	/**
	 * 接入认证；
	 * 
	 * @param clientIdentifications
	 * @return
	 */
	@RequestMapping(path = "/gateway/auth", method = RequestMethod.POST, consumes = BinaryMessageConverter.CONTENT_TYPE_VALUE)
	public GatewayIncomingSetting authenticateGateway(@RequestBody ClientIdentifications clientIdentifications) {
		// 去掉不严谨的网关注册和认证逻辑；暂时先放开，不做认证，后续应该在链上注册网关信息，并基于链上的网关信息进行认证；
		// by: huanghaiquan; at 2018-09-11 18:34;
		// TODO: 实现网关的链上注册与认证机制；
		// TODO: 暂时先返回全部账本对应的共识网络配置信息；以账本哈希为 key 标识每一个账本对应的共识域、以及共识配置参数；
		if (ledgerPeers.size() == 0 || clientIdentifications == null) {
			return null;
		}

        ClientIdentification[] identificationArray = clientIdentifications.getClientIdentifications();
		if (identificationArray == null || identificationArray.length <= 0) {
		    return null;
        }

		GatewayIncomingSetting setting = new GatewayIncomingSetting();
		List<LedgerIncomingSetting> ledgerIncomingList = new ArrayList<LedgerIncomingSetting>();

		for (HashDigest ledgerHash : ledgerPeers.keySet()) {

			NodeServer peer = ledgerPeers.get(ledgerHash);

			String peerProviderName = peer.getProviderName();

			ConsensusProvider provider = ConsensusProviders.getProvider(peer.getProviderName());

            ClientIncomingSettings clientIncomingSettings = null;
            for (ClientIdentification authId : identificationArray) {
                if (authId.getProviderName() == null ||
                        authId.getProviderName().length() <= 0 ||
                        !authId.getProviderName().equalsIgnoreCase(peerProviderName)) {
                    continue;
                }
                try {
                    clientIncomingSettings = peer.getManageService().authClientIncoming(authId);
                    break;
                } catch (Exception e) {
                    throw new AuthenticationServiceException(e.getMessage(), e);
                }
            }
            if (clientIncomingSettings == null) {
                continue;
            }

			byte[] clientIncomingBytes = provider.getSettingsFactory().getIncomingSettingsEncoder()
					.encode(clientIncomingSettings);
			String base64ClientIncomingSettings = ByteArray.toBase64(clientIncomingBytes);

			LedgerIncomingSetting ledgerIncomingSetting = new LedgerIncomingSetting();
			ledgerIncomingSetting.setLedgerHash(ledgerHash);
			ledgerIncomingSetting.setCryptoSetting(ledgerCryptoSettings.get(ledgerHash));
			ledgerIncomingSetting.setClientSetting(base64ClientIncomingSettings);
			ledgerIncomingSetting.setProviderName(peerProviderName);

			ledgerIncomingList.add(ledgerIncomingSetting);

		}
		setting.setLedgers(ledgerIncomingList.toArray(new LedgerIncomingSetting[ledgerIncomingList.size()]));
		return setting;
	}

	@Override
	public void setConfig(LedgerBindingConfig config) {
		// TODO 更新配置；暂时不考虑变化过程的平滑切换问题,后续完善该流程；
		// 1、检查账本的数据库配置；a、配置发生变化的账本，建立新的账本库(LedgerRepository)替换旧的实例；b、加入新增加的账本库实例；c、移除已经废弃的账本库；
		// 2、完成账本库更改后，读取最新的共识配置信息，更新共识域；
		// 3、基于当前共识地址检查共识域；a、启动新增加的共识地址，以及更新相应的共识域关系；c、已经废弃的共识域直接停止；
		try {
			// remove all existing ledger repositories;
			HashDigest[] existingLedgerHashs = ledgerManager.getLedgerHashs();
			for (HashDigest lh : existingLedgerHashs) {
				ledgerManager.unregister(lh);
			}
			HashDigest[] ledgerHashs = config.getLedgerHashs();
			for (HashDigest ledgerHash : ledgerHashs) {
				setConfig(config,ledgerHash);
//				LedgerBindingConfig.BindingConfig bindingConfig = config.getLedger(ledgerHash);
//				DbConnection dbConnNew = connFactory.connect(bindingConfig.getDbConnection().getUri(),
//						bindingConfig.getDbConnection().getPassword());
//				LedgerRepository ledgerRepository = ledgerManager.register(ledgerHash, dbConnNew.getStorageService());
//
//				// load provider;
//				LedgerAdminAccount ledgerAdminAccount = ledgerRepository.getAdminAccount();
//				String consensusProvider = ledgerAdminAccount.getSetting().getConsensusProvider();
//				ConsensusProvider provider = ConsensusProviders.getProvider(consensusProvider);
//				// find current node;
//				Bytes csSettingBytes = ledgerAdminAccount.getSetting().getConsensusSetting();
//				ConsensusSettings csSettings = provider.getSettingsFactory().getConsensusSettingsEncoder()
//						.decode(csSettingBytes.toBytes());
//				NodeSettings currentNode = null;
//				for (NodeSettings nodeSettings : csSettings.getNodes()) {
//					if (nodeSettings.getAddress().equals(bindingConfig.getParticipant().getAddress())) {
//						currentNode = nodeSettings;
//					}
//				}
//				if (currentNode == null) {
//					throw new IllegalArgumentException(
//							"Current node is not found from the consensus settings of ledger[" + ledgerHash.toBase58()
//									+ "]!");
//				}
//				ServerSettings serverSettings = provider.getServerFactory().buildServerSettings(ledgerHash.toBase58(), csSettings, currentNode.getAddress());
//
//				NodeServer server = provider.getServerFactory().setupServer(serverSettings, consensusMessageHandler,
//						consensusStateManager);
//				ledgerPeers.put(ledgerHash, server);
//				ledgerCryptoSettings.put(ledgerHash, ledgerAdminAccount.getSetting().getCryptoSetting());

			}

			// remove duplicate consensus realm,and establish consensus peer and consensus
			// realm corresponding relationship
			// initBindingConfig(config);
			this.config = config;

		} catch (Exception e) {
			LOGGER.error("Error occurred on configing LedgerBindingConfig! --" + e.getMessage(), e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public NodeServer setConfig(LedgerBindingConfig config, HashDigest ledgerHash) {
		LedgerBindingConfig.BindingConfig bindingConfig = config.getLedger(ledgerHash);
		DbConnection dbConnNew = connFactory.connect(bindingConfig.getDbConnection().getUri(),
				bindingConfig.getDbConnection().getPassword());
		LedgerRepository ledgerRepository = ledgerManager.register(ledgerHash, dbConnNew.getStorageService());

		// load provider;
		LedgerAdminAccount ledgerAdminAccount = ledgerRepository.getAdminAccount();
		String consensusProvider = ledgerAdminAccount.getSetting().getConsensusProvider();
		ConsensusProvider provider = ConsensusProviders.getProvider(consensusProvider);
		// find current node;
		Bytes csSettingBytes = ledgerAdminAccount.getSetting().getConsensusSetting();
		ConsensusSettings csSettings = provider.getSettingsFactory().getConsensusSettingsEncoder()
				.decode(csSettingBytes.toBytes());
		NodeSettings currentNode = null;
		for (NodeSettings nodeSettings : csSettings.getNodes()) {
			if (nodeSettings.getAddress().equals(bindingConfig.getParticipant().getAddress())) {
				currentNode = nodeSettings;
			}
		}
		if (currentNode == null) {
			throw new IllegalArgumentException(
					"Current node is not found from the consensus settings of ledger[" + ledgerHash.toBase58()
							+ "]!");
		}
		ServerSettings serverSettings = provider.getServerFactory().buildServerSettings(ledgerHash.toBase58(), csSettings, currentNode.getAddress());

		NodeServer server = provider.getServerFactory().setupServer(serverSettings, consensusMessageHandler,
				consensusStateManager);
		ledgerPeers.put(ledgerHash, server);
		ledgerCryptoSettings.put(ledgerHash, ledgerAdminAccount.getSetting().getCryptoSetting());

		return server;
	}

	// private void initBindingConfig(LedgerBindingConfig config) {
	// boolean intersection = false;
	// // to remove intersection consensus realm
	// for (HashDigest hashDigest : ledgerRealms.keySet()) {
	// ConsensusRealm consensusRealm1i = ledgerRealms.get(hashDigest);
	// for (ConsensusRealm consensusRealm1j : ledgerRealms.values()) {
	// // avoid compare with myself
	// if (consensusRealm1i.equals(consensusRealm1j)) {
	// continue;
	// }
	// if (consensusRealm1i.hasIntersection(consensusRealm1j)) {
	// intersection = true;
	// break;
	// }
	// }
	// // prompt consensus realm conflict info
	// if (intersection == true) {
	// ConsoleUtils.info("\r\nconsensus realm intersection with other consensus
	// realm\r\n");
	// continue;
	// }
	// if (intersection == false) {
	// // add consensus realm without conflict to ledgerRealmsNoConflict
	// ledgerRealmsNoConflict.put(hashDigest, consensusRealm1i);
	//
	// // String consensusSystemFile =
	// config.getLedger(hashDigest).getCsConfigFile();
	// int currentId = config.getLedger(hashDigest).getParticipant().getId();
	// // init consensusSystemConfig;
	// ConsensusProperties csProps =
	// ConsensusProperties.resolve(consensusRealm1i.getSetting());
	// ConsensusPeer consensusPeer = new ConsensusPeer(consensusRealm1i, currentId,
	// consensusService,
	// csProps.getProperties());
	// ledgerPeers.put(hashDigest, consensusPeer);
	// }
	// } // END OF FOR:get ledgerRealmsNoConflict and ledgerPeers
	//
	// }

	@Override
	public ConsensusRealm[] getRealms() {
		throw new IllegalStateException("Not implemented!");
	}

	@Override
	public void runAllRealms() {
		for (NodeServer peer : ledgerPeers.values()) {
			runRealm(peer);
		}
		// try {
		//
		// // for (ConsensusPeer peer : ledgerPeers.values()) {
		// for (Map.Entry<HashDigest, ConsensusPeer> entry : ledgerPeers.entrySet()) {
		// HashDigest ledgerHash = entry.getKey();
		// ConsensusPeer peer = entry.getValue();
		// // TODO: 多线程启动；
		// ConsensusNode[] nodes = peer.getConsensusRealm().getNodes();
		// StringBuilder consensusInfo = new StringBuilder();
		// for (ConsensusNode node : nodes) {
		// consensusInfo.append(
		// String.format("[%s]-%s; ", node.getAddress(),
		// node.getConsensusAddress().toString()));
		// }
		// LOGGER.debug(String.format("-------- start consensus peer[Id=%s] --Nodes=%s
		// -------------",
		// peer.getCurrentId(), consensusInfo.toString()));
		// peer.start();
		// // 设置消息队列
		// MsgQueueMessageDispatcher messageDispatcher = ledgerTxConverters.get(ledgerHash);
		//
		// if (messageDispatcher == null) {
		// LedgerBindingConfig.BindingConfig bindingConfig =
		// this.config.getLedger(ledgerHash);
		// MQConnectionConfig mqConnection = bindingConfig.getMqConnection();
		// if (mqConnection != null && mqConnection.getServer() != null) {
		// MessageQueueConfig mqConfig = new
		// MessageQueueConfig(mqConnection.getServer(),
		// mqConnection.getTopic());
		// messageDispatcher = MessageDispatcherFactory.newInstance(mqConfig, peer);
		// Executors.newSingleThreadExecutor().execute(messageDispatcher); // 启动监听
		// }
		// }
		// }
		// } catch (Exception e) {
		// LOGGER.error("Error occurred on starting all consensus realms! --" +
		// e.getMessage(), e);
		// throw new IllegalStateException(e.getMessage(), e);
		// }
	}

	@Override
	public void runRealm(NodeServer nodeServer) {
		nodeServer.start();
	}

	@Override
	public void closeAllRealms() {
		for (NodeServer peer : ledgerPeers.values()) {
			peer.stop();
		}
	}
}
