package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.MerkleAccount;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.utils.Bytes;

/**
 * 
 * @author huanghaiquan
 *
 */
public class BaseAccountTest {

	public static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	@Test
	public void basicTest() {
		String keyPrefix = "";
		MemoryKVStorage testStorage = new MemoryKVStorage();

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig cryptoConf = new CryptoConfig();
		cryptoConf.setSupportedProviders(supportedProviders);
		cryptoConf.setAutoVerifyHash(true);
		cryptoConf.setHashAlgorithm(ClassicAlgorithm.SHA256);

//		OpeningAccessPolicy accPlc = new OpeningAccessPolicy();

		BlockchainKeypair bck = BlockchainKeyGenerator.getInstance().generate();

		// 新建账户；
		MerkleAccount baseAccount = new MerkleAccount(bck.getIdentity(), cryptoConf, Bytes.fromString(keyPrefix),
				testStorage, testStorage);
		assertTrue(baseAccount.isUpdated());//初始化新账户时，先写入PubKey；
		assertFalse(baseAccount.isReadonly());

		// 在空白状态下写入数据；
		long v = baseAccount.getDataset().setValue("A", TypedValue.fromText("VALUE_A"), 0);
		// 预期失败；
		assertEquals(-1, v);

		v = baseAccount.getDataset().setValue("A", TypedValue.fromText("VALUE_A"), 1);
		// 预期失败；
		assertEquals(-1, v);

		v = baseAccount.getDataset().setValue("A", TypedValue.fromText("VALUE_A"), -1);
		// 预期成功；
		assertEquals(0, v);

		v = baseAccount.getDataset().setValue("A", TypedValue.fromText("VALUE_A-1"), -1);
		// 已经存在版本，指定版本号-1，预期导致失败；
		assertEquals(-1, v);

		baseAccount.commit();
		v = 0;
		for (int i = 0; i < 10; i++) {
			long s = baseAccount.getDataset().setValue("A", TypedValue.fromText("VALUE_A_" + i), v);
			baseAccount.commit();
			// 预期成功；
			assertEquals(v + 1, s);
			v++;
		}

		v = baseAccount.getDataset().setValue("A", TypedValue.fromText("VALUE_A_" + v), v + 1);
		// 预期成功；
		assertEquals(-1, v);

		System.out.println("============== commit ==============");
		baseAccount.commit();

	}

}
