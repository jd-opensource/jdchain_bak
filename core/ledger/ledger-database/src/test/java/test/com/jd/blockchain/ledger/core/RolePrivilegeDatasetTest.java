package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.RolePrivileges;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.RolePrivilegeDataset;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;

public class RolePrivilegeDatasetTest {

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
	public void testAddRolePrivilege() {

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setSupportedProviders(SUPPORTED_PROVIDERS);
		cryptoConfig.setHashAlgorithm(HASH_ALGORITHM);

		MemoryKVStorage testStorage = new MemoryKVStorage();

		String roleName = "DEFAULT";
		String prefix = "role-privilege/";
		RolePrivilegeDataset rolePrivilegeDataset = new RolePrivilegeDataset(cryptoConfig, prefix, testStorage,
				testStorage);
		rolePrivilegeDataset.addRolePrivilege(roleName, new LedgerPermission[] { LedgerPermission.REGISTER_USER },
				new TransactionPermission[] { TransactionPermission.CONTRACT_OPERATION });

		rolePrivilegeDataset.commit();

		RolePrivileges rolePrivilege = rolePrivilegeDataset.getRolePrivilege(roleName);
		assertNotNull(rolePrivilege);

		HashDigest rootHash = rolePrivilegeDataset.getRootHash();
		RolePrivilegeDataset newRolePrivilegeDataset = new RolePrivilegeDataset(rootHash, cryptoConfig, prefix,
				testStorage, testStorage, true);
		rolePrivilege = newRolePrivilegeDataset.getRolePrivilege(roleName);
		assertNotNull(rolePrivilege);
		
		assertTrue(rolePrivilege.getLedgerPrivilege().isEnable(LedgerPermission.REGISTER_USER));
		assertTrue(rolePrivilege.getTransactionPrivilege().isEnable(TransactionPermission.CONTRACT_OPERATION));
		
		
	}

}
