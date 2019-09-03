package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;
import org.mockito.Mockito;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesData;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.EndpointRequest;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
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
import com.jd.blockchain.ledger.core.impl.TransactionBatchProcessor;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.transaction.BooleanValueHolder;
import static com.jd.blockchain.transaction.ContractReturnValue.*;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.Bytes;

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
	}

	private static final String LEDGER_KEY_PREFIX = "LDG://";

	private BlockchainKeypair parti0 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti1 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti2 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti3 = BlockchainKeyGenerator.getInstance().generate();

	// 采用基于内存的 Storage；
	private MemoryKVStorage storage = new MemoryKVStorage();

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
		contractInvokingHandle.setup(contractAddress, TestContract.class, contractInstance);

		// 注册合约处理器；
		DefaultOperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
		opReg.insertAsTopPriority(contractInvokingHandle);

		// 发布指定地址合约
		deploy(ledgerRepo, ledgerManager, opReg, ledgerHash, contractKey);

		// 创建新区块的交易处理器；
		LedgerBlock preBlock = ledgerRepo.getLatestBlock();
		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(preBlock);

		// 加载合约
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(newBlockEditor, previousBlockDataset,
				opReg, ledgerManager);

		// 构建基于接口调用合约的交易请求，用于测试合约调用；
		TxBuilder txBuilder = new TxBuilder(ledgerHash);
		TestContract contractProxy = txBuilder.contract(contractAddress, TestContract.class);
		TestContract contractProxy1 = txBuilder.contract(contractAddress, TestContract.class);

		String asset = "AK";
		long issueAmount = new Random().nextLong();
		when(contractInstance.issue(anyString(), anyLong())).thenReturn(issueAmount);
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

		byte[] expectedRetnBytes = BinaryProtocol.encode(BytesData.fromInt64(issueAmount), BytesValue.class);
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

	@Test
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
		opReg.insertAsTopPriority(contractInvokingHandle);

		// 发布指定地址合约
		deploy(ledgerRepo, ledgerManager, opReg, ledgerHash, contractKey);

		// 创建新区块的交易处理器；
		LedgerBlock preBlock = ledgerRepo.getLatestBlock();
		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(preBlock);

		// 加载合约
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(newBlockEditor, previousBlockDataset,
				opReg, ledgerManager);

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

		BytesValue latestValue = ledgerRepo.getDataAccountSet().getDataAccount(kpDataAccount.getAddress()).getBytes(key,
				-1);
		System.out.printf("latest value=[%s] %s \r\n", latestValue.getType(), latestValue.getValue().toUTF8String());
		

		boolean readable = readableHolder.get();
		assertTrue(readable);

		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
		assertEquals(preBlock.getHeight() + 1, latestBlock.getHeight());
		assertEquals(resp1.getBlockHeight(), latestBlock.getHeight());
		assertEquals(resp1.getBlockHash(), latestBlock.getHash());
	}

	private void deploy(LedgerRepository ledgerRepo, LedgerManager ledgerManager,
			DefaultOperationHandleRegisteration opReg, HashDigest ledgerHash, BlockchainKeypair contractKey) {
		// 创建新区块的交易处理器；
		LedgerBlock preBlock = ledgerRepo.getLatestBlock();
		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(preBlock);

		// 加载合约
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(newBlockEditor, previousBlockDataset,
				opReg, ledgerManager);

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
		assertNull(block.getLedgerHash());
		assertNull(block.getPreviousHash());

		// 提交数据，写入存储；
		ldgEdt.commit();

		HashDigest ledgerHash = block.getHash();
		return ledgerHash;
	}

	private byte[] chainCode() {
		byte[] chainCode = new byte[1024];
		new Random().nextBytes(chainCode);
		return chainCode;
	}
}
