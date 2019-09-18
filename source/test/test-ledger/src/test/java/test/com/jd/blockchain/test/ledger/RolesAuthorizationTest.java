package test.com.jd.blockchain.test.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.RolePrivilegeSettings;
import com.jd.blockchain.ledger.RolePrivileges;
import com.jd.blockchain.ledger.RolesConfigureOperation;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.TransactionBuilder;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.UserAuthorizeOperation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.UserRoles;
import com.jd.blockchain.ledger.core.DataAccount;
import com.jd.blockchain.ledger.core.DefaultOperationHandleRegisteration;
import com.jd.blockchain.ledger.core.LedgerInitializer;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.OperationHandleRegisteration;
import com.jd.blockchain.ledger.core.TransactionBatchProcessor;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.service.TransactionBatchResult;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.tools.initializer.web.LedgerInitConfiguration;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.RuntimeIOException;

public class RolesAuthorizationTest {

	private static final OperationHandleRegisteration HANDLE_REG = new DefaultOperationHandleRegisteration();

	public static final String PASSWORD = "abc";

	public static final String[] PUB_KEYS = { "3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9",
			"3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX",
			"3snPdw7i7PZi6TStiyc6mzjprnNhgs2atSGNS8wPYzhbKaUWGFJt7x",
			"3snPdw7i7PifPuRX7fu3jBjsb3rJRfDe9GtbDfvFJaJ4V4hHXQfhwk" };

	public static final String[] PRIV_KEYS = {
			"177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x",
			"177gju9p5zrNdHJVEQnEEKF4ZjDDYmAXyfG84V5RPGVc5xFfmtwnHA7j51nyNLUFffzz5UT",
			"177gjtwLgmSx5v1hFb46ijh7L9kdbKUpJYqdKVf9afiEmAuLgo8Rck9yu5UuUcHknWJuWaF",
			"177gk1pudweTq5zgJTh8y3ENCTwtSFsKyX7YnpuKPo7rKgCkCBXVXh5z2syaTCPEMbuWRns" };

	public static final BlockchainKeypair[] KEYS;

	private static final BlockchainKeypair ADMIN_USER;
	private static final BlockchainKeypair MANAGER_USER;
	private static final BlockchainKeypair DEFAULT_USER;
	private static final BlockchainKeypair GUEST_USER;

	// 预置的新普通用户；
	private static final BlockchainKeypair NEW_USER = BlockchainKeyGenerator.getInstance().generate();
	// 预置的数据账户；
	private static final BlockchainIdentity DATA_ACCOUNT_ID = BlockchainKeyGenerator.getInstance().generate()
			.getIdentity();

	static {
		KEYS = new BlockchainKeypair[PRIV_KEYS.length];
		for (int i = 0; i < PRIV_KEYS.length; i++) {
			PrivKey privKey = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[i], PASSWORD);
			PubKey pubKey = KeyGenUtils.decodePubKey(PUB_KEYS[i]);
			KEYS[i] = new BlockchainKeypair(pubKey, privKey);
		}
		ADMIN_USER = KEYS[0];
		MANAGER_USER = KEYS[1];
		DEFAULT_USER = KEYS[2];
		GUEST_USER = KEYS[3];

		// ----------
		DataContractRegistry.register(LedgerInitOperation.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
		DataContractRegistry.register(RolesConfigureOperation.class);
		DataContractRegistry.register(UserAuthorizeOperation.class);
	}

	@Test
	public void test() {
		MemoryKVStorage storage = new MemoryKVStorage();
		LedgerBlock genesisBlock = initLedger(storage);
		final HashDigest ledgerHash = genesisBlock.getHash();

		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledger = ledgerManager.register(ledgerHash, storage);

		// 验证角色和用户的权限配置；
		assertUserRolesPermissions(ledger);

		// 预置数据：准备一个新用户和数据账户；
		TransactionRequest predefinedTx = buildRequest(ledger.getHash(), ADMIN_USER, ADMIN_USER,
				new TransactionDefiner() {
					@Override
					public void define(TransactionBuilder txBuilder) {
						txBuilder.security().roles().configure("NORMAL")
								.enable(LedgerPermission.REGISTER_DATA_ACCOUNT)
								.disable(LedgerPermission.REGISTER_USER)
								.enable(TransactionPermission.CONTRACT_OPERATION);

						txBuilder.users().register(NEW_USER.getIdentity());

						txBuilder.security().authorziations().forUser(NEW_USER.getAddress()).authorize("NORMAL");

						txBuilder.dataAccounts().register(DATA_ACCOUNT_ID);
					}
				});

		TransactionBatchResult procResult = executeTransactions(ledger, predefinedTx);

		//断言预定义数据的交易和区块成功；
		assertBlock(1, procResult);
		assertTransactionAllSuccess(procResult);

		//断言预定义的数据符合预期；
		assertPredefineData(ledgerHash, storage);

		// 用不具备“注册用户”权限的用户，注册另一个新用户，预期交易失败；
		BlockchainKeypair tempUser = BlockchainKeyGenerator.getInstance().generate();
		TransactionRequest tx = buildRequest(ledger.getHash(), NEW_USER, ADMIN_USER, new TransactionDefiner() {
			@Override
			public void define(TransactionBuilder txBuilder) {
				txBuilder.users().register(tempUser.getIdentity());
			}
		});

		procResult = executeTransactions(ledger, tx);
		assertBlock(2, procResult);
		
		assertTransactionAllFail(procResult, TransactionState.REJECTED_BY_SECURITY_POLICY);
	}

