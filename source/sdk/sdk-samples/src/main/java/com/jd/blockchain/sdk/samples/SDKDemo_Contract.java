package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.net.NetworkAddress;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

/**
 * 演示合约执行的过程；
 *
 * @author huanghaiquan
 *
 */
public class SDKDemo_Contract {

	public static BlockchainKeyPair CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate("ED25519");

	/**
	 * 演示合约执行的过程；
	 */
	public static void demoContract() {
		// 账本地址；
		String ledgerAddress = "ffkjhkeqwiuhivnsh3298josijdocaijsda==";
		// 节点地址列表；
		NetworkAddress[] peerAddrs = { new NetworkAddress("192.168.10.10", 8080),
				new NetworkAddress("192.168.10.11", 8080), new NetworkAddress("192.168.10.12", 8080),
				new NetworkAddress("192.168.10.13", 8080) };

		// 网关客户端编号；
		int gatewayId = 1001;
		// 客户端的认证账户；
		// String clientAddress = "kkjsafieweqEkadsfaslkdslkae998232jojf==";
		// String privKey = "safefsd32q34vdsvs";

		// 创建服务代理；
		final String GATEWAY_IP = "127.0.0.1";
		final int GATEWAY_PORT = 80;
		final boolean SECURE = false;
		GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE,
				CLIENT_CERT);
		BlockchainService service = serviceFactory.getBlockchainService();

		HashDigest ledgerHash = getLedgerHash();
		// 发起交易；
		TransactionTemplate txTemp = service.newTransaction(ledgerHash);

		// --------------------------------------
		// 一个贸易账户，贸易结算后的利润将通过一个合约账户来执行利润分配；
		// 合约账户被设置为通用的账户，不具备对贸易结算账户的直接权限；
		// 只有当前交易发起人具备对贸易账户的直接权限，当交易发起人对交易进行签名之后，权限被间接传递给合约账户；
		String commerceAccount = "GGhhreGeasdfasfUUfehf9932lkae99ds66jf==";
		// 处理利润分成的通用业务逻辑的合约账户；
		String profitDistributionContract = "AAdfe4346fHhefe34fwf343kaeER4678RT==";

		// 收益人账户；
		String receiptorAccount1 = "MMMEy902jkjjJJDkshreGeasdfassdfajjf==";
		String receiptorAccount2 = "Kjfe8832hfa9jjjJJDkshrFjksjdlkfj93F==";
		// 资产编码；
		String assetKey = "RMB-ASSET";
		// 此次待分配利润；
		long profit = 1000000;

		// 备注信息；
		Remark remark = new Remark();
		String remarkJSON = JSONSerializeUtils.serializeToJSON(remark);

		// 合约代码的参数表；
		ByteArray[] args = {};
		// 调用合约代码的分配操作；
		// txTemp.deployContract().deploy(identity, appByteCodes);

		// todo args暂时无数据，尚未确定填入什么
		txTemp.contractEvents().send(commerceAccount, "trans-asset", null);
//		txTemp.invokeContract().send(commerceAccount, "trans-asset", args);
		// --------------------------------------

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();
		String txHash = ByteArray.toBase64(prepTx.getHash().toBytes());

		// 使用私钥进行签名；
		AsymmetricKeypair keyPair = getSponsorKey();
		prepTx.sign(keyPair);

		// 提交交易；
		prepTx.commit();
	}

	private static HashDigest getLedgerHash() {
		// TODO Init ledger hash;
		return null;
	}

	/**
	 * 交易发起人的私钥；<br>
	 *
	 * 注：私钥由调用方在本地保管和使用；
	 *
	 * @return
	 */
	private static AsymmetricKeypair getSponsorKey() {
		SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction("ED25519");
		return signatureFunction.generateKeypair();
	}

	/**
	 * 商品信息；
	 *
	 * @author huanghaiquan
	 *
	 */
	public static class Remark {

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
