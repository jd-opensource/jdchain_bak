package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerAdminSettings;
import com.jd.blockchain.ledger.LedgerMetadata_V2;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerSettings;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.ledger.RolePrivilegeSettings;
import com.jd.blockchain.ledger.RolePrivileges;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.UserRoles;
import com.jd.blockchain.ledger.UserAuthorizationSettings;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.LedgerAdminDataset;
import com.jd.blockchain.ledger.core.LedgerConfiguration;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.transaction.ConsensusParticipantData;
import com.jd.blockchain.transaction.LedgerInitData;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.net.NetworkAddress;

public class LedgerAdminDatasetTest {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private Random rand = new Random();

	@Test
	public void testSerialization() {
		String keyPrefix = "";
		LedgerInitData initSetting = new LedgerInitData();
		ConsensusParticipantData[] parties = new ConsensusParticipantData[5];
		BlockchainKeypair[] bckeys = new BlockchainKeypair[parties.length];
		for (int i = 0; i < parties.length; i++) {
			bckeys[i] = BlockchainKeyGenerator.getInstance().generate();
			parties[i] = new ConsensusParticipantData();
			parties[i].setId(i);
			parties[i].setAddress(AddressEncoding.generateAddress(bckeys[i].getPubKey()));
			parties[i].setHostAddress(new NetworkAddress("192.168.10." + (10 + i), 10010 + 10 * i));
			parties[i].setName("Participant[" + i + "]");
			parties[i].setPubKey(bckeys[i].getPubKey());
			parties[i].setParticipantState(ParticipantNodeState.ACTIVED);
		}
		ConsensusParticipantData[] parties1 = Arrays.copyOf(parties, 4);
		initSetting.setConsensusParticipants(parties1);

		byte[] csSysSettingBytes = new byte[64];
		rand.nextBytes(csSysSettingBytes);
		initSetting.setConsensusSettings(new Bytes(csSysSettingBytes));
		initSetting.setConsensusProvider("consensus-provider");

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}
		CryptoConfig cryptoSetting = new CryptoConfig();
		cryptoSetting.setSupportedProviders(supportedProviders);
		cryptoSetting.setAutoVerifyHash(true);
		cryptoSetting.setHashAlgorithm(ClassicAlgorithm.SHA256);
		initSetting.setCryptoSetting(cryptoSetting);

		byte[] ledgerSeed = new byte[16];
		rand.nextBytes(ledgerSeed);
		initSetting.setLedgerSeed(ledgerSeed);

		MemoryKVStorage testStorage = new MemoryKVStorage();

		// Create intance with init setting;
		LedgerAdminDataset ledgerAdminDataset = new LedgerAdminDataset(initSetting, keyPrefix, testStorage,
				testStorage);

