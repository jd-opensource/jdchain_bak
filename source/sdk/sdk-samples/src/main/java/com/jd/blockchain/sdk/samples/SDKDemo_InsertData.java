package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * 演示数据写入的调用过程；
 * 
 * @author huanghaiquan
 *
 */
public class SDKDemo_InsertData {

	public static BlockchainKeyPair CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate("ED25519");


	/**
	 * 演示数据写入的调用过程；
	 */
	public static void insertData() {
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
		BlockchainService service = serviceFactory.getBlockchainService();

		HashDigest ledgerHash = getLedgerHash();
		// 在本地定义注册账号的 TX；
		TransactionTemplate txTemp = service.newTransaction(ledgerHash);

		// --------------------------------------
		// 将商品信息写入到指定的账户中；
		// 对象将被序列化为 JSON 形式存储，并基于 JSON 结构建立查询索引；
		String commodityDataAccount = "GGhhreGeasdfasfUUfehf9932lkae99ds66jf==";
		Commodity commodity1 = new Commodity();
		txTemp.dataAccount(commodityDataAccount).set("ASSET_CODE", commodity1.getCode().getBytes(), -1);

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();

		String txHash = ByteArray.toBase64(prepTx.getHash().toBytes());

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
		SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction("ED25519");
		return signatureFunction.generateKeyPair();
	}

	/**
	 * 商品信息；
	 * 
	 * @author huanghaiquan
	 *
	 */
	public static class Commodity {

		private String code;

		private String name;

		private String venderAddress;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getVenderAddress() {
			return venderAddress;
		}

		public void setVenderAddress(String venderAddress) {
			this.venderAddress = venderAddress;
		}

	}

}
