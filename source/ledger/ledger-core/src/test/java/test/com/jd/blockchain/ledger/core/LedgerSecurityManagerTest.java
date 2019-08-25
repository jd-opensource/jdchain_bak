package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.Privileges;
import com.jd.blockchain.ledger.RolePrivilegeSettings;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.UserRoleSettings;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.LedgerSecurityManager;
import com.jd.blockchain.ledger.core.LedgerSecurityManagerImpl;
import com.jd.blockchain.ledger.core.MultiIdsPolicy;
import com.jd.blockchain.ledger.core.RolePrivilegeDataset;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.UserRoleDataset;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.utils.Bytes;

public class LedgerSecurityManagerTest {

	private static final String[] SUPPORTED_PROVIDER_NAMES = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private static final CryptoAlgorithm HASH_ALGORITHM = Crypto.getAlgorithm("SHA256");

	private static final CryptoProvider[] SUPPORTED_PROVIDERS = new CryptoProvider[SUPPORTED_PROVIDER_NAMES.length];

	private static final CryptoSetting CRYPTO_SETTINGS;

	static {
		for (int i = 0; i < SUPPORTED_PROVIDER_NAMES.length; i++) {
			SUPPORTED_PROVIDERS[i] = Crypto.getProvider(SUPPORTED_PROVIDER_NAMES[i]);
		}

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setSupportedProviders(SUPPORTED_PROVIDERS);
		cryptoConfig.setHashAlgorithm(HASH_ALGORITHM);

		CRYPTO_SETTINGS = cryptoConfig;
	}

	private RolePrivilegeSettings initRoles(MemoryKVStorage testStorage, String[] roles, Privileges[] privilege) {
		String prefix = "role-privilege/";
		RolePrivilegeDataset rolePrivilegeDataset = new RolePrivilegeDataset(CRYPTO_SETTINGS, prefix, testStorage,
				testStorage);
		for (int i = 0; i < roles.length; i++) {
			rolePrivilegeDataset.addRolePrivilege(roles[i], privilege[i]);
		}

		rolePrivilegeDataset.commit();

		return rolePrivilegeDataset;
	}

	private UserRoleSettings initUserRoless(MemoryKVStorage testStorage, Bytes[] userAddresses, RolesPolicy[] policies,
			String[][] roles) {
		String prefix = "user-roles/";
		UserRoleDataset userRolesDataset = new UserRoleDataset(CRYPTO_SETTINGS, prefix, testStorage, testStorage);

		for (int i = 0; i < userAddresses.length; i++) {
			userRolesDataset.addUserRoles(userAddresses[i], policies[i], roles[i]);
		}

		userRolesDataset.commit();

		return userRolesDataset;
	}

	@Test
	public void testGetSecurityPolicy() {
		MemoryKVStorage testStorage = new MemoryKVStorage();

		final BlockchainKeypair kpManager = BlockchainKeyGenerator.getInstance().generate();
		final BlockchainKeypair kpEmployee = BlockchainKeyGenerator.getInstance().generate();
		final BlockchainKeypair kpDevoice = BlockchainKeyGenerator.getInstance().generate();
		
		final Map<Bytes, BlockchainKeypair> endpoints = new HashMap<>();
		endpoints.put(kpManager.getAddress(), kpManager);
		endpoints.put(kpEmployee.getAddress(), kpEmployee);
		
		final Map<Bytes, BlockchainKeypair> nodes = new HashMap<>();
		nodes.put(kpDevoice.getAddress(), kpDevoice);
		

		final String ROLE_ADMIN = "ID_ADMIN";
		final String ROLE_OPERATOR = "OPERATOR";
		final String ROLE_DATA_COLLECTOR = "DATA_COLLECTOR";

		final Privileges PRIVILEGES_ADMIN = Privileges.configure()
				.enable(LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT)
				.enable(TransactionPermission.DIRECT_OPERATION, TransactionPermission.CONTRACT_OPERATION);

		final Privileges PRIVILEGES_OPERATOR = Privileges.configure()
				.enable(LedgerPermission.WRITE_DATA_ACCOUNT, LedgerPermission.APPROVE_TX)
				.enable(TransactionPermission.CONTRACT_OPERATION);

		final Privileges PRIVILEGES_DATA_COLLECTOR = Privileges.configure().enable(LedgerPermission.WRITE_DATA_ACCOUNT)
				.enable(TransactionPermission.CONTRACT_OPERATION);

		RolePrivilegeSettings rolePrivilegeSettings = initRoles(testStorage,
				new String[] { ROLE_ADMIN, ROLE_OPERATOR, ROLE_DATA_COLLECTOR },
				new Privileges[] { PRIVILEGES_ADMIN, PRIVILEGES_OPERATOR, PRIVILEGES_DATA_COLLECTOR });

		String[] managerRoles = new String[] { ROLE_ADMIN, ROLE_OPERATOR };
		String[] employeeRoles = new String[] { ROLE_OPERATOR };
		String[] devoiceRoles = new String[] { ROLE_DATA_COLLECTOR };
		UserRoleSettings userRolesSettings = initUserRoless(testStorage,
				new Bytes[] { kpManager.getAddress(), kpEmployee.getAddress(), kpDevoice.getAddress() },
				new RolesPolicy[] { RolesPolicy.UNION, RolesPolicy.UNION, RolesPolicy.UNION },
				new String[][] { managerRoles, employeeRoles, devoiceRoles });
		
		LedgerSecurityManager securityManager = new LedgerSecurityManagerImpl(rolePrivilegeSettings, userRolesSettings);
		
		SecurityPolicy policy = securityManager.getSecurityPolicy(endpoints.keySet(), nodes.keySet());
		
		assertTrue(policy.isEnableToEndpoints(LedgerPermission.REGISTER_USER, MultiIdsPolicy.AT_LEAST_ONE));
		assertTrue(policy.isEnableToEndpoints(LedgerPermission.REGISTER_DATA_ACCOUNT, MultiIdsPolicy.AT_LEAST_ONE));
		assertTrue(policy.isEnableToEndpoints(LedgerPermission.WRITE_DATA_ACCOUNT, MultiIdsPolicy.AT_LEAST_ONE));
		assertTrue(policy.isEnableToEndpoints(LedgerPermission.APPROVE_TX, MultiIdsPolicy.AT_LEAST_ONE));
		assertFalse(policy.isEnableToEndpoints(LedgerPermission.REGISTER_USER, MultiIdsPolicy.ALL));
		assertFalse(policy.isEnableToEndpoints(LedgerPermission.AUTHORIZE_ROLES, MultiIdsPolicy.AT_LEAST_ONE));
	}

}
