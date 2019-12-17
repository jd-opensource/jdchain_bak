package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.core.CompositeAccount;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.MerkleAccountSet;
import com.jd.blockchain.ledger.core.OpeningAccessPolicy;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.utils.Bytes;

public class MerkleAccountSetTest {

	@Test
	public void testRegister() {
		final OpeningAccessPolicy POLICY = new OpeningAccessPolicy();

		final MemoryKVStorage STORAGE = new MemoryKVStorage();

		Bytes KEY_PREFIX = Bytes.fromString("/ACCOUNT");

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(LedgerTestUtils.getContextProviders());
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setHashAlgorithm(Crypto.getAlgorithm("SHA256"));

		MerkleAccountSet accountset = new MerkleAccountSet(cryptoConfig, KEY_PREFIX, STORAGE, STORAGE, POLICY);

		BlockchainKeypair key1 = BlockchainKeyGenerator.getInstance().generate();
		accountset.register(key1.getIdentity());

		accountset.commit();

		CompositeAccount acc1 = accountset.getAccount(key1.getAddress());
		assertNotNull(acc1);
		assertEquals(0, accountset.getVersion(key1.getAddress()));
		
		acc1.getDataset().setValue("K1", TypedValue.fromText("V0"), -1);

		TypedValue v1 = acc1.getDataset().getValue("K1");
		assertNotNull(v1);
		assertEquals(0, acc1.getDataset().getVersion("K1"));

		accountset.commit();

		v1 = acc1.getDataset().getValue("K1");
		assertNotNull(v1);
		assertEquals(0, acc1.getDataset().getVersion("K1"));
	}

}