		ledgerAdminDataset.getRolePrivileges().addRolePrivilege("DEFAULT",
				new LedgerPermission[] { LedgerPermission.CONFIGURE_ROLES, LedgerPermission.REGISTER_USER,
						LedgerPermission.APPROVE_TX },
				new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION,
						TransactionPermission.CONTRACT_OPERATION });

		ledgerAdminDataset.getAuthorizations().addUserRoles(parties[0].getAddress(), RolesPolicy.UNION, "DEFAULT");

		// New created instance is updated until being committed;
		assertTrue(ledgerAdminDataset.isUpdated());
		// Hash of account is null until being committed;
		assertNull(ledgerAdminDataset.getHash());

		LedgerMetadata_V2 meta = ledgerAdminDataset.getMetadata();
		assertNull(meta.getParticipantsHash());

		// Commit, and check the storage keys;
		ledgerAdminDataset.commit();

		// New created instance isn't updated after being committed;
		assertFalse(ledgerAdminDataset.isUpdated());
		// Hash of account isn't null after being committed;
		assertNotNull(ledgerAdminDataset.getHash());

		meta = ledgerAdminDataset.getMetadata();
		assertNotNull(meta.getParticipantsHash());
		assertNotNull(meta.getSettingsHash());
		assertNotNull(meta.getRolePrivilegesHash());
		assertNotNull(meta.getUserRolesHash());
		
		assertNotNull(ledgerAdminDataset.getRolePrivileges().getRolePrivilege("DEFAULT"));

		// ----------------------
		// Reload account from storage with readonly mode, and check the integrity of
		// data;
		HashDigest adminAccHash = ledgerAdminDataset.getHash();
		LedgerAdminDataset reloadAdminAccount1 = new LedgerAdminDataset(adminAccHash, keyPrefix, testStorage,
				testStorage, true);
		
		LedgerMetadata_V2 meta2 = reloadAdminAccount1.getMetadata();
		assertNotNull(meta2.getParticipantsHash());
		assertNotNull(meta2.getSettingsHash());
		assertNotNull(meta2.getRolePrivilegesHash());
		assertNotNull(meta2.getUserRolesHash());
		
		// verify realod settings of admin account;
		verifyRealoadingSettings(reloadAdminAccount1, adminAccHash, ledgerAdminDataset.getMetadata(),
				ledgerAdminDataset.getSettings());
		// verify the consensus participant list；
		verifyRealoadingParities(reloadAdminAccount1, parties1);
		// It will throw exeception because of this account is readonly;
		verifyReadonlyState(reloadAdminAccount1);

		verifyRealoadingRoleAuthorizations(reloadAdminAccount1, ledgerAdminDataset.getRolePrivileges(),
				ledgerAdminDataset.getAuthorizations());

		// --------------
		// 重新加载，并进行修改;
		LedgerAdminDataset reloadAdminAccount2 = new LedgerAdminDataset(adminAccHash, keyPrefix, testStorage, testStorage, false);
		LedgerConfiguration newSetting = new LedgerConfiguration(reloadAdminAccount2.getPreviousSetting());
		byte[] newCsSettingBytes = new byte[64];
		rand.nextBytes(newCsSettingBytes);
		newSetting.setConsensusSetting(new Bytes(newCsSettingBytes));
		newSetting.getCryptoSetting().setAutoVerifyHash(false);
		reloadAdminAccount2.setLedgerSetting(newSetting);

		reloadAdminAccount2.addParticipant(parties[4]);

		reloadAdminAccount2.getRolePrivileges().addRolePrivilege("ADMIN",
				new LedgerPermission[] { LedgerPermission.APPROVE_TX },
				new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION });

		reloadAdminAccount2.getRolePrivileges().disablePermissions("DEFAULT", TransactionPermission.CONTRACT_OPERATION);

		reloadAdminAccount2.getAuthorizations().addUserRoles(parties[1].getAddress(), RolesPolicy.UNION, "DEFAULT", "ADMIN");

		reloadAdminAccount2.commit();

		LedgerSettings newlyLedgerSettings = reloadAdminAccount2.getSettings();

		// record the new account hash;
		HashDigest newAccHash = reloadAdminAccount2.getHash();
		LedgerMetadata_V2 newMeta = reloadAdminAccount2.getMetadata();

		// load the last version of account and verify again;
		LedgerAdminDataset previousAdminAccount = new LedgerAdminDataset(adminAccHash, keyPrefix, testStorage,
				testStorage, true);
		verifyRealoadingSettings(previousAdminAccount, adminAccHash, ledgerAdminDataset.getMetadata(),
				ledgerAdminDataset.getSettings());
		verifyRealoadingParities(previousAdminAccount, parties1);
		verifyReadonlyState(previousAdminAccount);

		// load the hash of new committing;
		LedgerAdminDataset newlyAdminAccount = new LedgerAdminDataset(newAccHash, keyPrefix, testStorage, testStorage,
				true);
		verifyRealoadingSettings(newlyAdminAccount, newAccHash, newMeta, newlyLedgerSettings);
		verifyRealoadingParities(newlyAdminAccount, parties);
		verifyReadonlyState(newlyAdminAccount);

