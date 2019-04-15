package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.Transaction;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * 演示区块链信息查询的过程；
 * 
 * @author huanghaiquan
 *
 */
public class SDKDemo_Query {

	public static BlockchainKeypair CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate("ED25519");

	public static final HashDigest LEDGER_HASH = Crypto.getHashFunction("SHA256")
			.hash("xkxjcioewfqwe".getBytes());

	/**
	 * 演示合约执行的过程；
	 */
	public static void demoContract() {
		// 区块链共识域；
		String realm = "SUPPLY_CHAIN_ALLIANCE";
		// 节点地址列表；
		NetworkAddress[] peerAddrs = { new NetworkAddress("192.168.10.10", 8080),
				new NetworkAddress("192.168.10.11", 8080), new NetworkAddress("192.168.10.12", 8080),
				new NetworkAddress("192.168.10.13", 8080) };

		// 网关客户端编号；
		int gatewayId = 1001; // 客户端的认证账户；
		// 账本地址；
		String ledgerAddress = "ffkjhkeqwiuhivnsh3298josijdocaijsda==";
		String clientAddress = "kkjsafieweqEkadsfaslkdslkae998232jojf==";
		String privKey = "safefsd32q34vdsvs";
		// 创建服务代理；
		final String GATEWAY_IP = "127.0.0.1";
		final int GATEWAY_PORT = 80;
		final boolean SECURE = false;
		GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE,
				CLIENT_CERT);
		BlockchainService service = serviceFactory.getBlockchainService();

		// 查询区块信息；
		// 区块高度；
		long ledgerNumber = service.getLedger(LEDGER_HASH).getLatestBlockHeight();
		// 最新区块；
		LedgerBlock latestBlock = service.getBlock(LEDGER_HASH, ledgerNumber);
		// 区块中的交易的数量；
		long txCount = service.getTransactionCount(LEDGER_HASH, latestBlock.getHash());
		// 获取交易列表；
		LedgerTransaction[] txList = service.getTransactions(LEDGER_HASH, ledgerNumber, 0, 100);

		// 根据交易的 hash 获得交易；注：客户端生成 PrepareTransaction 时得到交易hash；
		HashDigest txHash = txList[0].getTransactionContent().getHash();
		Transaction tx = service.getTransactionByContentHash(LEDGER_HASH, txHash);

		// 获取数据；
		String commerceAccount = "GGhhreGeasdfasfUUfehf9932lkae99ds66jf==";
		String[] objKeys = new String[] { "x001", "x002" };
		KVDataEntry[] kvData = service.getDataEntries(LEDGER_HASH, commerceAccount, objKeys);

		long payloadVersion = kvData[0].getVersion();

//		boolean exist = service.containState(LEDGER_HASH, commerceAccount, "x003");

		// 按条件查询；
		// 1、从保存会员信息的账户地址查询；
//		String condition = "female = true AND age > 18 AND address.city = 'beijing'";
//		String memberInfoAccountAddress = "kkf2io39823jfIjfiIRWKQj30203fx==";
//		StateMap memberInfo = service.queryObject(LEDGER_HASH, memberInfoAccountAddress, condition);
//
//		// 2、从保存会员信息的账户地址查询；
//		Map<String, StateMap> memberInfoWithAccounts = service.queryObject(LEDGER_HASH, condition);
	}

}
