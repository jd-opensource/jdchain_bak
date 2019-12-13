package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.DataVersionConflictException;
import com.jd.blockchain.ledger.EndpointRequest;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.DataAccount;
import com.jd.blockchain.ledger.core.DefaultOperationHandleRegisteration;
import com.jd.blockchain.ledger.core.LedgerDataQuery;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerSecurityManager;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.LedgerTransactionalEditor;
import com.jd.blockchain.ledger.core.OperationHandleRegisteration;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionBatchProcessor;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;

public class TransactionBatchProcessorTest {
	static {
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(TransactionContentBody.class);
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(NodeRequest.class);
		DataContractRegistry.register(EndpointRequest.class);
		DataContractRegistry.register(TransactionResponse.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
	}

	private static final String LEDGER_KEY_PREFIX = "LDG://";

	private HashDigest ledgerHash = null;

	private BlockchainKeypair parti0 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti1 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti2 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti3 = BlockchainKeyGenerator.getInstance().generate();

	private BlockchainKeypair[] participants = { parti0, parti1, parti2, parti3 };

	// TODO: 验证无效签名会被拒绝；

	@Test
	public void testSingleTxProcess() {
		final MemoryKVStorage STORAGE = new MemoryKVStorage();

		// 初始化账本到指定的存储库；
		ledgerHash = initLedger(STORAGE, parti0, parti1, parti2, parti3);

		// 加载账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);

		// 验证参与方账户的存在；
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(ledgerRepo.getLatestBlock());
		UserAccount user0 = previousBlockDataset.getUserAccountSet().getAccount(parti0.getAddress());
		assertNotNull(user0);
		boolean partiRegistered = previousBlockDataset.getUserAccountSet().contains(parti0.getAddress());
		assertTrue(partiRegistered);

		// 生成新区块；
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();

		OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		LedgerSecurityManager securityManager = getSecurityManager();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor,
				ledgerRepo, opReg);

		// 注册新用户；
		BlockchainKeypair userKeypair = BlockchainKeyGenerator.getInstance().generate();
		TransactionRequest transactionRequest = LedgerTestUtils.createTxRequest_UserReg(userKeypair, ledgerHash, parti0,
				parti0);
		TransactionResponse txResp = txbatchProcessor.schedule(transactionRequest);

		LedgerBlock newBlock = newBlockEditor.prepare();
		newBlockEditor.commit();

		// 验证正确性；
		ledgerManager = new LedgerManager();
		ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);

		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
		assertEquals(newBlock.getHash(), latestBlock.getHash());
		assertEquals(1, newBlock.getHeight());

