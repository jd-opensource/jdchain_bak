package test.com.jd.blockchain.ledger.core;

import static com.jd.blockchain.transaction.ContractReturnValue.decode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Random;

import org.junit.Test;
import org.mockito.Mockito;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.EndpointRequest;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.ParticipantRegisterOperation;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.DefaultOperationHandleRegisteration;
import com.jd.blockchain.ledger.core.LedgerDataQuery;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerSecurityManager;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.LedgerTransactionalEditor;
import com.jd.blockchain.ledger.core.OperationHandleRegisteration;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionBatchProcessor;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.transaction.BooleanValueHolder;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;
import com.jd.blockchain.utils.io.BytesUtils;

import test.com.jd.blockchain.ledger.TxTestContract;
import test.com.jd.blockchain.ledger.TxTestContractImpl;

public class ContractInvokingTest {
	static {
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(TransactionContentBody.class);
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(NodeRequest.class);
		DataContractRegistry.register(EndpointRequest.class);
		DataContractRegistry.register(TransactionResponse.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
		DataContractRegistry.register(ParticipantNode.class);
		DataContractRegistry.register(ParticipantRegisterOperation.class);
		DataContractRegistry.register(ParticipantStateUpdateOperation.class);
	}

	private static final String LEDGER_KEY_PREFIX = "LDG://";

	private BlockchainKeypair parti0 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti1 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti2 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti3 = BlockchainKeyGenerator.getInstance().generate();

	// 采用基于内存的 Storage；
	private MemoryKVStorage storage = new MemoryKVStorage();

	private static final String CONTRACT_JAR = "contract-JDChain-Contract.jar";

	@Test
	public void testNormal() {
		// 初始化账本到指定的存储库；
		HashDigest ledgerHash = initLedger(storage, parti0, parti1, parti2, parti3);

		// 重新加载账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, storage);

		// 创建合约处理器；
		ContractInvokingHandle contractInvokingHandle = new ContractInvokingHandle();

		// 创建和加载合约实例；
		BlockchainKeypair contractKey = BlockchainKeyGenerator.getInstance().generate();
		Bytes contractAddress = contractKey.getAddress();

		TestContract contractInstance = Mockito.mock(TestContract.class);
		final String asset = "AK";
		final long issueAmount = new Random().nextLong();
		when(contractInstance.issue(anyString(), anyLong())).thenReturn(issueAmount);

		// 装载合约；
		contractInvokingHandle.setup(contractAddress, TestContract.class, contractInstance);

		// 注册合约处理器；
		DefaultOperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		opReg.registerHandle(contractInvokingHandle);

		// 发布指定地址合约
		deploy(ledgerRepo, ledgerManager, opReg, ledgerHash, contractKey);
		// 创建新区块的交易处理器；
		LedgerBlock preBlock = ledgerRepo.getLatestBlock();
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(preBlock);

		// 加载合约
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		LedgerSecurityManager securityManager = getSecurityManager();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor,
				ledgerRepo, opReg);

		// 构建基于接口调用合约的交易请求，用于测试合约调用；
		TxBuilder txBuilder = new TxBuilder(ledgerHash);
		TestContract contractProxy = txBuilder.contract(contractAddress, TestContract.class);

		// 构造调用合约的交易；
		contractProxy.issue(asset, issueAmount);

		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
		txReqBuilder.signAsEndpoint(parti0);
		txReqBuilder.signAsNode(parti0);
		TransactionRequest txReq = txReqBuilder.buildRequest();

		TransactionResponse resp = txbatchProcessor.schedule(txReq);
		verify(contractInstance, times(1)).issue(asset, issueAmount);
		OperationResult[] opResults = resp.getOperationResults();
		assertEquals(1, opResults.length);
		assertEquals(0, opResults[0].getIndex());

		byte[] expectedRetnBytes = BinaryProtocol.encode(TypedValue.fromInt64(issueAmount), BytesValue.class);
		byte[] reallyRetnBytes = BinaryProtocol.encode(opResults[0].getResult(), BytesValue.class);
		assertArrayEquals(expectedRetnBytes, reallyRetnBytes);

		// 提交区块；
		TransactionBatchResultHandle txResultHandle = txbatchProcessor.prepare();
		txResultHandle.commit();

		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
		assertEquals(preBlock.getHeight() + 1, latestBlock.getHeight());
		assertEquals(resp.getBlockHeight(), latestBlock.getHeight());
		assertEquals(resp.getBlockHash(), latestBlock.getHash());

		// 再验证一次结果；
		assertEquals(1, opResults.length);
		assertEquals(0, opResults[0].getIndex());

		reallyRetnBytes = BinaryProtocol.encode(opResults[0].getResult(), BytesValue.class);
		assertArrayEquals(expectedRetnBytes, reallyRetnBytes);

	}

//	@Test
	public void testReadNewWritting() {
		// 初始化账本到指定的存储库；
		HashDigest ledgerHash = initLedger(storage, parti0, parti1, parti2, parti3);

		// 重新加载账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, storage);

		// 创建合约处理器；
		ContractInvokingHandle contractInvokingHandle = new ContractInvokingHandle();

		// 创建和加载合约实例；
		BlockchainKeypair contractKey = BlockchainKeyGenerator.getInstance().generate();
		Bytes contractAddress = contractKey.getAddress();
		TxTestContractImpl contractInstance = new TxTestContractImpl();
		contractInvokingHandle.setup(contractAddress, TxTestContract.class, contractInstance);

		// 注册合约处理器；
		DefaultOperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		opReg.registerHandle(contractInvokingHandle);

		// 发布指定地址合约
		deploy(ledgerRepo, ledgerManager, opReg, ledgerHash, contractKey);

		// 创建新区块的交易处理器；
		LedgerBlock preBlock = ledgerRepo.getLatestBlock();
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(preBlock);

		// 加载合约
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(getSecurityManager(), newBlockEditor,
				ledgerRepo, opReg);

		String key = TxTestContractImpl.KEY;
		String value = "VAL";

		TxBuilder txBuilder = new TxBuilder(ledgerHash);
		BlockchainKeypair kpDataAccount = BlockchainKeyGenerator.getInstance().generate();
		contractInstance.setDataAddress(kpDataAccount.getAddress());

		txBuilder.dataAccounts().register(kpDataAccount.getIdentity());
		TransactionRequestBuilder txReqBuilder1 = txBuilder.prepareRequest();
		txReqBuilder1.signAsEndpoint(parti0);
		txReqBuilder1.signAsNode(parti0);
		TransactionRequest txReq1 = txReqBuilder1.buildRequest();

		// 构建基于接口调用合约的交易请求，用于测试合约调用；
		txBuilder = new TxBuilder(ledgerHash);
		TxTestContract contractProxy = txBuilder.contract(contractAddress, TxTestContract.class);
		BooleanValueHolder readableHolder = decode(contractProxy.testReadable());

		TransactionRequestBuilder txReqBuilder2 = txBuilder.prepareRequest();
		txReqBuilder2.signAsEndpoint(parti0);
		txReqBuilder2.signAsNode(parti0);
		TransactionRequest txReq2 = txReqBuilder2.buildRequest();

		TransactionResponse resp1 = txbatchProcessor.schedule(txReq1);
		TransactionResponse resp2 = txbatchProcessor.schedule(txReq2);

		// 提交区块；
		TransactionBatchResultHandle txResultHandle = txbatchProcessor.prepare();
		txResultHandle.commit();

		BytesValue latestValue = ledgerRepo.getDataAccountSet().getAccount(kpDataAccount.getAddress()).getDataset().getValue(key,
				-1);
		System.out.printf("latest value=[%s] %s \r\n", latestValue.getType(), latestValue.getBytes().toUTF8String());

		boolean readable = readableHolder.get();
		assertTrue(readable);

		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
		assertEquals(preBlock.getHeight() + 1, latestBlock.getHeight());
		assertEquals(resp1.getBlockHeight(), latestBlock.getHeight());
		assertEquals(resp1.getBlockHash(), latestBlock.getHash());
	}

