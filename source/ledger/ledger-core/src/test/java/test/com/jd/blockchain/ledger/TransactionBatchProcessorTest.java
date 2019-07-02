package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.EndpointRequest;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.ledger.core.impl.DefaultOperationHandleRegisteration;
import com.jd.blockchain.ledger.core.impl.LedgerManager;
import com.jd.blockchain.ledger.core.impl.LedgerTransactionalEditor;
import com.jd.blockchain.ledger.core.impl.OperationHandleRegisteration;
import com.jd.blockchain.ledger.core.impl.TransactionBatchProcessor;
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
		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(ledgerRepo.getLatestBlock());
		UserAccount user0 = previousBlockDataset.getUserAccountSet().getUser(parti0.getAddress());
		assertNotNull(user0);
		boolean partiRegistered = previousBlockDataset.getUserAccountSet().contains(parti0.getAddress());
		assertTrue(partiRegistered);

		// 生成新区块；
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();

		OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(newBlockEditor, previousBlockDataset,
				opReg, ledgerManager);

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

	@Test
	public void testMultiTxsProcess() {
		final MemoryKVStorage STORAGE = new MemoryKVStorage();

		// 初始化账本到指定的存储库；
		ledgerHash = initLedger(STORAGE, parti0, parti1, parti2, parti3);

		// 加载账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, STORAGE);

		// 验证参与方账户的存在；
		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(ledgerRepo.getLatestBlock());
		UserAccount user0 = previousBlockDataset.getUserAccountSet().getUser(parti0.getAddress());
		assertNotNull(user0);
		boolean partiRegistered = previousBlockDataset.getUserAccountSet().contains(parti0.getAddress());
		assertTrue(partiRegistered);

		// 生成新区块；
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();

		OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(newBlockEditor, previousBlockDataset,
				opReg, ledgerManager);

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

		LedgerDataSet ledgerDS = ledgerRepo.getDataSet(latestBlock);
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
		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(ledgerRepo.getLatestBlock());
		UserAccount user0 = previousBlockDataset.getUserAccountSet().getUser(parti0.getAddress());
		assertNotNull(user0);
		boolean partiRegistered = previousBlockDataset.getUserAccountSet().contains(parti0.getAddress());
		assertTrue(partiRegistered);

		// 生成新区块；
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();

		OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(newBlockEditor, previousBlockDataset,
				opReg, ledgerManager);

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

		LedgerDataSet ledgerDS = ledgerRepo.getDataSet(latestBlock);
		boolean existUser1 = ledgerDS.getUserAccountSet().contains(userKeypair1.getAddress());
		boolean existUser2 = ledgerDS.getUserAccountSet().contains(userKeypair2.getAddress());
		boolean existUser3 = ledgerDS.getUserAccountSet().contains(userKeypair3.getAddress());
		assertTrue(existUser1);
		assertFalse(existUser2);
		assertTrue(existUser3);
	}

	private HashDigest initLedger(MemoryKVStorage storage, BlockchainKeypair... partiKeys) {
		// 创建初始化配置；
		LedgerInitSetting initSetting = LedgerTestUtils.createLedgerInitSetting(partiKeys);

		// 创建账本；
		LedgerEditor ldgEdt = LedgerTransactionalEditor.createEditor(initSetting, LEDGER_KEY_PREFIX, storage, storage);

		TransactionRequest genesisTxReq = LedgerTestUtils.createLedgerInitTxRequest(partiKeys);
		LedgerTransactionContext genisisTxCtx = ldgEdt.newTransaction(genesisTxReq);
		LedgerDataSet ldgDS = genisisTxCtx.getDataSet();

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

		assertEquals(block.getHash(), block.getLedgerHash());

		// 提交数据，写入存储；
		ldgEdt.commit();

		HashDigest ledgerHash = block.getHash();
		return ledgerHash;
	}

}