		assertEquals(TransactionState.SUCCESS, txResp.getExecutionState());
	}

	private static LedgerSecurityManager getSecurityManager() {
		LedgerSecurityManager securityManager = Mockito.mock(LedgerSecurityManager.class);

		SecurityPolicy securityPolicy = Mockito.mock(SecurityPolicy.class);
		when(securityPolicy.isEndpointEnable(any(LedgerPermission.class), any())).thenReturn(true);
		when(securityPolicy.isEndpointEnable(any(TransactionPermission.class), any())).thenReturn(true);
		when(securityPolicy.isNodeEnable(any(LedgerPermission.class), any())).thenReturn(true);
		when(securityPolicy.isNodeEnable(any(TransactionPermission.class), any())).thenReturn(true);

		when(securityManager.createSecurityPolicy(any(), any())).thenReturn(securityPolicy);

		return securityManager;
	}

	@Test
	public void testMultiTxsProcess() {
		final MemoryKVStorage STORAGE = new MemoryKVStorage();

		// 初始化账本到指定的存储库；
		ledgerHash = initLedger(STORAGE, parti0, parti1, parti2, parti3);

		// 加载账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);

		// 验证参与方账户的存在；
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(ledgerRepo.getLatestBlock());
		UserAccount user0 = previousBlockDataset.getUserAccountSet().getAccount(parti0.getAddress());
		assertNotNull(user0);
		boolean partiRegistered = previousBlockDataset.getUserAccountSet().contains(parti0.getAddress());
		assertTrue(partiRegistered);

		// 生成新区块；
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();

		OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		LedgerSecurityManager securityManager = getSecurityManager();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor,
				ledgerRepo, opReg);

		// 注册新用户；
		BlockchainKeypair userKeypair1 = BlockchainKeyGenerator.getInstance().generate();
		TransactionRequest transactionRequest1 = LedgerTestUtils.createTxRequest_UserReg(userKeypair1, ledgerHash,
				parti0, parti0);
		TransactionResponse txResp1 = txbatchProcessor.schedule(transactionRequest1);

		BlockchainKeypair userKeypair2 = BlockchainKeyGenerator.getInstance().generate();
		TransactionRequest transactionRequest2 = LedgerTestUtils.createTxRequest_UserReg(userKeypair2, ledgerHash,
				parti0, parti0);
		TransactionResponse txResp2 = txbatchProcessor.schedule(transactionRequest2);

		LedgerBlock newBlock = newBlockEditor.prepare();
		newBlockEditor.commit();

		assertEquals(TransactionState.SUCCESS, txResp1.getExecutionState());
		assertEquals(TransactionState.SUCCESS, txResp2.getExecutionState());

		// 验证正确性；
		ledgerManager = new LedgerManager();
		ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);

		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
		assertEquals(newBlock.getHash(), latestBlock.getHash());
		assertEquals(1, newBlock.getHeight());

		LedgerDataQuery ledgerDS = ledgerRepo.getLedgerData(latestBlock);
		boolean existUser1 = ledgerDS.getUserAccountSet().contains(userKeypair1.getAddress());
		boolean existUser2 = ledgerDS.getUserAccountSet().contains(userKeypair2.getAddress());
		assertTrue(existUser1);
		assertTrue(existUser2);
	}

	@Test
	public void testTxRollback() {
		final MemoryKVStorage STORAGE = new MemoryKVStorage();

		// 初始化账本到指定的存储库；
		ledgerHash = initLedger(STORAGE, parti0, parti1, parti2, parti3);

		// 加载账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);

		// 验证参与方账户的存在；
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(ledgerRepo.getLatestBlock());
		UserAccount user0 = previousBlockDataset.getUserAccountSet().getAccount(parti0.getAddress());
		assertNotNull(user0);
		boolean partiRegistered = previousBlockDataset.getUserAccountSet().contains(parti0.getAddress());
		assertTrue(partiRegistered);

		// 生成新区块；
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();

		OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		LedgerSecurityManager securityManager = getSecurityManager();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor,
				ledgerRepo, opReg);

		// 注册新用户；
		BlockchainKeypair userKeypair1 = BlockchainKeyGenerator.getInstance().generate();
		TransactionRequest transactionRequest1 = LedgerTestUtils.createTxRequest_UserReg(userKeypair1, ledgerHash,
				parti0, parti0);
		TransactionResponse txResp1 = txbatchProcessor.schedule(transactionRequest1);

		BlockchainKeypair userKeypair2 = BlockchainKeyGenerator.getInstance().generate();
		TransactionRequest transactionRequest2 = LedgerTestUtils
				.createTxRequest_MultiOPs_WithNotExistedDataAccount(userKeypair2, ledgerHash, parti0, parti0);
		TransactionResponse txResp2 = txbatchProcessor.schedule(transactionRequest2);

		BlockchainKeypair userKeypair3 = BlockchainKeyGenerator.getInstance().generate();
		TransactionRequest transactionRequest3 = LedgerTestUtils.createTxRequest_UserReg(userKeypair3, ledgerHash,
				parti0, parti0);
		TransactionResponse txResp3 = txbatchProcessor.schedule(transactionRequest3);

		LedgerBlock newBlock = newBlockEditor.prepare();
		newBlockEditor.commit();

		assertEquals(TransactionState.SUCCESS, txResp1.getExecutionState());
		assertEquals(TransactionState.DATA_ACCOUNT_DOES_NOT_EXIST, txResp2.getExecutionState());
		assertEquals(TransactionState.SUCCESS, txResp3.getExecutionState());

		// 验证正确性；
		ledgerManager = new LedgerManager();
		ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);

		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
		assertEquals(newBlock.getHash(), latestBlock.getHash());
		assertEquals(1, newBlock.getHeight());

		LedgerTransaction tx1 = ledgerRepo.getTransactionSet()
				.get(transactionRequest1.getTransactionContent().getHash());
		LedgerTransaction tx2 = ledgerRepo.getTransactionSet()
				.get(transactionRequest2.getTransactionContent().getHash());
		LedgerTransaction tx3 = ledgerRepo.getTransactionSet()
				.get(transactionRequest3.getTransactionContent().getHash());

		assertNotNull(tx1);
		assertEquals(TransactionState.SUCCESS, tx1.getExecutionState());
		assertNotNull(tx2);
		assertEquals(TransactionState.DATA_ACCOUNT_DOES_NOT_EXIST, tx2.getExecutionState());
		assertNotNull(tx3);
		assertEquals(TransactionState.SUCCESS, tx3.getExecutionState());

		LedgerDataQuery ledgerDS = ledgerRepo.getLedgerData(latestBlock);
		boolean existUser1 = ledgerDS.getUserAccountSet().contains(userKeypair1.getAddress());
		boolean existUser2 = ledgerDS.getUserAccountSet().contains(userKeypair2.getAddress());
		boolean existUser3 = ledgerDS.getUserAccountSet().contains(userKeypair3.getAddress());
		assertTrue(existUser1);
		assertFalse(existUser2);
		assertTrue(existUser3);
	}

	@Test
	public void testTxRollbackByVersionsConflict() {
		final MemoryKVStorage STORAGE = new MemoryKVStorage();

		// 初始化账本到指定的存储库；
		ledgerHash = initLedger(STORAGE, parti0, parti1, parti2, parti3);

		// 加载账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);

		// 验证参与方账户的存在；
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(ledgerRepo.getLatestBlock());
		UserAccount user0 = previousBlockDataset.getUserAccountSet().getAccount(parti0.getAddress());
		assertNotNull(user0);
		boolean partiRegistered = previousBlockDataset.getUserAccountSet().contains(parti0.getAddress());
		assertTrue(partiRegistered);

		// 注册数据账户；
		// 生成新区块；
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();

		OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		LedgerSecurityManager securityManager = getSecurityManager();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor,
				ledgerRepo, opReg);

		BlockchainKeypair dataAccountKeypair = BlockchainKeyGenerator.getInstance().generate();
		TransactionRequest transactionRequest1 = LedgerTestUtils.createTxRequest_DataAccountReg(dataAccountKeypair,
				ledgerHash, parti0, parti0);
		TransactionResponse txResp1 = txbatchProcessor.schedule(transactionRequest1);
		LedgerBlock newBlock = newBlockEditor.prepare();
		newBlockEditor.commit();

		assertEquals(TransactionState.SUCCESS, txResp1.getExecutionState());
		DataAccount dataAccount = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress());
		assertNotNull(dataAccount);

		// 正确写入 KV 数据；
		TransactionRequest txreq1 = LedgerTestUtils.createTxRequest_DataAccountWrite(dataAccountKeypair.getAddress(),
				"K1", "V-1-1", -1, ledgerHash, parti0, parti0);
		TransactionRequest txreq2 = LedgerTestUtils.createTxRequest_DataAccountWrite(dataAccountKeypair.getAddress(),
				"K2", "V-2-1", -1, ledgerHash, parti0, parti0);
		TransactionRequest txreq3 = LedgerTestUtils.createTxRequest_DataAccountWrite(dataAccountKeypair.getAddress(),
				"K3", "V-3-1", -1, ledgerHash, parti0, parti0);

		// 连续写 K1，K1的版本将变为1；
		TransactionRequest txreq4 = LedgerTestUtils.createTxRequest_DataAccountWrite(dataAccountKeypair.getAddress(),
				"K1", "V-1-2", 0, ledgerHash, parti0, parti0);

		newBlockEditor = ledgerRepo.createNextBlock();
		previousBlockDataset = ledgerRepo.getLedgerData(ledgerRepo.getLatestBlock());
		txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor, ledgerRepo, opReg);

		txbatchProcessor.schedule(txreq1);
		txbatchProcessor.schedule(txreq2);
		txbatchProcessor.schedule(txreq3);
		txbatchProcessor.schedule(txreq4);

		newBlock = newBlockEditor.prepare();
		newBlockEditor.commit();

		BytesValue v1_0 = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress()).getDataset().getValue("K1",
				0);
		BytesValue v1_1 = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress()).getDataset().getValue("K1",
				1);
		BytesValue v2 = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress()).getDataset().getValue("K2",
				0);
		BytesValue v3 = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress()).getDataset().getValue("K3",
				0);
		

		assertNotNull(v1_0);
		assertNotNull(v1_1);
		assertNotNull(v2);
		assertNotNull(v3);

		assertEquals("V-1-1", v1_0.getBytes().toUTF8String());
		assertEquals("V-1-2", v1_1.getBytes().toUTF8String());
		assertEquals("V-2-1", v2.getBytes().toUTF8String());
		assertEquals("V-3-1", v3.getBytes().toUTF8String());

		// 提交多笔数据写入的交易，包含存在数据版本冲突的交易，验证交易是否正确回滚；
		// 先写一笔正确的交易； k3 的版本将变为 1 ；
		TransactionRequest txreq5 = LedgerTestUtils.createTxRequest_DataAccountWrite(dataAccountKeypair.getAddress(),
				"K3", "V-3-2", 0, ledgerHash, parti0, parti0);
		// 指定冲突的版本号，正确的应该是版本1；
		TransactionRequest txreq6 = LedgerTestUtils.createTxRequest_DataAccountWrite(dataAccountKeypair.getAddress(),
				"K1", "V-1-3", 0, ledgerHash, parti0, parti0);

		newBlockEditor = ledgerRepo.createNextBlock();
		previousBlockDataset = ledgerRepo.getLedgerData(ledgerRepo.getLatestBlock());
		txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor, ledgerRepo, opReg);

		txbatchProcessor.schedule(txreq5);
		// 预期会产生版本冲突异常； DataVersionConflictionException;
		DataVersionConflictException versionConflictionException = null;
		try {
			txbatchProcessor.schedule(txreq6);
		} catch (DataVersionConflictException e) {
			versionConflictionException = e;
		}
