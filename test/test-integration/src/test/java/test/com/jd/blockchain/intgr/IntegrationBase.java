/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.intgr.perf.IntegrationBase
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/25 下午3:40
 * Description:
 */
package test.com.jd.blockchain.intgr;

import static com.jd.blockchain.transaction.ContractReturnValue.decode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.binaryproto.DataContractRegistry;
//import com.jd.blockchain.contract.ReadContract;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/25
 * @since 1.0.0
 */

public class IntegrationBase {
    public static String KEY_TOTAL = "total";

	static {
		DataContractRegistry.register(LedgerInitOperation.class);
		DataContractRegistry.register(UserRegisterOperation.class);
	}

	public static final String PASSWORD = "abc";

	public static final String[] PUB_KEYS = {
			"3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9",
			"3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX",
			"3snPdw7i7PZi6TStiyc6mzjprnNhgs2atSGNS8wPYzhbKaUWGFJt7x",
			"3snPdw7i7PifPuRX7fu3jBjsb3rJRfDe9GtbDfvFJaJ4V4hHXQfhwk" };

	public static final String[] PRIV_KEYS = {
			"177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x",
			"177gju9p5zrNdHJVEQnEEKF4ZjDDYmAXyfG84V5RPGVc5xFfmtwnHA7j51nyNLUFffzz5UT",
			"177gjtwLgmSx5v1hFb46ijh7L9kdbKUpJYqdKVf9afiEmAuLgo8Rck9yu5UuUcHknWJuWaF",
			"177gk1pudweTq5zgJTh8y3ENCTwtSFsKyX7YnpuKPo7rKgCkCBXVXh5z2syaTCPEMbuWRns" };

	public static final AtomicLong validLong = new AtomicLong();

	public static KeyPairResponse testSDK_RegisterUser(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService) {
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

		KeyPairResponse keyPairResponse = new KeyPairResponse();
		keyPairResponse.keyPair = user;
		keyPairResponse.txResp = txResp;
		keyPairResponse.txHash = transactionHash;
		return keyPairResponse;
	}

	public static KeyPairResponse testSDK_BlockFullRollBack(AsymmetricKeypair adminKey, HashDigest ledgerHash,
															  BlockchainService blockchainService) {

		BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		//Register user account
		txTpl.users().register(user.getIdentity());

		PreparedTransaction prepTx = txTpl.prepare();

		HashDigest transactionHash = prepTx.getHash();

		prepTx.sign(adminKey);

		//Commit transaction
		TransactionResponse transactionResponse = prepTx.commit();

		//The whole block will rollback, due to storage error
		assertEquals(transactionResponse.getExecutionState().CODE, TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK.CODE);

		KeyPairResponse keyPairResponse = new KeyPairResponse();
		keyPairResponse.keyPair = user;
		keyPairResponse.txResp = transactionResponse;
		keyPairResponse.txHash = transactionHash;
		return keyPairResponse;
	}


	public static KeyPairResponse testSDK_RegisterDataAccount(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService) {
		// 注册数据账户，并验证最终写入；
		BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();

		// 定义交易；
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.dataAccounts().register(dataAccount.getIdentity());
//        txTpl.dataAccount(dataAccount.getAddress()).setInt64("total", 200, -1);
//        txTpl.dataAccount(dataAccount.getAddress()).set("param1", "v", -1);
//        txTpl.dataAccount(dataAccount.getAddress()).set("param2", 200, -1);

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();

		HashDigest transactionHash = ptx.getHash();

		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		KeyPairResponse keyPairResponse = new KeyPairResponse();
		keyPairResponse.keyPair = dataAccount;
		keyPairResponse.txResp = txResp;
		keyPairResponse.txHash = transactionHash;
		return keyPairResponse;
	}

	public static KvResponse testSDK_InsertData(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService, Bytes dataAccount) {

		// 在本地定义注册账号的 TX；
		TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

		// --------------------------------------
		// 将商品信息写入到指定的账户中；
		// 对象将被序列化为 JSON 形式存储，并基于 JSON 结构建立查询索引；
		String dataKey = "jingdong" + System.currentTimeMillis() + new Random().nextInt(100000);
		String dataVal = "www.jd.com";

		txTemp.dataAccount(dataAccount).setText(dataKey, dataVal, -1);

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();

		HashDigest transactionHash = prepTx.getHash();

		// 使用私钥进行签名；
		prepTx.sign(adminKey);

		// 提交交易；
		TransactionResponse txResp = prepTx.commit();

		KvResponse kvResponse = new KvResponse();
		kvResponse.ledgerHash = ledgerHash;
		kvResponse.dataAccount = dataAccount;
		kvResponse.txResp = txResp;
		kvResponse.txHash = transactionHash;
		kvResponse.key = dataKey;
		kvResponse.value = dataVal;
		return kvResponse;
	}

