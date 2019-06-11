package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.DataAccount;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.ledger.core.impl.LedgerTransactionalEditor;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.transaction.ConsensusParticipantData;
import com.jd.blockchain.transaction.LedgerInitSettingData;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

public class LedgerEditerTest {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	static {
		DataContractRegistry.register(com.jd.blockchain.ledger.TransactionContent.class);
		DataContractRegistry.register(com.jd.blockchain.ledger.UserRegisterOperation.class);
		DataContractRegistry.register(com.jd.blockchain.ledger.BlockBody.class);
	}

	private static final String LEDGER_KEY_PREFIX = "LDG://";
	private SignatureFunction signatureFunction;

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
		LedgerInitSetting initSetting = createLedgerInitSetting();

		// 创建账本；
		return LedgerTransactionalEditor.createEditor(initSetting, LEDGER_KEY_PREFIX, storage, storage);
	}

	private LedgerTransactionContext createGenisisTx(LedgerEditor ldgEdt) {
		TransactionRequest genesisTxReq = LedgerTestUtils.createTxRequest(null, signatureFunction);

		LedgerTransactionContext txCtx = ldgEdt.newTransaction(genesisTxReq);
		
		return txCtx;
	}

	@SuppressWarnings("unused")
	@Test
	public void testWriteDataAccoutKvOp() {
		LedgerEditor ldgEdt = createLedgerInitEditor();
		LedgerTransactionContext genisisTxCtx = createGenisisTx(ldgEdt);
		LedgerDataSet ldgDS = genisisTxCtx.getDataSet();
		
		
		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
		BlockchainKeypair dataKP = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());

		DataAccount dataAccount = ldgDS.getDataAccountSet().register(dataKP.getAddress(), dataKP.getPubKey(), null);

		dataAccount.setBytes(Bytes.fromString("A"), "abc", -1);

		LedgerTransaction tx = genisisTxCtx.commit(TransactionState.SUCCESS);
		LedgerBlock block = ldgEdt.prepare();
		// 提交数据，写入存储；
		ldgEdt.commit();

		// 预期这是第1个区块；
		assertNotNull(block);
		assertNotNull(block.getHash());
		assertEquals(0, block.getHeight());

		// 验证数据读写的一致性；
		BytesValue bytes = dataAccount.getBytes("A");
		assertEquals(DataType.TEXT, bytes.getType());
		String textValue = bytes.getValue().toUTF8String();
		assertEquals("abc", textValue);
	}

	/**
	 * 测试创建账本；
	 */
	@Test
	public void testGennesisBlockCreation() {
		LedgerEditor ldgEdt = createLedgerInitEditor();
		LedgerTransactionContext genisisTxCtx = createGenisisTx(ldgEdt);
		LedgerDataSet ldgDS = genisisTxCtx.getDataSet();

		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
		BlockchainKeypair userKP = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
		UserAccount userAccount = ldgDS.getUserAccountSet().register(userKP.getAddress(), userKP.getPubKey());
		userAccount.setProperty("Name", "孙悟空", -1);
		userAccount.setProperty("Age", "10000", -1);

		LedgerTransaction tx = genisisTxCtx.commit(TransactionState.SUCCESS);

		TransactionRequest genesisTxReq = genisisTxCtx.getRequestTX();
		assertEquals(genesisTxReq.getTransactionContent().getHash(), tx.getTransactionContent().getHash());
		assertEquals(0, tx.getBlockHeight());

		LedgerBlock block = ldgEdt.prepare();

		assertEquals(0, block.getHeight());
		assertNotNull(block.getHash());
		assertNull(block.getPreviousHash());

		assertEquals(block.getHash(), block.getLedgerHash());

		// 提交数据，写入存储；
		ldgEdt.commit();

	}

	private LedgerInitSetting createLedgerInitSetting() {
		SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig defCryptoSetting = new CryptoConfig();
		defCryptoSetting.setSupportedProviders(supportedProviders);
		defCryptoSetting.setAutoVerifyHash(true);
		defCryptoSetting.setHashAlgorithm(ClassicAlgorithm.SHA256);

		LedgerInitSettingData initSetting = new LedgerInitSettingData();

		initSetting.setLedgerSeed(BytesUtils.toBytes("A Test Ledger seed!", "UTF-8"));
		initSetting.setCryptoSetting(defCryptoSetting);
		ConsensusParticipantData[] parties = new ConsensusParticipantData[2];
		parties[0] = new ConsensusParticipantData();
		parties[0].setId(0);
		parties[0].setName("John");
		AsymmetricKeypair kp0 = signFunc.generateKeypair();
		parties[0].setPubKey(kp0.getPubKey());
		parties[0].setAddress(AddressEncoding.generateAddress(kp0.getPubKey()).toBase58());
		parties[0].setHostAddress(new NetworkAddress("192.168.1.6", 9000));

		parties[1] = new ConsensusParticipantData();
		parties[1].setId(1);
		parties[1].setName("John");
		AsymmetricKeypair kp1 = signFunc.generateKeypair();
		parties[1].setPubKey(kp1.getPubKey());
		parties[1].setAddress(AddressEncoding.generateAddress(kp1.getPubKey()).toBase58());
		parties[1].setHostAddress(new NetworkAddress("192.168.1.7", 9000));
		initSetting.setConsensusParticipants(parties);

		return initSetting;
	}
}
