package test.com.jd.blockchain.intgr;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.gateway.GatewayConfigProperties.KeyPairConfig;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInfo;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.ledger.core.DataAccount;
import com.jd.blockchain.ledger.core.DataAccountQuery;
import com.jd.blockchain.ledger.core.LedgerManage;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.codec.HexUtils;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.contract.AssetContract;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeWeb4SingleStepsTest;

public class IntegrationTestAll4Redis {

	public static final String PASSWORD = "abc";

	public static final String[] PUB_KEYS = { "3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9",
			"3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX",
			"3snPdw7i7PZi6TStiyc6mzjprnNhgs2atSGNS8wPYzhbKaUWGFJt7x",
			"3snPdw7i7PifPuRX7fu3jBjsb3rJRfDe9GtbDfvFJaJ4V4hHXQfhwk" };

	public static final String[] PRIV_KEYS = {
			"177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x",
			"177gju9p5zrNdHJVEQnEEKF4ZjDDYmAXyfG84V5RPGVc5xFfmtwnHA7j51nyNLUFffzz5UT",
			"177gjtwLgmSx5v1hFb46ijh7L9kdbKUpJYqdKVf9afiEmAuLgo8Rck9yu5UuUcHknWJuWaF",
			"177gk1pudweTq5zgJTh8y3ENCTwtSFsKyX7YnpuKPo7rKgCkCBXVXh5z2syaTCPEMbuWRns" };

	// batch transactions keys
	BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
	BlockchainKeypair dataKey = BlockchainKeyGenerator.getInstance().generate();

	// 合约测试使用的初始化数据;
	BlockchainKeypair contractDataKey = BlockchainKeyGenerator.getInstance().generate();
	BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
	private String contractZipName = "AssetContract1.contract";
	private String eventName = "issue-asset";
	HashDigest txContentHash;
	String pubKeyVal = "jd.com" + System.currentTimeMillis();
	// String userPubKeyVal = "this is user's pubKey";
	// 保存资产总数的键；
	private static final String KEY_TOTAL = "TOTAL";
	// 第二个参数;
	private static final String KEY_ABC = "abc";

