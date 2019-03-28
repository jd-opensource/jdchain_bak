package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bouncycastle.util.io.Streams;
import org.junit.Test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.ledger.BlockBody;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.ContractAccountSet;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.DataAccountSet;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.ledger.core.UserAccountSet;
import com.jd.blockchain.ledger.core.impl.LedgerManager;
import com.jd.blockchain.ledger.data.ConsensusParticipantData;
import com.jd.blockchain.ledger.data.LedgerInitSettingData;
import com.jd.blockchain.ledger.data.TxBuilder;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

public class LedgerManagerTest {
	
	static {
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
		DataContractRegistry.register(BlockBody.class);
	}

	private static SignatureFunction signatureFunction = CryptoUtils.sign(ClassicCryptoService.ED25519_ALGORITHM);

	@Test
	public void testLedgerInit() {
		// 创建账本初始化配置；
		LedgerInitSetting initSetting = createLedgerInitSetting();

		// 采用基于内存的 Storage；
		MemoryKVStorage storage = new MemoryKVStorage();

		// 新建账本；
		LedgerManager ledgerManager = new LedgerManager();
		LedgerEditor ldgEdt = ledgerManager.newLedger(initSetting, storage);

		// 创建一个模拟的创世交易；
		TransactionRequest genesisTxReq = LedgerTestUtils.createTxRequest(null, signatureFunction);

		// 记录交易，注册用户；
		LedgerTransactionContext txCtx = ldgEdt.newTransaction(genesisTxReq);
		LedgerDataSet ldgDS = txCtx.getDataSet();
		BlockchainKeyPair userKP = BlockchainKeyGenerator.getInstance().generate();;
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
		assertEquals(ledgerHash, genesisBlock.getLedgerHash());

		// 提交数据，写入存储；
		ldgEdt.commit();
		
		//重新加载并校验结果；
		LedgerManager reloadLedgerManager = new LedgerManager();
		LedgerRepository reloadLedgerRepo = reloadLedgerManager.register(ledgerHash, storage);
		
		HashDigest genesisHash= reloadLedgerRepo.getBlockHash(0);
		assertEquals(ledgerHash, genesisHash);
		
		LedgerBlock latestBlock = reloadLedgerRepo.getLatestBlock();
		assertEquals(0, latestBlock.getHeight());
		assertEquals(ledgerHash, latestBlock.getHash());
		assertEquals(ledgerHash, latestBlock.getLedgerHash());
		
		LedgerEditor editor1 = reloadLedgerRepo.createNextBlock();
		
		TxBuilder txBuilder = new TxBuilder(ledgerHash);
		BlockchainKeyPair dataKey = BlockchainKeyGenerator.getInstance().generate();
		txBuilder.dataAccounts().register(dataKey.getIdentity());
		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
		DigitalSignature dgtsign = txReqBuilder.signAsEndpoint(userKP);
		TransactionRequest txRequest = txReqBuilder.buildRequest();
		
		LedgerTransactionContext txCtx1 = editor1.newTransaction(txRequest);
		txCtx1.getDataSet().getDataAccountSet().register(dataKey.getAddress(), dataKey.getPubKey(), null);
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
		
		DataAccountSet dataAccountSet = reloadLedgerRepo.getDataAccountSet(latestBlock);
		UserAccountSet userAccountSet = reloadLedgerRepo.getUserAccountSet(latestBlock);
		ContractAccountSet contractAccountSet = reloadLedgerRepo.getContractAccountSet(latestBlock);

	}



	private void showStorageKeys(MemoryKVStorage storage) {
		// 输出写入的 kv；
		System.out.println("------------------- Storage Keys -------------------");
		Object[] keys = Stream.of(storage.getStorageKeySet().toArray(new Bytes[0])).map(p -> p.toString()).sorted((o1, o2) -> o1.compareTo(o2)).toArray();
		int i = 0;
		for (Object k : keys) {
			i++;
			System.out.println(i + ":" + k.toString());
		}
	}


	private LedgerInitSetting createLedgerInitSetting() {
		CryptoConfig defCryptoSetting = new CryptoConfig();
		defCryptoSetting.setAutoVerifyHash(true);
		defCryptoSetting.setHashAlgorithm(ClassicCryptoService.SHA256_ALGORITHM);

		LedgerInitSettingData initSetting = new LedgerInitSettingData();

		initSetting.setLedgerSeed(BytesUtils.toBytes("A Test Ledger seed!", "UTF-8"));
		initSetting.setCryptoSetting(defCryptoSetting);
		ConsensusParticipantData[] parties = new ConsensusParticipantData[4];
		parties[0] = new ConsensusParticipantData();
		parties[0].setId(0);
		parties[0].setName("John");
		CryptoKeyPair kp0 = CryptoUtils.sign(ClassicCryptoService.ED25519_ALGORITHM).generateKeyPair();
		parties[0].setPubKey(kp0.getPubKey());
		parties[0].setAddress(AddressEncoding.generateAddress(kp0.getPubKey()).toBase58());
		parties[0].setHostAddress(new NetworkAddress("127.0.0.1", 9000));

		parties[1] = new ConsensusParticipantData();
		parties[1].setId(1);
		parties[1].setName("Mary");
		CryptoKeyPair kp1 = CryptoUtils.sign(ClassicCryptoService.ED25519_ALGORITHM).generateKeyPair();
		parties[1].setPubKey(kp1.getPubKey());
		parties[1].setAddress(AddressEncoding.generateAddress(kp1.getPubKey()).toBase58());
		parties[1].setHostAddress(new NetworkAddress("127.0.0.1", 9010));

		parties[2] = new ConsensusParticipantData();
		parties[2].setId(2);
		parties[2].setName("Jerry");
		CryptoKeyPair kp2 = CryptoUtils.sign(ClassicCryptoService.ED25519_ALGORITHM).generateKeyPair();
		parties[2].setPubKey(kp2.getPubKey());
		parties[2].setAddress(AddressEncoding.generateAddress(kp2.getPubKey()).toBase58());
		parties[2].setHostAddress(new NetworkAddress("127.0.0.1", 9020));

		parties[3] = new ConsensusParticipantData();
		parties[3].setId(3);
		parties[3].setName("Tom");
		CryptoKeyPair kp3 = CryptoUtils.sign(ClassicCryptoService.ED25519_ALGORITHM).generateKeyPair();
		parties[3].setPubKey(kp3.getPubKey());
		parties[3].setAddress(AddressEncoding.generateAddress(kp3.getPubKey()).toBase58());
		parties[3].setHostAddress(new NetworkAddress("127.0.0.1", 9030));

		initSetting.setConsensusParticipants(parties);

		return initSetting;
	}
	
//	
}