	public static KeyPairResponse testSDK_RegisterParticipant(AsymmetricKeypair adminKey, HashDigest ledgerHash,
															  BlockchainService blockchainService) {
		// 注册参与方，并验证最终写入；
		BlockchainKeypair participant = BlockchainKeyGenerator.getInstance().generate();

		// 定义交易；
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		txTpl.participants().register("peer4", new BlockchainIdentityData(participant.getPubKey()), new NetworkAddress("127.0.0.1", 20000));

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();

		HashDigest transactionHash = ptx.getHash();

		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		KeyPairResponse keyPairResponse = new KeyPairResponse();
		keyPairResponse.keyPair = participant;
		keyPairResponse.txResp = txResp;
		keyPairResponse.txHash = transactionHash;
		return keyPairResponse;
	}

	public static KeyPairResponse testSDK_UpdateParticipantState(AsymmetricKeypair adminKey, BlockchainKeypair participantKeyPair, HashDigest ledgerHash,
																 BlockchainService blockchainService) {
		// 定义交易；
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		ParticipantInfoData participantInfoData = new ParticipantInfoData("peer4", participantKeyPair.getPubKey(), new NetworkAddress("127.0.0.1", 20000));

		txTpl.states().update(new BlockchainIdentityData(participantInfoData.getPubKey()), participantInfoData.getNetworkAddress(), ParticipantNodeState.ACTIVED);

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();

		HashDigest transactionHash = ptx.getHash();

		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		KeyPairResponse keyPairResponse = new KeyPairResponse();
		keyPairResponse.keyPair = participantKeyPair;
		keyPairResponse.txResp = txResp;
		keyPairResponse.txHash = transactionHash;
		return keyPairResponse;
	}

	public static void validKeyPair(IntegrationBase.KeyPairResponse keyPairResponse, LedgerQuery ledgerRepository,
			KeyPairType keyPairType) {
		TransactionResponse txResp = keyPairResponse.txResp;
		HashDigest transactionHash = keyPairResponse.txHash;
		BlockchainKeypair keyPair = keyPairResponse.keyPair;
		long index = validLong.incrementAndGet();
		System.out.printf("validKeyPair start %s \r\n", index);
		ledgerRepository.retrieveLatestBlock();

		assertEquals(txResp.getExecutionState(), TransactionState.SUCCESS);
		assertEquals(txResp.getBlockHeight(), ledgerRepository.getLatestBlockHeight());
		assertEquals(txResp.getContentHash(), transactionHash);
		assertEquals(txResp.getBlockHash(), ledgerRepository.getLatestBlockHash());
		if (keyPairType == KeyPairType.USER) {
			assertTrue(ledgerRepository.getUserAccountSet(ledgerRepository.getLatestBlock())
					.contains(keyPair.getAddress()));
		}

		if (keyPairType == KeyPairType.DATAACCOUNT) {
			assertNotNull(ledgerRepository.getDataAccountSet(ledgerRepository.getLatestBlock())
					.getAccount(keyPair.getAddress()));
		}
		System.out.printf("validKeyPair end %s \r\n", index);
	}

	public static void validKeyPair(IntegrationBase.KeyPairResponse keyPairResponse, LedgerQuery ledgerRepository,
			KeyPairType keyPairType, CountDownLatch countDownLatch) {

		TransactionResponse txResp = keyPairResponse.txResp;
		HashDigest transactionHash = keyPairResponse.txHash;
		BlockchainKeypair keyPair = keyPairResponse.keyPair;
		ledgerRepository.retrieveLatestBlock();

		assertEquals(txResp.getExecutionState(), TransactionState.SUCCESS);
		assertEquals(txResp.getBlockHeight(), ledgerRepository.getLatestBlockHeight());
		assertEquals(txResp.getContentHash(), transactionHash);
		assertEquals(txResp.getBlockHash(), ledgerRepository.getLatestBlockHash());
		if (keyPairType == KeyPairType.USER) {
			assertTrue(ledgerRepository.getUserAccountSet(ledgerRepository.getLatestBlock())
					.contains(keyPair.getAddress()));
		}

		if (keyPairType == KeyPairType.DATAACCOUNT) {
			assertNotNull(ledgerRepository.getDataAccountSet(ledgerRepository.getLatestBlock())
					.getAccount(keyPair.getAddress()));
		}
		countDownLatch.countDown();
	}

