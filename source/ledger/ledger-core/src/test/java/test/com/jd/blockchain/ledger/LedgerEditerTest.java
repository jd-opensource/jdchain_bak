package test.com.jd.blockchain.ledger;

import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;

import org.junit.Test;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.crypto.impl.AsymmtricCryptographyImpl;
import com.jd.blockchain.ledger.core.impl.LedgerTransactionalEditor;
import com.jd.blockchain.ledger.data.ConsensusParticipantData;
import com.jd.blockchain.ledger.data.LedgerInitSettingData;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import static org.junit.Assert.*;

public class LedgerEditerTest {

	static {
		DataContractRegistry.register(com.jd.blockchain.ledger.TransactionContent.class);
		DataContractRegistry.register(com.jd.blockchain.ledger.UserRegisterOperation.class);
		DataContractRegistry.register(com.jd.blockchain.ledger.BlockBody.class);
	}

	String ledgerKeyPrefix = "LDG://";
	AsymmetricCryptography asymmetricCryptography = new AsymmtricCryptographyImpl();
	SignatureFunction signatureFunction = asymmetricCryptography.getSignatureFunction(CryptoAlgorithm.ED25519);

	// 存储；
	MemoryKVStorage storage = new MemoryKVStorage();

	TransactionRequest genesisTxReq = LedgerTestUtils.createTxRequest(null, signatureFunction);

	// 创建初始化配置；
	LedgerInitSetting initSetting = createLedgerInitSetting();

	// 创建账本；
	LedgerEditor ldgEdt = LedgerTransactionalEditor.createEditor(initSetting, ledgerKeyPrefix, storage, storage);
	LedgerTransactionContext txCtx = ldgEdt.newTransaction(genesisTxReq);
	LedgerDataSet ldgDS = txCtx.getDataSet();

	CryptoKeyPair cryptoKeyPair = signatureFunction.generateKeyPair();

	@Test
	public void testWriteDataAccoutKvOp() {

		BlockchainKeyPair dataKP = new BlockchainKeyPair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());

		DataAccount dataAccount = ldgDS.getDataAccountSet().register(dataKP.getAddress(), dataKP.getPubKey(), null);

		dataAccount.setBytes(Bytes.fromString("A"), "abc".getBytes(), -1);

		LedgerTransaction tx = txCtx.commit(TransactionState.SUCCESS);
		LedgerBlock block = ldgEdt.prepare();
		// 提交数据，写入存储；
		ldgEdt.commit();

		byte[] bytes = dataAccount.getBytes("A");
		assertArrayEquals("abc".getBytes(), bytes);
	}

	/**
	 * 测试创建账本；
	 */
	@Test
	public void testLedgerEditorCreation() {

		BlockchainKeyPair userKP = new BlockchainKeyPair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
		UserAccount userAccount = ldgDS.getUserAccountSet().register(userKP.getAddress(), userKP.getPubKey());
		userAccount.setProperty("Name", "孙悟空", -1);
		userAccount.setProperty("Age", "10000", -1);

		LedgerTransaction tx = txCtx.commit(TransactionState.SUCCESS);

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
		CryptoConfig defCryptoSetting = new CryptoConfig();
		defCryptoSetting.setAutoVerifyHash(true);
		defCryptoSetting.setHashAlgorithm(CryptoAlgorithm.SHA256);

		LedgerInitSettingData initSetting = new LedgerInitSettingData();

		initSetting.setLedgerSeed(BytesUtils.toBytes("A Test Ledger seed!", "UTF-8"));
		initSetting.setCryptoSetting(defCryptoSetting);
		ConsensusParticipantData[] parties = new ConsensusParticipantData[2];
		parties[0] = new ConsensusParticipantData();
		parties[0].setId(0);
		parties[0].setName("John");
		CryptoKeyPair kp0 = CryptoUtils.sign(CryptoAlgorithm.ED25519).generateKeyPair();
		parties[0].setPubKey(kp0.getPubKey());
		parties[0].setAddress(AddressEncoding.generateAddress(kp0.getPubKey()).toBase58());
		parties[0].setHostAddress(new NetworkAddress("192.168.1.6", 9000));

		parties[1] = new ConsensusParticipantData();
		parties[1].setId(1);
		parties[1].setName("John");
		CryptoKeyPair kp1 = CryptoUtils.sign(CryptoAlgorithm.ED25519).generateKeyPair();
		parties[1].setPubKey(kp1.getPubKey());
		parties[1].setAddress(AddressEncoding.generateAddress(kp1.getPubKey()).toBase58());
		parties[1].setHostAddress(new NetworkAddress("192.168.1.7", 9000));
		initSetting.setConsensusParticipants(parties);

		return initSetting;
	}
}