	@Test
	public void test() {

		NetworkAddress peerSrvAddr0 = new NetworkAddress("127.0.0.1", 10200);
		LedgerBindingConfig bindingConfig0 = loadBindingConfig(0);
		PeerTestRunner peer0 = new PeerTestRunner(peerSrvAddr0, bindingConfig0);

		NetworkAddress peerSrvAddr1 = new NetworkAddress("127.0.0.1", 10210);
		LedgerBindingConfig bindingConfig1 = loadBindingConfig(1);
		PeerTestRunner peer1 = new PeerTestRunner(peerSrvAddr1, bindingConfig1);

		NetworkAddress peerSrvAddr2 = new NetworkAddress("127.0.0.1", 10220);
		LedgerBindingConfig bindingConfig2 = loadBindingConfig(2);
		PeerTestRunner peer2 = new PeerTestRunner(peerSrvAddr2, bindingConfig2);

		NetworkAddress peerSrvAddr3 = new NetworkAddress("127.0.0.1", 10230);
		LedgerBindingConfig bindingConfig3 = loadBindingConfig(3);
		PeerTestRunner peer3 = new PeerTestRunner(peerSrvAddr3, bindingConfig3);

		AsyncCallback<Object> peerStarting0 = peer0.start();
		AsyncCallback<Object> peerStarting1 = peer1.start();
		AsyncCallback<Object> peerStarting2 = peer2.start();
		AsyncCallback<Object> peerStarting3 = peer3.start();

		peerStarting0.waitReturn();
		peerStarting1.waitReturn();
		peerStarting2.waitReturn();
		peerStarting3.waitReturn();

		DbConnectionFactory dbConnectionFactory0 = peer0.getDBConnectionFactory();
		DbConnectionFactory dbConnectionFactory1 = peer1.getDBConnectionFactory();
		DbConnectionFactory dbConnectionFactory2 = peer2.getDBConnectionFactory();
		DbConnectionFactory dbConnectionFactory3 = peer3.getDBConnectionFactory();

		String encodedBase58Pwd = KeyGenUtils.encodePasswordAsBase58(LedgerInitializeWeb4SingleStepsTest.PASSWORD);

		KeyPairConfig gwkey0 = new KeyPairConfig();
		gwkey0.setPubKeyValue(PUB_KEYS[0]);
		gwkey0.setPrivKeyValue(PRIV_KEYS[0]);
		gwkey0.setPrivKeyPassword(encodedBase58Pwd);
		GatewayTestRunner gateway0 = new GatewayTestRunner("127.0.0.1", 11000, gwkey0, peerSrvAddr0);

		AsyncCallback<Object> gwStarting0 = gateway0.start();

		gwStarting0.waitReturn();

		// 执行测试用例之前，校验每个节点的一致性；
		LedgerQuery[] ledgers = buildLedgers(
				new LedgerBindingConfig[] { bindingConfig0, bindingConfig1, bindingConfig2, bindingConfig3 },
				new DbConnectionFactory[] { dbConnectionFactory0, dbConnectionFactory1, dbConnectionFactory2,
						dbConnectionFactory3 });
		testConsistencyAmongNodes(ledgers);

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD);
		PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[1], PASSWORD);
		PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[2], PASSWORD);
		PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[3], PASSWORD);

		PubKey pubKey0 = KeyGenUtils.decodePubKey(PUB_KEYS[0]);
		PubKey pubKey1 = KeyGenUtils.decodePubKey(PUB_KEYS[1]);
		PubKey pubKey2 = KeyGenUtils.decodePubKey(PUB_KEYS[2]);
		PubKey pubKey3 = KeyGenUtils.decodePubKey(PUB_KEYS[3]);

		AsymmetricKeypair adminKey = new AsymmetricKeypair(pubKey0, privkey0);

		testWriteBatchTransactions(gateway0, adminKey, ledgers[0]);

		testSDK(gateway0, adminKey, ledgers[0]);

		// 执行测试用例之后，校验每个节点的一致性；
		testConsistencyAmongNodes(ledgers);
	}

	private LedgerBindingConfig loadBindingConfig(int id) {
		ClassPathResource res = new ClassPathResource("ledger-binding-redis-" + id + ".conf");
		try (InputStream in = res.getInputStream()) {
			return LedgerBindingConfig.resolve(in);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private LedgerQuery[] buildLedgers(LedgerBindingConfig[] bindingConfigs,
			DbConnectionFactory[] dbConnectionFactories) {
		int[] ids = { 0, 1, 2, 3 };
		LedgerQuery[] ledgers = new LedgerQuery[ids.length];
		LedgerManager[] ledgerManagers = new LedgerManager[ids.length];
		for (int i = 0; i < ids.length; i++) {
			ledgerManagers[i] = new LedgerManager();
			HashDigest ledgerHash = bindingConfigs[0].getLedgerHashs()[0];
			DbConnection conn = dbConnectionFactories[i].connect(
					bindingConfigs[i].getLedger(ledgerHash).getDbConnection().getUri(),
					bindingConfigs[i].getLedger(ledgerHash).getDbConnection().getPassword());
			ledgers[i] = ledgerManagers[i].register(ledgerHash, conn.getStorageService());
		}
		return ledgers;
	}

	private void testConsistencyAmongNodes(LedgerQuery[] ledgers) {
		LedgerQuery ledger0 = ledgers[0];
		LedgerBlock latestBlock0 = ledger0.retrieveLatestBlock();
		for (int i = 1; i < ledgers.length; i++) {
			LedgerQuery otherLedger = ledgers[i];
			LedgerBlock otherLatestBlock = otherLedger.retrieveLatestBlock();
			assertEquals(ledger0.getHash(), otherLedger.getHash());
			assertEquals(ledger0.getLatestBlockHeight(), otherLedger.getLatestBlockHeight());
			assertEquals(latestBlock0.getHeight(), otherLatestBlock.getHeight());
			assertEquals(latestBlock0.getAdminAccountHash(), otherLatestBlock.getAdminAccountHash());
			assertEquals(latestBlock0.getUserAccountSetHash(), otherLatestBlock.getUserAccountSetHash());
			assertEquals(latestBlock0.getDataAccountSetHash(), otherLatestBlock.getDataAccountSetHash());
			assertEquals(latestBlock0.getContractAccountSetHash(), otherLatestBlock.getContractAccountSetHash());
			assertEquals(latestBlock0.getPreviousHash(), otherLatestBlock.getPreviousHash());

			assertEquals(latestBlock0.getTransactionSetHash(), otherLatestBlock.getTransactionSetHash());
			assertEquals(ledger0.getLatestBlockHash(), otherLedger.getLatestBlockHash());
			assertEquals(latestBlock0.getHash(), otherLatestBlock.getHash());
		}
	}

	// 测试一个区块包含多个交易的写入情况，并验证写入结果；
	private void testWriteBatchTransactions(GatewayTestRunner gateway, AsymmetricKeypair adminKey,
			LedgerQuery ledgerRepository) {
		// 连接网关；
		GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway.getServiceAddress());
		BlockchainService blockchainService = gwsrvFact.getBlockchainService();

		HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHashs[0]);

		// regist user account
		txTpl.users().register(userKey.getIdentity());

		// regist data account
		txTpl.dataAccounts().register(dataKey.getIdentity());

		// add kv ops for data account
		DataAccountKVSetOperation dataKvsetOP = txTpl.dataAccount(dataKey.getAddress()).setText("A", "Value_A_0", -1)
				.setText("B", "Value_B_0", -1).setText("C", "Value_C_0", -1).setText("D", "Value_D_0", -1)
				.getOperation();

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		assertTrue(txResp.isSuccess());
		assertEquals(ledgerRepository.retrieveLatestBlockHeight(), txResp.getBlockHeight());

		assertEquals("Value_A_0", ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock())
				.getAccount(dataKey.getAddress()).getDataset().getValue("A").getBytes().toUTF8String());
		assertEquals("Value_B_0", ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock())
				.getAccount(dataKey.getAddress()).getDataset().getValue("B").getBytes().toUTF8String());
		assertEquals("Value_C_0", ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock())
				.getAccount(dataKey.getAddress()).getDataset().getValue("C").getBytes().toUTF8String());
		assertEquals("Value_D_0", ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock())
				.getAccount(dataKey.getAddress()).getDataset().getValue("D").getBytes().toUTF8String());
		assertEquals(0, ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock())
				.getAccount(dataKey.getAddress()).getDataset().getVersion("A"));
		assertEquals(0, ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock())
				.getAccount(dataKey.getAddress()).getDataset().getVersion("B"));
		assertEquals(0, ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock())
				.getAccount(dataKey.getAddress()).getDataset().getVersion("C"));
		assertEquals(0, ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock())
				.getAccount(dataKey.getAddress()).getDataset().getVersion("D"));

		return;
	}

	private void testSDK(GatewayTestRunner gateway, AsymmetricKeypair adminKey, LedgerQuery ledgerRepository) {
		// 连接网关；
		GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway.getServiceAddress());
		BlockchainService bcsrv = gwsrvFact.getBlockchainService();

		HashDigest[] ledgerHashs = bcsrv.getLedgerHashs();
		BlockchainKeypair newUserAcount = testSDK_RegisterUser(adminKey, ledgerHashs[0], bcsrv, ledgerRepository);
		BlockchainKeypair newDataAccount = testSDK_RegisterDataAccount(adminKey, ledgerHashs[0], bcsrv,
				ledgerRepository);
		testSDK_InsertData(adminKey, ledgerHashs[0], bcsrv, newDataAccount.getAddress(), ledgerRepository);
		LedgerBlock latestBlock = testSDK_Contract(adminKey, ledgerHashs[0], bcsrv, ledgerRepository);

	}

	private void testSDK_InsertData(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService, Bytes dataAccountAddress, LedgerQuery ledgerRepository) {

		// 在本地定义注册账号的 TX；
		TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

		// --------------------------------------
		// 将商品信息写入到指定的账户中；
		// 对象将被序列化为 JSON 形式存储，并基于 JSON 结构建立查询索引；
		Bytes dataAccount = dataAccountAddress;

		String dataKey = "jingdong" + new Random().nextInt(100000);
		String dataVal = "www.jd.com";

		txTemp.dataAccount(dataAccount).setText(dataKey, dataVal, -1);

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();

		// 使用私钥进行签名；
		prepTx.sign(adminKey);

		// 提交交易；
		TransactionResponse txResp = prepTx.commit();

		ledgerRepository.retrieveLatestBlock(); // 更新内存

		// 先验证应答
		assertEquals(TransactionState.SUCCESS, txResp.getExecutionState());
		assertEquals(txResp.getBlockHeight(), ledgerRepository.getLatestBlockHeight());
		assertEquals(txResp.getContentHash(), prepTx.getHash());
		assertEquals(txResp.getBlockHash(), ledgerRepository.getLatestBlockHash());

		TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, dataAccountAddress.toString(),
				dataKey);
		for (TypedKVEntry kvDataEntry : kvDataEntries) {
			assertEquals(dataKey, kvDataEntry.getKey());
			String valHexText = (String) kvDataEntry.getValue();
			byte[] valBytes = HexUtils.decode(valHexText);
			String valText = new String(valBytes);
			System.out.println(valText);
		}
	}

	private BlockchainKeypair testSDK_RegisterDataAccount(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService, LedgerQuery ledgerRepository) {
		// 注册数据账户，并验证最终写入；
		BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();

		// 定义交易；
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.dataAccounts().register(dataAccount.getIdentity());

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();

		HashDigest transactionHash = ptx.getHash();

		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		// 验证结果;
		// LedgerRepository ledgerOfNode0 =
		// node0.getLedgerManager().getLedger(ledgerHash);
		LedgerManage ledgerManager = new LedgerManager();
		long latestBlockHeight = ledgerRepository.retrieveLatestBlockHeight();

		assertEquals(txResp.getExecutionState(), TransactionState.SUCCESS);
		assertEquals(txResp.getBlockHeight(), latestBlockHeight);
		assertEquals(txResp.getContentHash(), transactionHash);
		assertEquals(txResp.getBlockHash(), ledgerRepository.getLatestBlockHash());
		assertNotNull(ledgerRepository.getDataAccountSet(ledgerRepository.getLatestBlock())
				.getAccount(dataAccount.getAddress()));

		return dataAccount;
	}

	private BlockchainKeypair testSDK_RegisterUser(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService, LedgerQuery ledgerRepository) {
		// 注册用户，并验证最终写入；
		BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();

		// 定义交易；
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.users().register(user.getIdentity());

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();

		HashDigest transactionHash = ptx.getHash();

		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		// 验证结果;
		LedgerManage ledgerManager = new LedgerManager();
		assertEquals(txResp.getExecutionState(), TransactionState.SUCCESS);
		assertEquals(txResp.getBlockHeight(), ledgerRepository.getLatestBlockHeight());
		assertEquals(txResp.getContentHash(), transactionHash);
		assertEquals(txResp.getBlockHash(), ledgerRepository.getLatestBlockHash());
		assertTrue(ledgerRepository.getUserAccountSet(ledgerRepository.getLatestBlock()).contains(user.getAddress()));

		return user;
	}

	public static LedgerInitProperties loadInitSetting_integration() {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger_init_test_integration.init");
		try (InputStream in = ledgerInitSettingResource.getInputStream()) {
			LedgerInitProperties setting = LedgerInitProperties.resolve(in);
			return setting;
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private LedgerBlock testSDK_Contract(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService, LedgerQuery ledgerRepository) {
		System.out.println("adminKey=" + AddressEncoding.generateAddress(adminKey.getPubKey()));
		BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
		System.out.println("userKey=" + userKey.getAddress());
		// valid the basic data in contract;
		// prepareContractData(adminKey, ledgerHash,
		// blockchainService,ledgerRepository);

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.users().register(userKey.getIdentity());

		// 定义交易；
		// 注册数据账户，并验证最终写入；
		txTpl.dataAccounts().register(contractDataKey.getIdentity());
		// dataAccountSet.getDataAccount(dataAddress)
		DataAccount dataAccount = ledgerRepository.getDataAccountSet(ledgerRepository.getLatestBlock())
				.getAccount(contractDataKey.getAddress());

		DataAccountKVSetOperation kvsetOP = txTpl.dataAccount(contractDataKey.getAddress())
				.setText("A", "Value_A_0", -1).setText("B", "Value_B_0", -1)
				.setText(KEY_TOTAL, "total value,dataAccount", -1).setText(KEY_ABC, "abc value,dataAccount", -1)
				// 所有的模拟数据都在这个dataAccount中填充;
				.setBytes("ledgerHash", ledgerHash.getRawDigest(), -1).getOperation();

		byte[] contractCode = getChainCodeBytes();
		txTpl.contracts().deploy(contractDeployKey.getIdentity(), contractCode);

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();
		assertTrue(txResp.isSuccess());

		// 验证结果；
		txResp.getContentHash();

		LedgerBlock block = ledgerRepository.getBlock(txResp.getBlockHeight());
		byte[] contractCodeInDb = ledgerRepository.getContractAccountSet(block)
				.getAccount(contractDeployKey.getAddress()).getChainCode();
		assertArrayEquals(contractCode, contractCodeInDb);
		txContentHash = ptx.getHash();

		// execute the contract;
		testContractExe(adminKey, ledgerHash, userKey, blockchainService, ledgerRepository);

		return block;
	}

	private void testContractExe(AsymmetricKeypair adminKey, HashDigest ledgerHash, BlockchainKeypair userKey,
			BlockchainService blockchainService, LedgerQuery ledgerRepository) {
		LedgerInfo ledgerInfo = blockchainService.getLedger(ledgerHash);
		LedgerBlock previousBlock = blockchainService.getBlock(ledgerHash, ledgerInfo.getLatestBlockHeight() - 1);

		// 定义交易；
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		txTpl.contract(contractDeployKey.getAddress(), AssetContract.class).issue(10,"abc");

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		// 验证结果；
		txResp.getContentHash();

		LedgerInfo latestLedgerInfo = blockchainService.getLedger(ledgerHash);
		assertEquals(ledgerInfo.getLatestBlockHeight() + 1, latestLedgerInfo.getLatestBlockHeight());
		assertEquals(txResp.getBlockHeight(), latestLedgerInfo.getLatestBlockHeight());

		LedgerBlock backgroundLedgerBlock = ledgerRepository.retrieveLatestBlock();
		assertEquals(txResp.getBlockHeight(), backgroundLedgerBlock.getHeight());

		// 验证合约中的赋值，外部可以获得;
		DataAccountQuery dataAccountSet = ledgerRepository.getDataAccountSet(backgroundLedgerBlock);
		AsymmetricKeypair key = Crypto.getSignatureFunction("ED25519").generateKeypair();
		PubKey pubKey = key.getPubKey();
		Bytes dataAddress = AddressEncoding.generateAddress(pubKey);
		assertEquals(dataAddress, dataAccountSet.getAccount(dataAddress).getID().getAddress());
		assertEquals("hello",
				dataAccountSet.getAccount(dataAddress).getDataset().getValue(KEY_TOTAL, -1).getBytes().toUTF8String());

		// 验证userAccount，从合约内部赋值，然后外部验证;内部定义动态key，外部不便于得到，临时屏蔽;
		// UserAccountSet userAccountSet =
		// ledgerRepository.getUserAccountSet(backgroundLedgerBlock);
		// PubKey userPubKey = new PubKey(CryptoAlgorithm.ED25519,
		// userPubKeyVal.getBytes());
		// String userAddress = AddressEncoding.generateAddress(userPubKey);
		// assertEquals(userAddress, userAccountSet.getUser(userAddress).getAddress());
	}

	private void prepareContractData(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService, LedgerQuery ledgerRepository) {

		// 定义交易；
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();
		assertTrue(txResp.isSuccess());

		// 验证结果；
		LedgerBlock block = ledgerRepository.getBlock(txResp.getBlockHeight());
		BytesValue val1InDb = ledgerRepository.getDataAccountSet(block).getAccount(contractDataKey.getAddress())
				.getDataset().getValue("A");
		BytesValue val2InDb = ledgerRepository.getDataAccountSet(block).getAccount(contractDataKey.getAddress())
				.getDataset().getValue(KEY_TOTAL);
		assertEquals("Value_A_0", val1InDb.getBytes().toUTF8String());
		assertEquals("total value,dataAccount", val2InDb.getBytes().toUTF8String());
	}

	/**
	 * 根据合约构建字节数组;
	 *
	 * @return
	 */
	private byte[] getChainCodeBytes() {
		// 构建合约的字节数组;
		byte[] contractCode = null;
		File file = null;
		InputStream input = null;
		try {
			ClassPathResource contractPath = new ClassPathResource(contractZipName);
			file = new File(contractPath.getURI());
			assertTrue("contract zip file is not exist.", file.exists() == true);
			input = new FileInputStream(file);
			// 这种暴力的读取压缩包，在class解析时有问题，所有需要改进;
			contractCode = new byte[input.available()];
			input.read(contractCode);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return contractCode;
	}
}
