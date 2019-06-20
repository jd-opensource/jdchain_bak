package test.com.jd.blockchain.sdk.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.contract.samples.AssetContract;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.sdk.samples.SDKDemo_Contract;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.net.NetworkAddress;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

/**
 * 演示合约执行的过程；
 *
 * @author zhaogw
 * 2019-05-21 11:03
 */
public class SDK_Contract_Test {
	public static Logger log = LoggerFactory.getLogger(SDKDemo_Contract.class);

	public static BlockchainKeypair CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate("ED25519");
	// 账本地址；
	public static String ledgerAddress;
	private PrivKey privKey;
	private PubKey pubKey;
	BlockchainService bcsrv;
	AsymmetricKeypair signKeyPair;
	HashDigest ledgerHash;

	@Before
	public void init(){
		ledgerAddress = "j5qHcS8jG6XwpE5wXv9HYMeGTb5Fs2gQao3TPQ3irqFpQL";
		ledgerHash = getLedgerHash();
		pubKey = SDK_GateWay_KeyPair_Para.pubKey0;
		privKey = SDK_GateWay_KeyPair_Para.privkey0;
		// 使用私钥进行签名；
		signKeyPair = new BlockchainKeypair(pubKey, privKey);

		// 创建服务代理；
		final String GATEWAY_IP = "localhost";
		final int GATEWAY_PORT = 11000;
		NetworkAddress addr = new NetworkAddress(GATEWAY_IP,GATEWAY_PORT);
		GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(addr);
		bcsrv = serviceFactory.getBlockchainService();
	}

//	/**
//	 * 演示合约执行的过程；
//	 */
////	@Test
//	public void demoContract1() {
//        String dataAddress = registerData4Contract();
//		// 发起交易；
//		TransactionTemplate txTemp = bcsrv.newTransaction(ledgerHash);
//		String contractAddress = "LdeNg8JHFCKABJt6AaRNVCZPgY4ofGPd8MgcR";
//		AssetContract2 assetContract = txTemp.contract(contractAddress, AssetContract2.class);
////		assetContract.issue(transactionContentBody,contractAddress);
////        assetContract.issue(transactionContentBody,contractAddress,888888);
////        assetContract.issue(Bytes.fromString("zhaogw, contract based interface is OK!"),contractAddress,77777);
////		assetContract.issue(Bytes.fromString("zhaogw, contract based interface is OK!"),contractAddress,77777);
//		Byte byteObj = Byte.parseByte("127");
//		assetContract.issue(byteObj,dataAddress,321123);
////		assetContract.issue(contractBizContent,dataAddress);
//		assetContract.issue(Byte.parseByte("126"),dataAddress,Bytes.fromString("100.234"));
//
//		// TX 准备就绪；
//		PreparedTransaction prepTx = txTemp.prepare();
//		prepTx.sign(signKeyPair);
//		// 提交交易；
//		TransactionResponse transactionResponse = prepTx.commit();
//
//		//check;
//        KVDataEntry[] dataEntries = bcsrv.getDataEntries(ledgerHash,dataAddress,"total");
//        assertEquals("100",dataEntries[0].getValue().toString());
//	}

//	/**
//	 * 演示合约执行的过程；
//	 */
////	@Test
//	public void demoContract2() throws IOException {
//		String contractAddress = deploy();
//		String dataAddress = registerData4Contract();
//		System.out.println("dataAddress="+dataAddress);
//		// 发起交易；
//		TransactionTemplate txTemp = bcsrv.newTransaction(ledgerHash);
//
//		AssetContract2 assetContract = txTemp.contract(contractAddress, AssetContract2.class);
//		ContractBizContent contractBizContent = () -> new String[]{"param1","param2"};
//		assetContract.issue(contractBizContent,dataAddress,123456);
//
//		// TX 准备就绪；
//		PreparedTransaction prepTx = txTemp.prepare();
//		prepTx.sign(signKeyPair);
//		// 提交交易；
//		TransactionResponse transactionResponse = prepTx.commit();
//
//		//check;
//		assertTrue(transactionResponse.isSuccess());
//		KVDataEntry[] dataEntries = bcsrv.getDataEntries(ledgerHash,dataAddress,contractBizContent.getAttrs()[0],contractBizContent.getAttrs()[1]);
//		assertEquals("value1",dataEntries[0].getValue().toString());
//		assertEquals(888,dataEntries[1].getValue());
//	}

//	@Test
	public void registerData(){
		// 在本地定义 TX 模板
		TransactionTemplate txTemp = bcsrv.newTransaction(ledgerHash);
		BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
		txTemp.dataAccounts().register(dataAccount.getIdentity());

		String key1 = "jd_key1";
		String val1 = "www.jd1.com";
		String key2 = "jd_key2";
		String val2 = "www.jd2.com";
		// 定义交易,传输最简单的数字、字符串、提取合约中的地址;
		txTemp.dataAccount(dataAccount.getAddress()).setText(key1, val1, -1);
		txTemp.dataAccount(dataAccount.getAddress()).setText(key2, val2, -1);

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();
		prepTx.sign(signKeyPair);
		// 提交交易；
		TransactionResponse transactionResponse = prepTx.commit();

		assertTrue(transactionResponse.isSuccess());

		//repeat;
		String[] keys = {key1,key2};
		String[] values = {"www.jd1.com.v1","www.jd2.com.v1"};
		this.setDataInDataAddress(dataAccount.getAddress(),keys,values,0);
		String[] values2 = {"www.jd1.com.v2","www.jd2.com.v2"};
		this.setDataInDataAddress(dataAccount.getAddress(),keys,values2,1);
	}

