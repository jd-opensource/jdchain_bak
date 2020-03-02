//package com.jd.blockchain.sdk.samples;
//
//import com.jd.blockchain.ledger.BlockchainEventType;
//import com.jd.blockchain.ledger.BlockchainKeyGenerator;
//import com.jd.blockchain.ledger.BlockchainKeyPair;
//import com.jd.blockchain.ledger.CryptoKeyType;
//import com.jd.blockchain.ledger.StateMap;
//import com.jd.blockchain.sdk.BlockchainEventHandle;
//import com.jd.blockchain.sdk.BlockchainEventListener;
//import com.jd.blockchain.sdk.BlockchainEventMessage;
//import com.jd.blockchain.sdk.BlockchainService;
//import com.jd.blockchain.sdk.client.BlockchainServiceFactory;
//
//import my.utils.net.NetworkAddress;
//
///**
// * 演示监听区块链事件的过程；
// * 
// * @author huanghaiquan
// *
// */
//public class SDKDemo_EventListener {
//
//	public static BlockchainKeyPair CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate(CryptoKeyType.ED25519);
//
//	/**
//	 * 演示合约执行的过程；
//	 */
//	public static void demoContract() {
//		// 区块链共识域；
//		String realm = "SUPPLY_CHAIN_ALLIANCE";
//		// 节点地址列表；
//		NetworkAddress[] peerAddrs = { new NetworkAddress("192.168.10.10", 8080),
//				new NetworkAddress("192.168.10.11", 8080), new NetworkAddress("192.168.10.12", 8080),
//				new NetworkAddress("192.168.10.13", 8080) };
//
//		// 网关客户端编号；
//		int gatewayId = 1001;
//		// 账本地址；
//		String ledgerAddress = "ffkjhkeqwiuhivnsh3298josijdocaijsda==";
//		// 客户端的认证账户；
//		String clientAddress = "kkjsafieweqEkadsfaslkdslkae998232jojf==";
//		String privKey = "safefsd32q34vdsvs";
//		// 创建服务代理；
//		final String GATEWAY_IP = "127.0.0.1";
//		final int GATEWAY_PORT = 80;
//		final boolean SECURE = false;
//		BlockchainServiceFactory serviceFactory = BlockchainServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE,
//				CLIENT_CERT);
//		BlockchainService service = serviceFactory.getBlockchainService();
//
//		// 监听账户变动；
//		String walletAccount = "MMMEy902jkjjJJDkshreGeasdfassdfajjf==";
//		service.addBlockchainEventListener(BlockchainEventType.PAYLOAD_UPDATED.CODE, null, walletAccount,
//				new BlockchainEventListener() {
//					@Override
//					public void onEvent(BlockchainEventMessage eventMessage, BlockchainEventHandle eventHandle) {
//						// 钱包余额；
//						StateMap balancePayload = service.getState(walletAccount, "RMB-ASSET","name");
//						Long balance = (Long) balancePayload.get("RMB-ASSET").longValue();
//						if (balance != null) {
//							// notify balance change;
//						} else {
//							// wallet is empty and isn't listened any more;
//							eventHandle.cancel();
//						}
//					}
//				});
//
//		// 销毁服务代理；
//		serviceFactory.close();
//	}
//
//}