	/**
	 * 验证在合约方法中写入数据账户时，如果版本校验失败是否会引发异常而导致回滚；<br>
	 * 期待正确的表现是引发异常而回滚当前交易；
	 */
	@Test
	public void testRollbackWhileVersionConfliction() {
		// 初始化账本到指定的存储库；
		HashDigest ledgerHash = initLedger(storage, parti0, parti1, parti2, parti3);

		// 重新加载账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, storage);

		// 创建合约处理器；
		ContractInvokingHandle contractInvokingHandle = new ContractInvokingHandle();

		// 创建和加载合约实例；
		BlockchainKeypair contractKey = BlockchainKeyGenerator.getInstance().generate();
		Bytes contractAddress = contractKey.getAddress();
		TxTestContractImpl contractInstance = new TxTestContractImpl();
		contractInvokingHandle.setup(contractAddress, TxTestContract.class, contractInstance);

		// 注册合约处理器；
		DefaultOperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		opReg.registerHandle(contractInvokingHandle);

		// 发布指定地址合约
		deploy(ledgerRepo, ledgerManager, opReg, ledgerHash, contractKey);

		// 注册数据账户；
		BlockchainKeypair kpDataAccount = BlockchainKeyGenerator.getInstance().generate();
		contractInstance.setDataAddress(kpDataAccount.getAddress());
		registerDataAccount(ledgerRepo, ledgerManager, opReg, ledgerHash, kpDataAccount);

		// 调用合约
		// 构建基于接口调用合约的交易请求，用于测试合约调用；
		buildBlock(ledgerRepo, ledgerManager, opReg, new TxDefinitor() {
			@Override
			public void buildTx(TxBuilder txBuilder) {
				TxTestContract contractProxy = txBuilder.contract(contractAddress, TxTestContract.class);
				contractProxy.testRollbackWhileVersionConfliction(kpDataAccount.getAddress().toBase58(), "K1", "V1-0",
						-1);
				contractProxy.testRollbackWhileVersionConfliction(kpDataAccount.getAddress().toBase58(), "K2", "V2-0",
						-1);
			}
		});
		// 预期数据都能够正常写入；
		DataEntry<String, TypedValue> kv1 = ledgerRepo.getDataAccountSet().getAccount(kpDataAccount.getAddress()).getDataset().getDataEntry("K1",
				0);
		DataEntry<String, TypedValue> kv2 = ledgerRepo.getDataAccountSet().getAccount(kpDataAccount.getAddress()).getDataset().getDataEntry("K2",
				0);
		assertEquals(0, kv1.getVersion());
		assertEquals(0, kv2.getVersion());
		assertEquals("V1-0", kv1.getValue().stringValue());
		assertEquals("V2-0", kv2.getValue().stringValue());

		// 构建基于接口调用合约的交易请求，用于测试合约调用；
		buildBlock(ledgerRepo, ledgerManager, opReg, new TxDefinitor() {
			@Override
			public void buildTx(TxBuilder txBuilder) {
				TxTestContract contractProxy = txBuilder.contract(contractAddress, TxTestContract.class);
				contractProxy.testRollbackWhileVersionConfliction(kpDataAccount.getAddress().toBase58(), "K1", "V1-1",
						0);
				contractProxy.testRollbackWhileVersionConfliction(kpDataAccount.getAddress().toBase58(), "K2", "V2-1",
						0);
			}
		});
		// 预期数据都能够正常写入；
		kv1 = ledgerRepo.getDataAccountSet().getAccount(kpDataAccount.getAddress()).getDataset().getDataEntry("K1", 1);
		kv2 = ledgerRepo.getDataAccountSet().getAccount(kpDataAccount.getAddress()).getDataset().getDataEntry("K2", 1);
		assertEquals(1, kv1.getVersion());
		assertEquals(1, kv2.getVersion());
		assertEquals("V1-1", kv1.getValue().stringValue());
		assertEquals("V2-1", kv2.getValue().stringValue());

		// 构建基于接口调用合约的交易请求，用于测试合约调用；
		buildBlock(ledgerRepo, ledgerManager, opReg, new TxDefinitor() {
			@Override
			public void buildTx(TxBuilder txBuilder) {
				TxTestContract contractProxy = txBuilder.contract(contractAddress, TxTestContract.class);
				contractProxy.testRollbackWhileVersionConfliction(kpDataAccount.getAddress().toBase58(), "K1", "V1-2",
						1);
				contractProxy.testRollbackWhileVersionConfliction(kpDataAccount.getAddress().toBase58(), "K2", "V2-2",
						0);//预期会回滚；
			}
		});
		// 预期数据回滚，账本没有发生变更；
		kv1 = ledgerRepo.getDataAccountSet().getAccount(kpDataAccount.getAddress()).getDataset().getDataEntry("K1", 1);
		assertEquals(1, kv1.getVersion());
		assertEquals("V1-1", kv1.getValue().stringValue());
		kv1 = ledgerRepo.getDataAccountSet().getAccount(kpDataAccount.getAddress()).getDataset().getDataEntry("K1", 2);
		assertNull(kv1);
		kv2 = ledgerRepo.getDataAccountSet().getAccount(kpDataAccount.getAddress()).getDataset().getDataEntry("K2", 1);
		assertEquals(1, kv2.getVersion());

	}

