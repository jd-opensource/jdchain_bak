package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.UserRoles;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.UserRoleDataset;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;

public class UserRoleDatasetTest {

	private static final String[] SUPPORTED_PROVIDER_NAMES = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private static final CryptoAlgorithm HASH_ALGORITHM = Crypto.getAlgorithm("SHA256");

	private static final CryptoProvider[] SUPPORTED_PROVIDERS = new CryptoProvider[SUPPORTED_PROVIDER_NAMES.length];
	static {
		for (int i = 0; i < SUPPORTED_PROVIDER_NAMES.length; i++) {
			SUPPORTED_PROVIDERS[i] = Crypto.getProvider(SUPPORTED_PROVIDER_NAMES[i]);
		}
	}

	@Test
	public void testAddUserRoles() {
		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setSupportedProviders(SUPPORTED_PROVIDERS);
		cryptoConfig.setHashAlgorithm(HASH_ALGORITHM);

		MemoryKVStorage testStorage = new MemoryKVStorage();
		String prefix = "user-roles/";
		UserRoleDataset userRolesDataset = new UserRoleDataset(cryptoConfig, prefix, testStorage, testStorage);

		BlockchainKeypair bckp = BlockchainKeyGenerator.getInstance().generate();
		String[] authRoles = { "DEFAULT", "MANAGER" };
		userRolesDataset.addUserRoles(bckp.getAddress(), RolesPolicy.UNION, authRoles);

		userRolesDataset.commit();

		assertEquals(1, userRolesDataset.getUserCount());
		UserRoles userRoles = userRolesDataset.getUserRoles(bckp.getAddress());
		assertNotNull(userRoles);
		String[] roles = userRoles.getRoles();
		assertEquals(2, roles.length);
		assertArrayEquals(authRoles, roles);
		assertEquals(RolesPolicy.UNION, userRoles.getPolicy());
	}

}
