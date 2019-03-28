package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.impl.AsymmtricCryptographyImpl;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainTransactionService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.net.NetworkAddress;

public class SDKDemo_User {

	public static BlockchainKeyPair CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate();

	public static AsymmetricCryptography asymmetricCryptography = new AsymmtricCryptographyImpl();

	/**
	 * 生成一个区块链用户，并注册到区块链；
	 */
	public static void registerUser() {
		// 区块链共识域；
		String realm = "SUPPLY_CHAIN_ALLIANCE";
		// 节点地址列表；
		NetworkAddress[] peerAddrs = { new NetworkAddress("192.168.10.10", 8080),
				new NetworkAddress("192.168.10.11", 8080), new NetworkAddress("192.168.10.12", 8080),
				new NetworkAddress("192.168.10.13", 8080) };

		// 网关客户端编号；
		int gatewayId = 1001;
		// 账本地址；
		String ledgerAddress = "ffkjhkeqwiuhivnsh3298josijdocaijsda==";
		// 客户端的认证账户；
		String clientAddress = "kkjsafieweqEkadsfaslkdslkae998232jojf==";
		String privKey = "safefsd32q34vdsvs";
		// 创建服务代理；
		final String GATEWAY_IP = "127.0.0.1";
		final int GATEWAY_PORT = 80;
		final boolean SECURE = false;
		GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE,
				CLIENT_CERT);
		BlockchainTransactionService service = serviceFactory.getBlockchainService();

		HashDigest ledgerHash = getLedgerHash();

		// 在本地定义注册账号的 TX；
		TransactionTemplate txTemp = service.newTransaction(ledgerHash);

		// --------------------------------------
		// 配置账户的权限；
		String walletAccount = "Kjfe8832hfa9jjjJJDkshrFjksjdlkfj93F==";
		String user1 = "MMMEy902jkjjJJDkshreGeasdfassdfajjf==";
		String user2 = "Kjfe8832hfa9jjjJJDkshrFjksjdlkfj93F==";
		// 配置:
		// “状态数据的写入权限”的阈值为 100；
		// 需要 user1、user2 两个账户的联合签名才能写入；
		// 当前账户仅用于表示一个业务钱包，禁止自身的写入权限，只能由业务角色的账户才能操作；

		String userPubKeyStr = "Kjfe8832hfa9jjjJJDkshrFjksjdlkfj93F==";

		// 在本地产生要注册的账户的秘钥；
		//BlockchainKeyGenerator generator = BlockchainKeyGenerator.getInstance();
		//BlockchainKeyPair user = generator.generate(CryptoKeyType.PUB_KEY);

        SignatureFunction signatureFunction = asymmetricCryptography.getSignatureFunction(CryptoAlgorithm.ED25519);
		CryptoKeyPair cryptoKeyPair = signatureFunction.generateKeyPair();
		BlockchainKeyPair user = new BlockchainKeyPair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());

		txTemp.users().register(user.getIdentity());

		// --------------------------------------

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();

		// 使用私钥进行签名；
		CryptoKeyPair keyPair = getSponsorKey();
		prepTx.sign(keyPair);

		// 提交交易；
		prepTx.commit();
	}

	private static HashDigest getLedgerHash() {
		// TODO Init ledger hash;
		return null;
	}

	private static CryptoKeyPair getSponsorKey() {
		SignatureFunction signatureFunction = asymmetricCryptography.getSignatureFunction(CryptoAlgorithm.ED25519);
		return signatureFunction.generateKeyPair();
	}

}