	private LedgerBlock buildBlock(LedgerRepository ledgerRepo, LedgerService ledgerService,
			OperationHandleRegisteration opReg, TxDefinitor txDefinitor) {
		LedgerBlock preBlock = ledgerRepo.getLatestBlock();
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(preBlock);
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(getSecurityManager(), newBlockEditor,
				ledgerRepo, opReg);

		TxBuilder txBuilder = new TxBuilder(ledgerRepo.getHash());
		txDefinitor.buildTx(txBuilder);

		TransactionRequest txReq = buildAndSignRequest(txBuilder, parti0, parti0);
		TransactionResponse resp = txbatchProcessor.schedule(txReq);

		// 提交区块；
		TransactionBatchResultHandle txResultHandle = txbatchProcessor.prepare();
		txResultHandle.commit();

		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
		assertNotNull(resp.getBlockHash());
		assertEquals(preBlock.getHeight() + 1, resp.getBlockHeight());
		return latestBlock;
	}

	private TransactionRequest buildAndSignRequest(TxBuilder txBuilder, BlockchainKeypair endpointKey,
			BlockchainKeypair nodeKey) {
		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
		txReqBuilder.signAsEndpoint(endpointKey);
		txReqBuilder.signAsNode(nodeKey);
		TransactionRequest txReq = txReqBuilder.buildRequest();
		return txReq;
	}

