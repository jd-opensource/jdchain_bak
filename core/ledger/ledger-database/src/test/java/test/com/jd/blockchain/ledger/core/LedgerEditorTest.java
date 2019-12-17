package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.core.DataAccount;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.LedgerTransactionalEditor;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;

public class LedgerEditorTest {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	static {
		DataContractRegistry.register(com.jd.blockchain.ledger.TransactionContent.class);
		DataContractRegistry.register(com.jd.blockchain.ledger.UserRegisterOperation.class);
		DataContractRegistry.register(com.jd.blockchain.ledger.BlockBody.class);
	}

	private static final String LEDGER_KEY_PREFIX = "LDG://";
	private SignatureFunction signatureFunction;

	private BlockchainKeypair parti0 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti1 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti2 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti3 = BlockchainKeyGenerator.getInstance().generate();

	private BlockchainKeypair[] participants = { parti0, parti1, parti2, parti3 };

	/**
	 * 初始化一个;
	 */
	@Before
	public void beforeTest() {
		signatureFunction = Crypto.getSignatureFunction("ED25519");
	}

	/**
	 * @return
	 */
	private LedgerEditor createLedgerInitEditor() {
		// 存储；
		MemoryKVStorage storage = new MemoryKVStorage();

		// 创建初始化配置；
		LedgerInitSetting initSetting = LedgerTestUtils.createLedgerInitSetting();

		// 创建账本；
		return LedgerTransactionalEditor.createEditor(initSetting, LEDGER_KEY_PREFIX, storage, storage);
	}

	private LedgerTransactionContext createGenisisTx(LedgerEditor ldgEdt, BlockchainKeypair[] partis) {
		TransactionRequest genesisTxReq = LedgerTestUtils.createLedgerInitTxRequest(partis);

		LedgerTransactionContext txCtx = ldgEdt.newTransaction(genesisTxReq);

		return txCtx;
	}

	@SuppressWarnings("unused")
	@Test
	public void testWriteDataAccoutKvOp() {
		LedgerEditor ldgEdt = createLedgerInitEditor();
		LedgerTransactionContext genisisTxCtx = createGenisisTx(ldgEdt, participants);
		LedgerDataset ldgDS = genisisTxCtx.getDataset();

		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
		BlockchainKeypair dataKP = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());

		DataAccount dataAccount = ldgDS.getDataAccountSet().register(dataKP.getAddress(), dataKP.getPubKey(), null);

		dataAccount.getDataset().setValue("A", TypedValue.fromText("abc"), -1);

		LedgerTransaction tx = genisisTxCtx.commit(TransactionState.SUCCESS);
		LedgerBlock block = ldgEdt.prepare();
		// 提交数据，写入存储；
		ldgEdt.commit();

		// 预期这是第1个区块；
		assertNotNull(block);
		assertNotNull(block.getHash());
		assertEquals(0, block.getHeight());

		// 验证数据读写的一致性；
		BytesValue bytes = dataAccount.getDataset().getValue("A");
		assertEquals(DataType.TEXT, bytes.getType());
		String textValue = bytes.getBytes().toUTF8String();
		assertEquals("abc", textValue);
	}

	/**
	 * 测试创建账本；
	 */
	@Test
	public void testGennesisBlockCreation() {
		LedgerEditor ldgEdt = createLedgerInitEditor();
		LedgerTransactionContext genisisTxCtx = createGenisisTx(ldgEdt, participants);
		LedgerDataset ldgDS = genisisTxCtx.getDataset();

		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
		BlockchainKeypair userKP = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
		UserAccount userAccount = ldgDS.getUserAccountSet().register(userKP.getAddress(), userKP.getPubKey());
		userAccount.setProperty("Name", "孙悟空", -1);
		userAccount.setProperty("Age", "10000", -1);

		LedgerTransaction tx = genisisTxCtx.commit(TransactionState.SUCCESS);

		TransactionRequest genesisTxReq = genisisTxCtx.getTransactionRequest();
		assertEquals(genesisTxReq.getTransactionContent().getHash(), tx.getTransactionContent().getHash());
		assertEquals(0, tx.getBlockHeight());

		LedgerBlock block = ldgEdt.prepare();

		assertEquals(0, block.getHeight());
		assertNotNull(block.getHash());
		assertNull(block.getLedgerHash());
		assertNull(block.getPreviousHash());

		// 提交数据，写入存储；
		ldgEdt.commit();

	}

}