//		System.out.println("========= [LedgerAdminAccount Test] Show generated storage keys... =======");
//		testStorage.printStoragedKeys();
	}

	private void verifyRealoadingSettings(LedgerAdminDataset actualAccount, HashDigest expAccRootHash,
			LedgerMetadata_V2 expMeta, LedgerSettings expLedgerSettings) {
		// 验证基本信息；
		assertFalse(actualAccount.isUpdated());
		assertTrue(actualAccount.isReadonly());

		assertEquals(expAccRootHash, actualAccount.getHash());

		// verify metadata；
		LedgerMetadata_V2 actualMeta = actualAccount.getMetadata();
		assertArrayEquals(expMeta.getSeed(), actualMeta.getSeed());
		assertEquals(expMeta.getParticipantsHash(), actualMeta.getParticipantsHash());
		assertNotNull(actualMeta.getSettingsHash());
		assertEquals(expMeta.getSettingsHash(), actualMeta.getSettingsHash());
		assertNotNull(actualMeta.getRolePrivilegesHash());
		assertEquals(expMeta.getRolePrivilegesHash(), actualMeta.getRolePrivilegesHash());
		assertNotNull(actualMeta.getUserRolesHash());
		assertEquals(expMeta.getUserRolesHash(), actualMeta.getUserRolesHash());

		LedgerSettings actualLedgerSettings = actualAccount.getSettings();

		assertEquals(expLedgerSettings.getConsensusSetting(), actualLedgerSettings.getConsensusSetting());
		assertEquals(expLedgerSettings.getConsensusProvider(), actualLedgerSettings.getConsensusProvider());

		assertEquals(expLedgerSettings.getCryptoSetting().getAutoVerifyHash(),
				actualLedgerSettings.getCryptoSetting().getAutoVerifyHash());
		assertEquals(expLedgerSettings.getCryptoSetting().getHashAlgorithm(),
				actualLedgerSettings.getCryptoSetting().getHashAlgorithm());
	}

	private void verifyRealoadingRoleAuthorizations(LedgerAdminSettings actualAccount,
			RolePrivilegeSettings expRolePrivilegeSettings, UserAuthorizationSettings expUserRoleSettings) {
		// 验证基本信息；
		RolePrivilegeSettings actualRolePrivileges = actualAccount.getRolePrivileges();
		RolePrivileges[] expRPs = expRolePrivilegeSettings.getRolePrivileges();

		assertEquals(expRPs.length, actualRolePrivileges.getRoleCount());

		for (RolePrivileges expRP : expRPs) {
			RolePrivileges actualRP = actualRolePrivileges.getRolePrivilege(expRP.getRoleName());
			assertNotNull(actualRP);
			assertArrayEquals(expRP.getLedgerPrivilege().toBytes(), actualRP.getLedgerPrivilege().toBytes());
			assertArrayEquals(expRP.getTransactionPrivilege().toBytes(), actualRP.getTransactionPrivilege().toBytes());
		}

		UserAuthorizationSettings actualUserRoleSettings = actualAccount.getAuthorizations();
		UserRoles[] expUserRoles = expUserRoleSettings.getUserRoles();
		assertEquals(expUserRoles.length, actualUserRoleSettings.getUserCount());

		for (UserRoles expUR : expUserRoles) {
			UserRoles actualUR = actualAccount.getAuthorizations().getUserRoles(expUR.getUserAddress());
			assertNotNull(actualUR);
			assertEquals(expUR.getPolicy(), actualUR.getPolicy());
			String[] expRoles = expUR.getRoles();
			Arrays.sort(expRoles);
			String[] actualRoles = actualUR.getRoles();
			Arrays.sort(actualRoles);
			assertArrayEquals(expRoles, actualRoles);
		}
	}

	private void verifyRealoadingParities(LedgerAdminInfo actualAccount, ParticipantNode[] expParties) {
		assertEquals(expParties.length, actualAccount.getParticipantCount());
		ParticipantNode[] actualPaticipants = actualAccount.getParticipants();
		assertEquals(expParties.length, actualPaticipants.length);
		for (int i = 0; i < actualPaticipants.length; i++) {
			ParticipantNode rlParti = actualPaticipants[i];
			assertEquals(expParties[i].getAddress(), rlParti.getAddress());
			assertEquals(expParties[i].getName(), rlParti.getName());
			// assertEquals(expParties[i].getConsensusAddress(),
			// rlParti.getConsensusAddress());
			assertEquals(expParties[i].getPubKey(), rlParti.getPubKey());
		}
	}

	/**
	 * 验证指定账户是否只读；
	 * 
	 * @param readonlyAccount
	 */
	private void verifyReadonlyState(LedgerAdminDataset readonlyAccount) {
		ConsensusParticipantData newParti = new ConsensusParticipantData();
		newParti.setId((int) readonlyAccount.getParticipantCount());
		newParti.setHostAddress(
				new NetworkAddress("192.168.10." + (10 + newParti.getId()), 10010 + 10 * newParti.getId()));
		newParti.setName("Participant[" + newParti.getAddress() + "]");

		BlockchainKeypair newKey = BlockchainKeyGenerator.getInstance().generate();
		newParti.setPubKey(newKey.getPubKey());

		Throwable ex = null;
		try {
			readonlyAccount.addParticipant(newParti);
		} catch (Exception e) {
			ex = e;
		}
		assertNotNull(ex);

		ex = null;
		try {
			LedgerConfiguration newLedgerSetting = new LedgerConfiguration(readonlyAccount.getSettings());
			readonlyAccount.setLedgerSetting(newLedgerSetting);
		} catch (Exception e) {
			ex = e;
		}
		assertNotNull(ex);
	}

}