	/**
	 * 断言区块高度；
	 * 
	 * @param blockHeight
	 * @param procResult
	 */
	private void assertBlock(long blockHeight, TransactionBatchResult procResult) {
		assertEquals(blockHeight, procResult.getBlock().getHeight());
	}

	/**
	 * 断言全部交易结果都是成功的；
	 * 
	 * @param procResult
	 */
	private void assertTransactionAllSuccess(TransactionBatchResult procResult) {

		Iterator<TransactionResponse> responses = procResult.getResponses();
		while (responses.hasNext()) {
			TransactionResponse transactionResponse = (TransactionResponse) responses.next();

			assertEquals(true, transactionResponse.isSuccess());
			assertEquals(TransactionState.SUCCESS, transactionResponse.getExecutionState());
			assertEquals(procResult.getBlock().getHash(), transactionResponse.getBlockHash());
			assertEquals(procResult.getBlock().getHeight(), transactionResponse.getBlockHeight());
		}
	}
	
	/**
	 * 断言全部交易结果都是失败的；
	 * 
	 * @param procResult
	 */
	private void assertTransactionAllFail(TransactionBatchResult procResult, TransactionState txState) {
		Iterator<TransactionResponse> responses = procResult.getResponses();
		while (responses.hasNext()) {
			TransactionResponse transactionResponse = (TransactionResponse) responses.next();
			
			assertEquals(false, transactionResponse.isSuccess());
			assertEquals(txState, transactionResponse.getExecutionState());
		}
	}

	/**
	 * 断言预定义的数据符合预期；
	 * 
	 * @param ledgerHash
	 * @param storage
	 */
	private void assertPredefineData(HashDigest ledgerHash, MemoryKVStorage storage) {
		LedgerManager ledgerManager = new LedgerManager();
		LedgerRepository ledger = ledgerManager.register(ledgerHash, storage);
		UserAccount newUser = ledger.getUserAccountSet().getAccount(NEW_USER.getAddress());
		assertNotNull(newUser);
		DataAccount dataAccount = ledger.getDataAccountSet().getAccount(DATA_ACCOUNT_ID.getAddress());
		assertNotNull(dataAccount);

		UserRoles userRoles = ledger.getAdminSettings().getAuthorizations().getUserRoles(NEW_USER.getAddress());
		assertNotNull(userRoles);
		assertEquals(1, userRoles.getRoleCount());
		assertEquals("NORMAL", userRoles.getRoles()[0]);

		RolePrivileges normalRole = ledger.getAdminSettings().getRolePrivileges().getRolePrivilege("NORMAL");
		assertNotNull(normalRole);
		assertEquals(true, normalRole.getLedgerPrivilege().isEnable(LedgerPermission.REGISTER_DATA_ACCOUNT));
		assertEquals(false, normalRole.getLedgerPrivilege().isEnable(LedgerPermission.REGISTER_USER));
		assertEquals(true, normalRole.getTransactionPrivilege().isEnable(TransactionPermission.CONTRACT_OPERATION));
		assertEquals(false, normalRole.getTransactionPrivilege().isEnable(TransactionPermission.DIRECT_OPERATION));
	}

	private TransactionBatchResult executeTransactions(LedgerRepository ledger, TransactionRequest... transactions) {
		TransactionBatchProcessor txProcessor = new TransactionBatchProcessor(ledger, HANDLE_REG);

		for (TransactionRequest request : transactions) {
			txProcessor.schedule(request);
		}

		TransactionBatchResultHandle procResult = txProcessor.prepare();
		procResult.commit();

		return procResult;
	}

	private TransactionRequest buildRequest(HashDigest ledgerHash, BlockchainKeypair endpoint, BlockchainKeypair node,
			TransactionDefiner definer) {
		TransactionBuilder txBuilder = new TxBuilder(ledgerHash);
		definer.define(txBuilder);
		TransactionRequestBuilder reqBuilder = txBuilder.prepareRequest();
		reqBuilder.signAsEndpoint(endpoint);
		if (node != null) {
			reqBuilder.signAsNode(node);
		}
		return reqBuilder.buildRequest();
	}

	private void assertUserRolesPermissions(LedgerQuery ledger) {
		// 验证角色-权限；
		assertRolePermissions(ledger, "DEFAULT",
				new LedgerPermission[] { LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT },
				new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION,
						TransactionPermission.CONTRACT_OPERATION });