	public static void validKvWrite(IntegrationBase.KvResponse kvResponse, LedgerQuery ledgerRepository,
			BlockchainService blockchainService) {
		// 先验证应答
		TransactionResponse txResp = kvResponse.getTxResp();
		HashDigest transactionHash = kvResponse.getTxHash();
		HashDigest ledgerHash = kvResponse.getLedgerHash();
		String daAddress = kvResponse.getDataAccount().toBase58();
		String dataKey = kvResponse.getKey();
		String dataVal = kvResponse.getValue();

		ledgerRepository.retrieveLatestBlock();

		assertEquals(TransactionState.SUCCESS, txResp.getExecutionState());
		assertEquals(txResp.getBlockHeight(), ledgerRepository.getLatestBlockHeight());
		assertEquals(txResp.getContentHash(), transactionHash);
		assertEquals(txResp.getBlockHash(), ledgerRepository.getLatestBlockHash());

		TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, daAddress, dataKey);
		for (TypedKVEntry kvDataEntry : kvDataEntries) {
			assertEquals(dataKey, kvDataEntry.getKey());
			String valHexText = (String) kvDataEntry.getValue();
			assertEquals(dataVal, valHexText);
		}
	}

	public static LedgerQuery[] buildLedgers(LedgerBindingConfig[] bindingConfigs,
			DbConnectionFactory[] dbConnectionFactories) {
		int[] ids = { 0, 1, 2, 3 };
		LedgerQuery[] ledgers = new LedgerQuery[ids.length];
		LedgerManager[] ledgerManagers = new LedgerManager[ids.length];
		for (int i = 0; i < ids.length; i++) {
			ledgerManagers[i] = new LedgerManager();
			HashDigest ledgerHash = bindingConfigs[0].getLedgerHashs()[0];
			DbConnection conn = dbConnectionFactories[i]
					.connect(bindingConfigs[i].getLedger(ledgerHash).getDbConnection().getUri());
			ledgers[i] = ledgerManagers[i].register(ledgerHash, conn.getStorageService());
		}
		return ledgers;
	}

	public static void testConsistencyAmongNodes(LedgerQuery[] ledgers) {
		LedgerQuery ledger0 = ledgers[0];
		LedgerBlock latestBlock0 = ledger0.retrieveLatestBlock();
		for (int i = 1; i < ledgers.length; i++) {
			LedgerQuery otherLedger = ledgers[i];
			LedgerBlock otherLatestBlock = otherLedger.retrieveLatestBlock();
			assertEquals(ledger0.getHash(), otherLedger.getHash());
			assertEquals(ledger0.getLatestBlockHeight(), otherLedger.getLatestBlockHeight());
			assertEquals(ledger0.getLatestBlockHash(), otherLedger.getLatestBlockHash());

			assertEquals(latestBlock0.getHeight(), otherLatestBlock.getHeight());
			assertEquals(latestBlock0.getHash(), otherLatestBlock.getHash());
			assertEquals(latestBlock0.getAdminAccountHash(), otherLatestBlock.getAdminAccountHash());
			assertEquals(latestBlock0.getTransactionSetHash(), otherLatestBlock.getTransactionSetHash());
			assertEquals(latestBlock0.getUserAccountSetHash(), otherLatestBlock.getUserAccountSetHash());
			assertEquals(latestBlock0.getDataAccountSetHash(), otherLatestBlock.getDataAccountSetHash());
			assertEquals(latestBlock0.getContractAccountSetHash(), otherLatestBlock.getContractAccountSetHash());
			assertEquals(latestBlock0.getPreviousHash(), otherLatestBlock.getPreviousHash());
		}
	}

	public static PeerTestRunner[] peerNodeStart(HashDigest ledgerHash, String dbType) {
		NetworkAddress peerSrvAddr0 = new NetworkAddress("127.0.0.1", 12000);
		LedgerBindingConfig bindingConfig0 = loadBindingConfig(0, ledgerHash, dbType);
		PeerTestRunner peer0 = new PeerTestRunner(peerSrvAddr0, bindingConfig0);

		NetworkAddress peerSrvAddr1 = new NetworkAddress("127.0.0.1", 12010);
		LedgerBindingConfig bindingConfig1 = loadBindingConfig(1, ledgerHash, dbType);
		PeerTestRunner peer1 = new PeerTestRunner(peerSrvAddr1, bindingConfig1);

		NetworkAddress peerSrvAddr2 = new NetworkAddress("127.0.0.1", 12020);
		LedgerBindingConfig bindingConfig2 = loadBindingConfig(2, ledgerHash, dbType);
		PeerTestRunner peer2 = new PeerTestRunner(peerSrvAddr2, bindingConfig2);

		NetworkAddress peerSrvAddr3 = new NetworkAddress("127.0.0.1", 12030);
		LedgerBindingConfig bindingConfig3 = loadBindingConfig(3, ledgerHash, dbType);
		PeerTestRunner peer3 = new PeerTestRunner(peerSrvAddr3, bindingConfig3);

		ThreadInvoker.AsyncCallback<Object> peerStarting0 = peer0.start();
		ThreadInvoker.AsyncCallback<Object> peerStarting1 = peer1.start();
		ThreadInvoker.AsyncCallback<Object> peerStarting2 = peer2.start();
		ThreadInvoker.AsyncCallback<Object> peerStarting3 = peer3.start();

		peerStarting0.waitReturn();
		peerStarting1.waitReturn();
		peerStarting2.waitReturn();
		peerStarting3.waitReturn();

		return new PeerTestRunner[] { peer0, peer1, peer2, peer3 };
	}

	public static LedgerBindingConfig loadBindingConfig(int id, HashDigest ledgerHash, String dbType) {
		LedgerBindingConfig ledgerBindingConfig;
		String newLedger = ledgerHash.toBase58();
		String resourceClassPath = "ledger-binding-" + dbType + "-" + id + ".conf";
		String ledgerBindingUrl = IntegrationBase.class.getResource("/") + resourceClassPath;

		try {
			URL url = new URL(ledgerBindingUrl);
			File ledgerBindingConf = new File(url.getPath());
			System.out.printf("URL-ledgerBindingConf = %s \r\n", url.getPath());
			if (ledgerBindingConf.exists()) {
				List<String> readLines = FileUtils.readLines(ledgerBindingConf);

				List<String> writeLines = new ArrayList<>();

				if (readLines != null && !readLines.isEmpty()) {
					String oldLedgerLine = null;
					for (String readLine : readLines) {
						if (readLine.startsWith("ledger")) {
							oldLedgerLine = readLine;
							break;
						}
					}
					String[] oldLedgerArray = oldLedgerLine.split("=");

					String oldLedger = oldLedgerArray[1];
					if (!oldLedger.equalsIgnoreCase(newLedger)) {
						for (String readLine : readLines) {
							String newLine = readLine.replace(oldLedger, newLedger);
							if (dbType.equalsIgnoreCase("rocksdb")) {
								if (newLine.contains("db.uri")) {
									String[] propArray = newLine.split("=");
									String dbKey = propArray[0];
									String dbValue = LedgerInitConsensusConfig.rocksdbConnectionStrings[id];
									newLine = dbKey + "=" + dbValue;
								}
							}
							writeLines.add(newLine);
						}
					} else if (dbType.equalsIgnoreCase("rocksdb")) {
						for (String readLine : readLines) {
							String newLine = readLine;
							if (readLine.contains("db.uri")) {
								String[] propArray = readLine.split("=");
								String dbKey = propArray[0];
								String dbValue = LedgerInitConsensusConfig.rocksdbConnectionStrings[id];
								newLine = dbKey + "=" + dbValue;
							}
							writeLines.add(newLine);
						}
					}
					if (!writeLines.isEmpty()) {
						FileUtils.writeLines(ledgerBindingConf, writeLines);
					}
				}
			}
		} catch (Exception e) {

		}

		ClassPathResource res = new ClassPathResource(resourceClassPath);
		try (InputStream in = res.getInputStream()) {
			ledgerBindingConfig = LedgerBindingConfig.resolve(in);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return ledgerBindingConfig;
	}

	public static class KeyPairResponse {
		HashDigest txHash;

		BlockchainKeypair keyPair;

		TransactionResponse txResp;

		public BlockchainKeypair getKeyPair() {
			return keyPair;
		}

		public TransactionResponse getTxResp() {
			return txResp;
		}

		public HashDigest getTxHash() {
			return txHash;
		}
	}

	public static class KvResponse {

		Bytes dataAccount;

		HashDigest ledgerHash;

		HashDigest txHash;

		TransactionResponse txResp;

		String key;

		String value;

		public HashDigest getTxHash() {
			return txHash;
		}

		public TransactionResponse getTxResp() {
			return txResp;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public HashDigest getLedgerHash() {
			return ledgerHash;
		}

		public Bytes getDataAccount() {
			return dataAccount;
		}
	}

	public enum KeyPairType {
		USER, DATAACCOUNT
	}

	// 合约测试使用的初始化数据;
    static BlockchainKeypair contractDataKey = BlockchainKeyGenerator.getInstance().generate();
    static BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
    // 保存资产总数的键；
    // 第二个参数;
    private static String contractZipName = "contract-read.jar";
    static HashDigest txContentHash;

    public static LedgerBlock testSDK_Contract(AsymmetricKeypair adminKey, HashDigest ledgerHash,
			BlockchainService blockchainService, LedgerQuery ledgerRepository) {
        KeyPairResponse keyPairResponse = testSDK_RegisterDataAccount(adminKey,ledgerHash,blockchainService);

		System.out.println("adminKey=" + AddressEncoding.generateAddress(adminKey.getPubKey()));
		BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
		System.out.println("userKey=" + userKey.getAddress());
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.users().register(userKey.getIdentity());

		// 定义交易；
		byte[] contractCode = getChainCodeBytes();
		txTpl.contracts().deploy(contractDeployKey.getIdentity(), contractCode);

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();
		assertTrue(txResp.isSuccess());

        // 验证结果；
        assertEquals(ptx.getHash(),txResp.getContentHash());

		LedgerBlock block = ledgerRepository.getBlock(txResp.getBlockHeight());
		byte[] contractCodeInDb = ledgerRepository.getContractAccountSet(block)
				.getAccount(contractDeployKey.getAddress()).getChainCode();
		assertArrayEquals(contractCode, contractCodeInDb);

		// execute the contract;
//        testContractExe(adminKey, ledgerHash, keyPairResponse.keyPair,  blockchainService, ledgerRepository);
//        testContractExe1(adminKey, ledgerHash, keyPairResponse.keyPair,  blockchainService, ledgerRepository);
		testExeReadContract(adminKey, ledgerHash, blockchainService);
		return block;
	}

//    private static  <T> void testContractExe(AsymmetricKeypair adminKey, HashDigest ledgerHash, BlockchainKeypair dataKey,
//			BlockchainService blockchainService, LedgerRepository ledgerRepository) {
//		LedgerInfo ledgerInfo = blockchainService.getLedger(ledgerHash);
//		LedgerBlock previousBlock = blockchainService.getBlock(ledgerHash, ledgerInfo.getLatestBlockHeight() - 1);
//
//		// 定义交易；
//		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
//
//        Byte byteObj = Byte.parseByte("123");
////        txTpl.contract(contractDeployKey.getAddress(),AssetContract2.class).issue(byteObj,
////                contractDeployKey.getAddress().toBase58(),321123);
//        txTpl.contract(contractDeployKey.getAddress(),AssetContract2.class).issue(byteObj,
//                dataKey.getAddress().toBase58(),Bytes.fromString("123321"));
//
//		// 签名；
//		PreparedTransaction ptx = txTpl.prepare();
//		ptx.sign(adminKey);
//
//		// 提交并等待共识返回；
//		TransactionResponse txResp = ptx.commit();
//
//		// 验证结果；
//        Assert.assertTrue(txResp.isSuccess());
//        assertEquals(ptx.getHash(),txResp.getContentHash());
//        LedgerBlock block = ledgerRepository.getBlock(txResp.getBlockHeight());
//        KVDataEntry[] kvDataEntries =  ledgerRepository.getDataAccountSet(block).getDataAccount(dataKey.getAddress()).getDataEntries(0,1);
//        assertEquals("100",kvDataEntries[0].getValue().toString());
//    }

//    private static  <T> void testContractExe1(AsymmetricKeypair adminKey, HashDigest ledgerHash, BlockchainKeypair dataKey,
//                                             BlockchainService blockchainService,LedgerRepository ledgerRepository) {
//        LedgerInfo ledgerInfo = blockchainService.getLedger(ledgerHash);
//        LedgerBlock previousBlock = blockchainService.getBlock(ledgerHash, ledgerInfo.getLatestBlockHeight() - 1);
//
//        // 定义交易；
//        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
//
//        AssetContract2 assetContract = txTpl.contract(contractDeployKey.getAddress(), AssetContract2.class);
//        ContractBizContent contractBizContent = () -> new String[]{"param1","param2"};
//        assetContract.issue(contractBizContent,dataKey.getAddress().toBase58(),123456);
//
//        // 签名；
//        PreparedTransaction ptx = txTpl.prepare();
//        ptx.sign(adminKey);
//
//        // 提交并等待共识返回；
//        TransactionResponse txResp = ptx.commit();
//
//        // 验证结果；
//		Assert.assertTrue(txResp.isSuccess());
//        assertEquals(ptx.getHash(),txResp.getContentHash());
//        LedgerBlock block = ledgerRepository.getBlock(txResp.getBlockHeight());
//        KVDataEntry[] kvDataEntries =  ledgerRepository.getDataAccountSet(block).getDataAccount(dataKey.getAddress()).getDataEntries(1,2);
//        assertEquals("value1",kvDataEntries[0].getValue().toString());
//        assertEquals(888L,kvDataEntries[1].getValue());
//	}

	private static void testExeReadContract(AsymmetricKeypair adminKey, HashDigest ledgerHash, BlockchainService blockchainService) {

		// 首先注册一个数据账户
		BlockchainKeypair newDataAccount = BlockchainKeyGenerator.getInstance().generate();

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.dataAccounts().register(newDataAccount.getIdentity());

		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		ptx.commit();

		// 再提交一个KV写入
		String key1 = "JingDong", value1 = "www.jd.com";
		String key2 = "JD", value2 = "JingDong";
		String key3 = "Test", value3 = "OK";

		TransactionTemplate txKv = blockchainService.newTransaction(ledgerHash);
		txKv.dataAccount(newDataAccount.getAddress())
				.setText(key1, value1, -1)
				.setBytes(key2, Bytes.fromString(value2), -1)
				.setBytes(key3, Bytes.fromString(value3).toBytes(), -1);
		PreparedTransaction kvPtx = txKv.prepare();
		kvPtx.sign(adminKey);

		// 提交并等待共识返回；
		kvPtx.commit();

		// 下面才是执行Read交易
		// 定义交易；
		TransactionTemplate txContract = blockchainService.newTransaction(ledgerHash);

		ReadContract readContract1 = txContract.contract(contractDeployKey.getAddress(), ReadContract.class);

		GenericValueHolder<String> result1 = decode(readContract1.read(newDataAccount.getAddress().toBase58(), key1));

		ReadContract readContract2 = txContract.contract(contractDeployKey.getAddress(), ReadContract.class);

		readContract2.read(newDataAccount.getAddress().toBase58(), key2);

		ReadContract readContract3 = txContract.contract(contractDeployKey.getAddress(), ReadContract.class);

		GenericValueHolder<Long> result3 = decode(readContract3.readVersion(newDataAccount.getAddress().toBase58(), key2));

		// 签名；
		PreparedTransaction contractPtx = txContract.prepare();
		contractPtx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse readTxResp = contractPtx.commit();

		OperationResult[] operationResults = readTxResp.getOperationResults();

		// 通过EventResult获取结果
		System.out.printf("readContract1.result = %s \r\n", result1.get());
		System.out.printf("readContract3.result = %s \r\n", result3.get());

		for (int i = 0; i < operationResults.length; i++) {
			OperationResult opResult = operationResults[i];
			System.out.printf("Operation[%s].result = %s \r\n", opResult.getIndex(), BytesValueEncoding.decode(opResult.getResult()));
		}



//		// 打印结果
//		for (OperationResult or : operationResults) {
//			System.out.printf("操作[%s].Result = %s \r\n", or.getIndex(), ContractSerializeUtils.resolve(or.getResult()));
//		}
//
//        // 验证结果
//        assertNotNull(contractReturn);
//        assertEquals(contractReturn.length, 3);
//
//        String returnVal1 = contractReturn[0];
//        assertEquals(value1, returnVal1);
//
//        String returnVal2 = contractReturn[1];
//        assertEquals(value2, returnVal2);
//
//        String returnVal3 = contractReturn[2];
//        assertEquals("0", returnVal3);
	}

	/**
	 * 根据合约构建字节数组;
	 *
	 * @return
	 */
    private static byte[] getChainCodeBytes() {
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