	private String registerData4Contract(){
        // 在本地定义 TX 模板
        TransactionTemplate txTemp = bcsrv.newTransaction(ledgerHash);
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        txTemp.dataAccounts().register(dataAccount.getIdentity());
        txTemp.dataAccount(dataAccount.getAddress()).setInt64("total", 200, -1);
		txTemp.dataAccount(dataAccount.getAddress()).setText("param1", "v", -1);
		txTemp.dataAccount(dataAccount.getAddress()).setInt64("param2", 123, -1);
        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(signKeyPair);
        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();
        assertTrue(transactionResponse.isSuccess());

        return dataAccount.getAddress().toBase58();
    }

	private void setDataInDataAddress(Bytes dataAddress, String[] keys, String[] values, long version){
		// 在本地定义 TX 模板
		TransactionTemplate txTemp = bcsrv.newTransaction(ledgerHash);
		BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();

		for(int i=0; i<keys.length; i++){
			txTemp.dataAccount(dataAddress).setText(keys[i], values[i], version);
		}

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();
		prepTx.sign(signKeyPair);
		// 提交交易；
		TransactionResponse transactionResponse = prepTx.commit();
		assertTrue(transactionResponse.isSuccess());
	}

	private String deploy() throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("contract.jar");
		byte[] chainCode = this.getChainCode(classPathResource.getURL().getPath());
		TransactionTemplate txTpl = this.bcsrv.newTransaction(ledgerHash);
		BlockchainIdentity contractIdentity = BlockchainKeyGenerator.getInstance().generate().getIdentity();
		txTpl.contracts().deploy(contractIdentity, chainCode);
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(signKeyPair);
		TransactionResponse txResp = ptx.commit();
		System.out.println("contract's address=" + contractIdentity.getAddress());
		String contractAddr = contractIdentity.getAddress().toBase58();
		log.info("contractAddr="+contractAddr);
		return contractAddr;
	}

	public byte[] getChainCode(String path) {
		byte[] chainCode = null;
		File file = null;
		FileInputStream input = null;

		try {
			file = new File(path);
			input = new FileInputStream(file);
			chainCode = new byte[input.available()];
			input.read(chainCode);
		} catch (IOException var14) {
			var14.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException var13) {
				var13.printStackTrace();
			}

		}

		return chainCode;
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
		// 发起交易；
		TransactionTemplate txTemp = service.newTransaction(ledgerHash);

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
		
		AssetContract assetContract = txTemp.contract(profitDistributionContract, AssetContract.class);
		assetContract.issue(1000, receiptorAccount1);
		assetContract.transfer(receiptorAccount1, receiptorAccount2, 600);
		
//		assetContract.
		
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
		return new HashDigest(Base58Utils.decode(ledgerAddress));
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

//	@Test
//	public void testStringArr(){
//		ContractBizContent contractBizContent = () -> new String[]{"1","2","you are welcome!"};
//		byte[] bizBytes = BinaryProtocol.encode(contractBizContent,ContractBizContent.class);
//		ContractBizContent actualObj = BinaryProtocol.decodeAs(bizBytes,ContractBizContent.class);
//		assertArrayEquals(contractBizContent.getAttrs(),actualObj.getAttrs());
//	}

//	@Test
//	public void testContractArgs(){
//		ContractBizContent contractBizContent = () -> new String[]{"param1"};
//		Method method = ReflectionUtils.findMethod(AssetContract2.class,"issue",ContractBizContent.class,String.class);
//		ContractArgs contractArgs = new ContractArgs() {
//			@Override
//			public Method getMethod() {
//				return method;
//			}
//
//			@Override
//			public Object[] getArgs() {
//				return new Object[]{contractBizContent,"hello"};
//			}
//		};
//
//		//add the annotation;
//
//	}
}
