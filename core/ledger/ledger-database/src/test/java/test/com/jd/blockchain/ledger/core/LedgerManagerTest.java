package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.stream.Stream;

import com.jd.blockchain.ledger.*;
import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.core.ContractAccountQuery;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.DataAccountQuery;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerInitializer;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.ledger.core.UserAccountQuery;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.transaction.ConsensusParticipantData;
import com.jd.blockchain.transaction.LedgerInitData;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

public class LedgerManagerTest {

	static {
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
		DataContractRegistry.register(ParticipantNode.class);
		DataContractRegistry.register(ParticipantRegisterOperation.class);
		DataContractRegistry.register(ParticipantStateUpdateOperation.class);
		DataContractRegistry.register(BlockBody.class);
		DataContractRegistry.register(CryptoProvider.class);
	}

	public static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private BlockchainKeypair parti0 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti1 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti2 = BlockchainKeyGenerator.getInstance().generate();
	private BlockchainKeypair parti3 = BlockchainKeyGenerator.getInstance().generate();

	private BlockchainKeypair[] participants = { parti0, parti1, parti2, parti3 };

	private SignatureFunction signatureFunction;

	@Before
	public void intialize() {
		signatureFunction = Crypto.getSignatureFunction("ED25519");
	}

	@Test
	public void testLedgerInit() {
		// 创建账本初始化配置；
		LedgerInitSetting initSetting = createLedgerInitSetting();
		
		// 采用基于内存的 Storage；
		MemoryKVStorage storage = new MemoryKVStorage();

		// 新建账本；
		LedgerEditor ldgEdt = LedgerInitializer.createLedgerEditor(initSetting, storage);

		// 创建一个模拟的创世交易；
		TransactionRequest genesisTxReq = LedgerTestUtils.createLedgerInitTxRequest(participants);

		// 记录交易，注册用户；
		LedgerTransactionContext txCtx = ldgEdt.newTransaction(genesisTxReq);
		LedgerDataset ldgDS = txCtx.getDataset();
		BlockchainKeypair userKP = BlockchainKeyGenerator.getInstance().generate();

		UserAccount userAccount = ldgDS.getUserAccountSet().register(userKP.getAddress(), userKP.getPubKey());
		userAccount.setProperty("Name", "孙悟空", -1);
		userAccount.setProperty("Age", "10000", -1);

		System.out.println("UserAddress=" + userAccount.getAddress());

		// 提交交易结果；
		LedgerTransaction tx = txCtx.commit(TransactionState.SUCCESS);

		assertEquals(genesisTxReq.getTransactionContent().getHash(), tx.getTransactionContent().getHash());
		assertEquals(0, tx.getBlockHeight());

		// 生成区块；
		LedgerBlock genesisBlock = ldgEdt.prepare();
		HashDigest ledgerHash = genesisBlock.getHash();

		assertEquals(0, genesisBlock.getHeight());
		assertNotNull(genesisBlock.getHash());
		assertNull(genesisBlock.getPreviousHash());
		// 创世区块的账本hash 为null；创世区块本身的哈希就代表了账本的哈希；
		assertNull(genesisBlock.getLedgerHash());

		// 提交数据，写入存储；
		ldgEdt.commit();
		
		assertNull(genesisBlock.getLedgerHash());
		assertNotNull(genesisBlock.getHash());

		// 重新加载并校验结果；
		LedgerManager reloadLedgerManager = new LedgerManager();
		LedgerRepository reloadLedgerRepo = reloadLedgerManager.register(ledgerHash, storage);

		HashDigest genesisHash = reloadLedgerRepo.getBlockHash(0);
		assertEquals(ledgerHash, genesisHash);

		LedgerBlock latestBlock = reloadLedgerRepo.getLatestBlock();
		assertEquals(0, latestBlock.getHeight());
		assertEquals(ledgerHash, latestBlock.getHash());
		// 创世区块的账本hash 为null；创世区块本身的哈希就代表了账本的哈希；
		assertNull(latestBlock.getLedgerHash());

		LedgerEditor editor1 = reloadLedgerRepo.createNextBlock();

		TxBuilder txBuilder = new TxBuilder(ledgerHash);
		BlockchainKeypair dataKey = BlockchainKeyGenerator.getInstance().generate();
		txBuilder.dataAccounts().register(dataKey.getIdentity());
		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
		DigitalSignature dgtsign = txReqBuilder.signAsEndpoint(userKP);
		TransactionRequest txRequest = txReqBuilder.buildRequest();

		LedgerTransactionContext txCtx1 = editor1.newTransaction(txRequest);
		txCtx1.getDataset().getDataAccountSet().register(dataKey.getAddress(), dataKey.getPubKey(), null);
		txCtx1.commit(TransactionState.SUCCESS);

		LedgerBlock block1 = editor1.prepare();
		editor1.commit();
		assertEquals(1, block1.getHeight());
		assertNotNull(block1.getHash());
		assertEquals(genesisHash, block1.getPreviousHash());
		assertEquals(ledgerHash, block1.getLedgerHash());

		latestBlock = reloadLedgerRepo.getLatestBlock();
		assertEquals(1, latestBlock.getHeight());
		assertEquals(block1.getHash(), latestBlock.getHash());

		showStorageKeys(storage);

		reloadLedgerManager = new LedgerManager();
		reloadLedgerRepo = reloadLedgerManager.register(ledgerHash, storage);
		latestBlock = reloadLedgerRepo.getLatestBlock();
		assertEquals(1, latestBlock.getHeight());
		assertEquals(block1.getHash(), latestBlock.getHash());

		DataAccountQuery dataAccountSet = reloadLedgerRepo.getDataAccountSet(latestBlock);
		UserAccountQuery userAccountSet = reloadLedgerRepo.getUserAccountSet(latestBlock);
		ContractAccountQuery contractAccountSet = reloadLedgerRepo.getContractAccountSet(latestBlock);

	}