//		assertNotNull(versionConflictionException);

		newBlock = newBlockEditor.prepare();
		newBlockEditor.commit();

		BytesValue v1 = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress()).getDataset().getValue("K1");
		v3 = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress()).getDataset().getValue("K3");

		// k1 的版本仍然为1，没有更新；
		long k1_version = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress())
				.getDataset().getVersion("K1");
		assertEquals(1, k1_version);

		long k3_version = ledgerRepo.getDataAccountSet().getAccount(dataAccountKeypair.getAddress())
				.getDataset().getVersion("K3");
		assertEquals(1, k3_version);

		assertNotNull(v1);
		assertNotNull(v3);
		assertEquals("V-1-2", v1.getBytes().toUTF8String());
		assertEquals("V-3-2", v3.getBytes().toUTF8String());

//		// 验证正确性；
//		ledgerManager = new LedgerManager();
//		ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);
//
//		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
//		assertEquals(newBlock.getHash(), latestBlock.getHash());
//		assertEquals(1, newBlock.getHeight());
//
//		LedgerTransaction tx1 = ledgerRepo.getTransactionSet()
//				.get(transactionRequest1.getTransactionContent().getHash());
//
//		assertNotNull(tx1);
//		assertEquals(TransactionState.SUCCESS, tx1.getExecutionState());

	}

	private HashDigest initLedger(MemoryKVStorage storage, BlockchainKeypair... partiKeys) {
		// 创建初始化配置；
		LedgerInitSetting initSetting = LedgerTestUtils.createLedgerInitSetting(partiKeys);

		// 创建账本；
		LedgerEditor ldgEdt = LedgerTransactionalEditor.createEditor(initSetting, LEDGER_KEY_PREFIX, storage, storage);

		TransactionRequest genesisTxReq = LedgerTestUtils.createLedgerInitTxRequest(partiKeys);
		LedgerTransactionContext genisisTxCtx = ldgEdt.newTransaction(genesisTxReq);
		LedgerDataset ldgDS = genisisTxCtx.getDataset();

		for (int i = 0; i < partiKeys.length; i++) {
			UserAccount userAccount = ldgDS.getUserAccountSet().register(partiKeys[i].getAddress(),
					partiKeys[i].getPubKey());
			userAccount.setProperty("Name", "参与方-" + i, -1);
			userAccount.setProperty("Share", "" + (10 + i), -1);
		}

		LedgerTransaction tx = genisisTxCtx.commit(TransactionState.SUCCESS);

		assertEquals(genesisTxReq.getTransactionContent().getHash(), tx.getTransactionContent().getHash());
		assertEquals(0, tx.getBlockHeight());

		LedgerBlock block = ldgEdt.prepare();

		assertEquals(0, block.getHeight());
		assertNotNull(block.getHash());
		assertNull(block.getPreviousHash());

		// 创世区块的账本哈希为 null；
		assertNull(block.getLedgerHash());
		assertNotNull(block.getHash());

		// 提交数据，写入存储；
		ldgEdt.commit();

		HashDigest ledgerHash = block.getHash();
		return ledgerHash;
	}

}