	private void registerDataAccount(LedgerRepository ledgerRepo, LedgerManager ledgerManager,
			DefaultOperationHandleRegisteration opReg, HashDigest ledgerHash, BlockchainKeypair kpDataAccount) {
		LedgerBlock preBlock = ledgerRepo.getLatestBlock();
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(preBlock);

		// 加载合约
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(getSecurityManager(), newBlockEditor,
				ledgerRepo, opReg);

		// 注册数据账户；
		TxBuilder txBuilder = new TxBuilder(ledgerHash);

		txBuilder.dataAccounts().register(kpDataAccount.getIdentity());
		TransactionRequestBuilder txReqBuilder1 = txBuilder.prepareRequest();
		txReqBuilder1.signAsEndpoint(parti0);
		txReqBuilder1.signAsNode(parti0);
		TransactionRequest txReq = txReqBuilder1.buildRequest();

		TransactionResponse resp = txbatchProcessor.schedule(txReq);

		TransactionBatchResultHandle txResultHandle = txbatchProcessor.prepare();
		txResultHandle.commit();

		assertNotNull(resp.getBlockHash());
		assertEquals(TransactionState.SUCCESS, resp.getExecutionState());
		assertEquals(preBlock.getHeight() + 1, resp.getBlockHeight());
	}

	private void deploy(LedgerRepository ledgerRepo, LedgerManager ledgerManager,
			DefaultOperationHandleRegisteration opReg, HashDigest ledgerHash, BlockchainKeypair contractKey) {
		// 创建新区块的交易处理器；
		LedgerBlock preBlock = ledgerRepo.getLatestBlock();
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(preBlock);

		// 加载合约
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		LedgerSecurityManager securityManager = getSecurityManager();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor,
				ledgerRepo, opReg);

		// 构建基于接口调用合约的交易请求，用于测试合约调用；
		TxBuilder txBuilder = new TxBuilder(ledgerHash);
		txBuilder.contracts().deploy(contractKey.getIdentity(), chainCode());
		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
		txReqBuilder.signAsEndpoint(parti0);
		txReqBuilder.signAsNode(parti0);
		TransactionRequest txReq = txReqBuilder.buildRequest();

		TransactionResponse resp = txbatchProcessor.schedule(txReq);
		OperationResult[] opResults = resp.getOperationResults();
		assertNull(opResults);

		// 提交区块；
		TransactionBatchResultHandle txResultHandle = txbatchProcessor.prepare();
		txResultHandle.commit();
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
		assertNull(block.getLedgerHash());
		assertNull(block.getPreviousHash());

		// 提交数据，写入存储；
		ldgEdt.commit();

		HashDigest ledgerHash = block.getHash();
		return ledgerHash;
	}

	private byte[] chainCode() {

		InputStream in = this.getClass().getResourceAsStream("/" + CONTRACT_JAR);

		return BytesUtils.copyToBytes(in);
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

	public static interface TxDefinitor {

		void buildTx(TxBuilder txBuilder);

	}
}
