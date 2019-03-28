package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.core.AccountSet;
import com.jd.blockchain.ledger.core.BaseAccount;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.impl.OpeningAccessPolicy;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;

public class AccountSetTest {

	@Test
	public void test() {
		OpeningAccessPolicy accessPolicy = new OpeningAccessPolicy();
		
		MemoryKVStorage storage = new MemoryKVStorage();

		CryptoConfig cryptoConf = new CryptoConfig();
		cryptoConf.setAutoVerifyHash(true);
		cryptoConf.setHashAlgorithm(ClassicCryptoService.SHA256_ALGORITHM);
		
		String keyPrefix = "";
		AccountSet accset = new AccountSet(cryptoConf,keyPrefix, storage, storage, accessPolicy);
		
		BlockchainKeyPair userKey = BlockchainKeyGenerator.getInstance().generate();
		accset.register(userKey.getAddress(), userKey.getPubKey());
		
		BaseAccount userAcc = accset.getAccount(userKey.getAddress());
		assertNotNull(userAcc);
		assertTrue(accset.contains(userKey.getAddress()));
		
		accset.commit();
		HashDigest rootHash = accset.getRootHash();
		assertNotNull(rootHash);
		
		AccountSet reloadAccSet = new AccountSet(rootHash, cryptoConf, keyPrefix,storage, storage, true, accessPolicy);
		BaseAccount reloadUserAcc = reloadAccSet.getAccount(userKey.getAddress());
		assertNotNull(reloadUserAcc);
		assertTrue(reloadAccSet.contains(userKey.getAddress()));
		
		assertEquals(userAcc.getAddress(), reloadUserAcc.getAddress());
		assertEquals(userAcc.getPubKey(), reloadUserAcc.getPubKey());
	}

}
