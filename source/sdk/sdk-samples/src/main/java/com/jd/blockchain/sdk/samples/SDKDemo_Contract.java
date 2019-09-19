package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.contract.samples.AssetContract;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.transaction.ContractReturnValue;
import com.jd.blockchain.transaction.LongValueHolder;
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

	public static BlockchainKeypair CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate("ED25519");

	public static void main(String[] args) {
		demoContract();
	}

	/**
	 * 演示合约执行的过程；
	 */
	public static void demoContract() {
		// 账本地址；
		String ledgerAddress = "j5rpuGWVxSuUbU3gK7MDREfui797AjfdHzvAMiSaSzydu7";
		// 节点地址列表；
//		NetworkAddress[] peerAddrs = { new NetworkAddress("192.168.10.10", 8080),
//				new NetworkAddress("192.168.10.11", 8080), new NetworkAddress("192.168.10.12", 8080),
//				new NetworkAddress("192.168.10.13", 8080) };

		// 创建服务代理；
		final String GATEWAY_IP = "localhost";
		final int GATEWAY_PORT = 11000;
		final boolean SECURE = false;
		GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE,
				CLIENT_CERT);
		BlockchainService service = serviceFactory.getBlockchainService();

		HashDigest ledgerHash = getLedgerHash();

		// --------------------------------------
		// 一个贸易账户，贸易结算后的利润将通过一个合约账户来执行利润分配；
		// 合约账户被设置为通用的账户，不具备对贸易结算账户的直接权限；
		// 只有当前交易发起人具备对贸易账户的直接权限，当交易发起人对交易进行签名之后，权限被间接传递给合约账户；
		String commerceAccount = "LdeP13gKE6319LvYPyWAT4UXr2brvpitPRBN1";
		// 处理利润分成的通用业务逻辑的合约账户；
		String profitDistributionContract = "LdeP13gKE6319LvYPyWAT4UXr2brvpitPRBN1";

		// 收益人账户；
		String receiptorAccount1 = "LdeP13gKE6319LvYPyWAT4UXr2brvpitPRBN1";
		String receiptorAccount2 = "LdeP13gKE6319LvYPyWAT4UXr2brvpitPRBN1";
		// 资产编码；
		String assetKey = "RMB-ASSET";
		// 此次待分配利润；
		long profit = 1000000;

		// 备注信息；
		Remark remark = new Remark();
		String remarkJSON = JSONSerializeUtils.serializeToJSON(remark);

		// 发起交易；
		TransactionTemplate txTemp = service.newTransaction(ledgerHash);

		AssetContract assetContract = txTemp.contract(profitDistributionContract, AssetContract.class);
		assetContract.issue(1000, receiptorAccount1);
		LongValueHolder balance = ContractReturnValue.decode(assetContract.transfer(receiptorAccount1, receiptorAccount2, 600));

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();

		// 使用私钥进行签名；
		AsymmetricKeypair keyPair = getSponsorKey();//示例方法，取发起人的私钥；
		prepTx.sign(keyPair);

		// 提交交易；
		prepTx.commit();
		
		//获取返回值；
		System.out.println("balance = " + balance.get());
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
		SignatureFunction signatureFunction = Crypto.getSignatureFunction("ED25519");
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