	private void showStorageKeys(MemoryKVStorage storage) {
		// 输出写入的 kv；
		System.out.println("------------------- Storage Keys -------------------");
		Object[] keys = Stream.of(storage.getStorageKeySet().toArray(new Bytes[0])).map(p -> p.toString())
				.sorted((o1, o2) -> o1.compareTo(o2)).toArray();
		int i = 0;
		for (Object k : keys) {
			i++;
			System.out.println(i + ":" + k.toString());
		}
	}

	private LedgerInitSetting createLedgerInitSetting() {

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig defCryptoSetting = new CryptoConfig();

		defCryptoSetting.setSupportedProviders(supportedProviders);

		defCryptoSetting.setAutoVerifyHash(true);
		defCryptoSetting.setHashAlgorithm(ClassicAlgorithm.SHA256);

		LedgerInitData initSetting = new LedgerInitData();

		initSetting.setLedgerSeed(BytesUtils.toBytes("A Test Ledger seed!", "UTF-8"));
		initSetting.setCryptoSetting(defCryptoSetting);
		ConsensusParticipantData[] parties = new ConsensusParticipantData[4];
		parties[0] = new ConsensusParticipantData();
		parties[0].setId(0);
		parties[0].setName("John");
		AsymmetricKeypair kp0 = signatureFunction.generateKeypair();
		parties[0].setPubKey(kp0.getPubKey());
		parties[0].setAddress(AddressEncoding.generateAddress(kp0.getPubKey()));
		parties[0].setHostAddress(new NetworkAddress("127.0.0.1", 9000));
		parties[0].setParticipantState(ParticipantNodeState.ACTIVED);

		parties[1] = new ConsensusParticipantData();
		parties[1].setId(1);
		parties[1].setName("Mary");
		AsymmetricKeypair kp1 = signatureFunction.generateKeypair();
		parties[1].setPubKey(kp1.getPubKey());
		parties[1].setAddress(AddressEncoding.generateAddress(kp1.getPubKey()));
		parties[1].setHostAddress(new NetworkAddress("127.0.0.1", 9010));
		parties[1].setParticipantState(ParticipantNodeState.ACTIVED);

		parties[2] = new ConsensusParticipantData();
		parties[2].setId(2);
		parties[2].setName("Jerry");
		AsymmetricKeypair kp2 = signatureFunction.generateKeypair();
		parties[2].setPubKey(kp2.getPubKey());
		parties[2].setAddress(AddressEncoding.generateAddress(kp2.getPubKey()));
		parties[2].setHostAddress(new NetworkAddress("127.0.0.1", 9020));
		parties[2].setParticipantState(ParticipantNodeState.ACTIVED);

		parties[3] = new ConsensusParticipantData();
		parties[3].setId(3);
		parties[3].setName("Tom");
		AsymmetricKeypair kp3 = signatureFunction.generateKeypair();
		parties[3].setPubKey(kp3.getPubKey());
		parties[3].setAddress(AddressEncoding.generateAddress(kp3.getPubKey()));
		parties[3].setHostAddress(new NetworkAddress("127.0.0.1", 9030));
		parties[3].setParticipantState(ParticipantNodeState.ACTIVED);

		initSetting.setConsensusParticipants(parties);

		return initSetting;
	}

	//
}