		assertRolePermissions(ledger, "ADMIN",
				new LedgerPermission[] { LedgerPermission.CONFIGURE_ROLES, LedgerPermission.AUTHORIZE_USER_ROLES,
						LedgerPermission.SET_CONSENSUS, LedgerPermission.SET_CRYPTO,
						LedgerPermission.REGISTER_PARTICIPANT, LedgerPermission.REGISTER_USER },
				new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION });

		assertRolePermissions(ledger, "MANAGER",
				new LedgerPermission[] { LedgerPermission.CONFIGURE_ROLES, LedgerPermission.AUTHORIZE_USER_ROLES,
						LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT,
						LedgerPermission.REGISTER_CONTRACT, LedgerPermission.UPGRADE_CONTRACT,
						LedgerPermission.SET_USER_ATTRIBUTES, LedgerPermission.WRITE_DATA_ACCOUNT,
						LedgerPermission.APPROVE_TX },
				new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION,
						TransactionPermission.CONTRACT_OPERATION });

		assertRolePermissions(ledger, "GUEST", new LedgerPermission[] {},
				new TransactionPermission[] { TransactionPermission.CONTRACT_OPERATION });

		// 验证用户-角色；
		assertUserRoles(ledger, ADMIN_USER, RolesPolicy.UNION, "ADMIN", "MANAGER");
		assertUserRoles(ledger, MANAGER_USER, RolesPolicy.UNION, "MANAGER");
		assertUserRoles(ledger, DEFAULT_USER, RolesPolicy.UNION);
		assertUserRoles(ledger, DEFAULT_USER, RolesPolicy.UNION);
	}

	private void assertUserRoles(LedgerQuery ledger, BlockchainKeypair userKey, RolesPolicy policy, String... roles) {
		assertUserRoles(ledger, userKey.getAddress(), policy, roles);
	}

	private void assertUserRoles(LedgerQuery ledger, Bytes address, RolesPolicy policy, String[] roles) {
		if (roles == null) {
			roles = new String[0];
		}
		UserRoles userRoles = ledger.getAdminSettings().getAuthorizations().getUserRoles(address);
		assertNotNull(userRoles);
		assertEquals(policy, userRoles.getPolicy());

		Set<String> expectedRoles = new HashSet<String>(Arrays.asList(roles));
		Set<String> actualRoles = userRoles.getRoleSet();
		assertEquals(expectedRoles.size(), actualRoles.size());
		for (String r : actualRoles) {
			assertTrue(expectedRoles.contains(r));
		}
	}

	private void assertRolePermissions(LedgerQuery ledger, String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] txPermissions) {
		RolePrivilegeSettings roles = ledger.getAdminSettings().getRolePrivileges();
		assertTrue(roles.contains(roleName));
		RolePrivileges privileges = roles.getRolePrivilege(roleName);
		assertEquals(ledgerPermissions.length, privileges.getLedgerPrivilege().getPermissionCount());
		assertEquals(txPermissions.length, privileges.getTransactionPrivilege().getPermissionCount());

		Set<LedgerPermission> expectedLedgerPermissions = new HashSet<LedgerPermission>(
				Arrays.asList(ledgerPermissions));
		for (LedgerPermission p : LedgerPermission.values()) {
			if (expectedLedgerPermissions.contains(p)) {
				assertTrue(privileges.getLedgerPrivilege().isEnable(p));
			} else {
				assertFalse(privileges.getLedgerPrivilege().isEnable(p));
			}
		}

		Set<TransactionPermission> expectedTxPermissions = new HashSet<TransactionPermission>(
				Arrays.asList(txPermissions));
		for (TransactionPermission p : TransactionPermission.values()) {
			if (expectedTxPermissions.contains(p)) {
				assertTrue(privileges.getTransactionPrivilege().isEnable(p));
			} else {
				assertFalse(privileges.getTransactionPrivilege().isEnable(p));
			}
		}
	}

	private LedgerBlock initLedger(KVStorageService storage) {
		LedgerInitProperties initProps = loadInitProperties();
		LedgerInitConfiguration initConfig = LedgerInitConfiguration.create(initProps);
		LedgerInitializer initializer = LedgerInitializer.create(initConfig.getLedgerSettings(),
				initConfig.getSecuritySettings());

		DigitalSignature sign0 = initializer.signTransaction(KEYS[0]);
		DigitalSignature sign1 = initializer.signTransaction(KEYS[1]);
		DigitalSignature sign2 = initializer.signTransaction(KEYS[2]);
		DigitalSignature sign3 = initializer.signTransaction(KEYS[3]);

		LedgerBlock genesisBlock = initializer.prepareLedger(storage, sign0, sign1, sign2, sign3);
		initializer.commit();
		return genesisBlock;
	}

	private LedgerInitProperties loadInitProperties() {
		try {
			ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger.init");
			InputStream in = ledgerInitSettingResource.getInputStream();
			return LedgerInitProperties.resolve(in);
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	private static interface TransactionDefiner {

		void define(TransactionBuilder txBuilder);

	}
}
