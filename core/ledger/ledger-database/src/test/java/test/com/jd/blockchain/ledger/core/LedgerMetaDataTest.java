package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import com.jd.blockchain.ledger.*;
import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.LedgerAdminDataset;
import com.jd.blockchain.ledger.core.LedgerConfiguration;
import com.jd.blockchain.ledger.core.ParticipantCertData;
import com.jd.blockchain.utils.Bytes;

/**
 * Created by zhangshuang3 on 2018/8/31.
 */
public class LedgerMetaDataTest {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	byte[] seed = null;
	String consensusProvider = "test-provider";
	byte[] consensusSettingBytes = null;
	byte[] rawDigestBytes = null;

	@Before
	public void initCfg() throws Exception {
		Random rand = new Random();
		seed = new byte[8];
		consensusSettingBytes = new byte[8];
		rawDigestBytes = new byte[8];
		rand.nextBytes(seed);
		rand.nextBytes(consensusSettingBytes);
		rand.nextBytes(rawDigestBytes);
		DataContractRegistry.register(LedgerMetadata.class);
		DataContractRegistry.register(ParticipantNode.class);
	}

	@Test
	public void testSerialize_LedgerMetadata() {
		// LedgerCodes.METADATA

		// prepare work
		// ConsensusConfig consensusConfig = new ConsensusConfig();
		// consensusConfig.setValue(settingValue);ClassicCryptoService.ED25519_ALGORITHM

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);

//		LedgerConfiguration ledgerConfiguration = new LedgerConfiguration(consensusProvider,
//				new Bytes(consensusSettingBytes), cryptoConfig);
		HashDigest settingsHash = Crypto.getHashFunction("SHA256").hash(consensusSettingBytes);

		LedgerAdminDataset.LedgerMetadataInfo ledgerMetadata = new LedgerAdminDataset.LedgerMetadataInfo();

		ledgerMetadata.setSeed(seed);
		ledgerMetadata.setSettingsHash(settingsHash);

		HashDigest hashDigest = new HashDigest(ClassicAlgorithm.SHA256, rawDigestBytes);
		ledgerMetadata.setParticipantsHash(hashDigest);

		// encode and decode
		byte[] encodeBytes = BinaryProtocol.encode(ledgerMetadata, LedgerMetadata.class);
		LedgerMetadata deLedgerMetaData = BinaryProtocol.decode(encodeBytes);

		// verify start
		assertArrayEquals(ledgerMetadata.getSeed(), deLedgerMetaData.getSeed());
		assertEquals(ledgerMetadata.getParticipantsHash(), deLedgerMetaData.getParticipantsHash());
		assertEquals(ledgerMetadata.getSettingsHash(), deLedgerMetaData.getSettingsHash());

		return;
	}

	@Test
	public void testSerialize_LedgerSetting() {
		// LedgerCodes.METADATA_LEDGER_SETTING
		Random rand = new Random();
		byte[] csSettingsBytes = new byte[8];
		rand.nextBytes(csSettingsBytes);
		String consensusProvider = "testprovider";

		// ConsensusConfig consensusConfig = new ConsensusConfig();
		// consensusConfig.setValue(settingValue);

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);

		LedgerConfiguration ledgerConfiguration = new LedgerConfiguration(consensusProvider, new Bytes(csSettingsBytes),
				cryptoConfig);
		byte[] encodeBytes = BinaryProtocol.encode(ledgerConfiguration, LedgerSettings.class);
		LedgerSettings deLedgerConfiguration = BinaryProtocol.decode(encodeBytes);
		// verify start
		assertTrue(ledgerConfiguration.getConsensusSetting().equals(deLedgerConfiguration.getConsensusSetting()));
		assertEquals(ledgerConfiguration.getCryptoSetting().getAutoVerifyHash(),
				deLedgerConfiguration.getCryptoSetting().getAutoVerifyHash());
		assertEquals(ledgerConfiguration.getCryptoSetting().getHashAlgorithm(),
				deLedgerConfiguration.getCryptoSetting().getHashAlgorithm());

		return;
	}

	// @Test
	// public void testSerialize_ConsensusSetting() {
	// //LedgerCodes.METADATA_LEDGER_SETTING_CONSENSUS
	// Random rand = new Random();
	// byte[] settingValue = new byte[8];
	// rand.nextBytes(settingValue);
	//
	// ConsensusConfig consensusConfig = new ConsensusConfig();
	// consensusConfig.setValue(settingValue);
	// byte[] encodeBytes = BinaryEncodingUtils.encode(consensusConfig,
	// ConsensusSetting.class);
	// ConsensusSetting deConsensusConfig = BinaryEncodingUtils.decode(encodeBytes);
	//
	// //verify start
	// assertArrayEquals(consensusConfig.getValue(), deConsensusConfig.getValue());
	//
	// return;
	// }

	@Test
	public void testSerialize_CryptoSetting() {
		// LedgerCodes.METADATA_LEDGER_SETTING_CRYPTO

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);
		byte[] encodeBytes = BinaryProtocol.encode(cryptoConfig, CryptoSetting.class);
		CryptoSetting deCryptoConfig = BinaryProtocol.decode(encodeBytes);

		// verify start
		assertEquals(cryptoConfig.getHashAlgorithm(), deCryptoConfig.getHashAlgorithm());
		assertEquals(cryptoConfig.getAutoVerifyHash(), deCryptoConfig.getAutoVerifyHash());
		return;
	}

	@Test
	public void testSerialize_ParticipantCert() {
		// LedgerCodes.METADATA_PARTICIPANT_CERT
		// prepare work
		int id = 1;
		// String address = "xxxxxxxxxxxxxx";
		PubKey pubKey = new PubKey(ClassicAlgorithm.ED25519, rawDigestBytes);
		// ParticipantInfo info = new ParticipantCertData.ParticipantInfoData(1, "yyy");
		// SignatureDigest signature = new SignatureDigest(CryptoAlgorithm.SM2,
		// rawDigestBytes);
		String name = "John";
		// NetworkAddress consensusAddress = new NetworkAddress("192.168.1.1", 9001,
		// false);
		Bytes address = AddressEncoding.generateAddress(pubKey);
		ParticipantCertData participantCertData = new ParticipantCertData(address, name, pubKey, ParticipantNodeState.ACTIVED);

		// encode and decode
		byte[] encodeBytes = BinaryProtocol.encode(participantCertData, ParticipantNode.class);
		ParticipantNode deParticipantInfoData = BinaryProtocol.decode(encodeBytes);

		// verify start
		assertEquals(participantCertData.getAddress(), deParticipantInfoData.getAddress());
		assertEquals(participantCertData.getPubKey(), deParticipantInfoData.getPubKey());
		assertEquals(participantCertData.getName(), deParticipantInfoData.getName());
		// assertEquals(participantCertData.getConsensusAddress().getHost(),
		// deParticipantInfoData.getConsensusAddress().getHost());
		// assertEquals(participantCertData.getConsensusAddress().getPort(),
		// deParticipantInfoData.getConsensusAddress().getPort());
		// assertEquals(participantCertData.getConsensusAddress().isSecure(),
		// deParticipantInfoData.getConsensusAddress().isSecure());

		return;
	}

	// @Test
	// public void testSerialize_ParticipantInfo() {
	// String name = "yyyy";
	//
	// ParticipantCertData.ParticipantInfoData participantInfoData = new
	// ParticipantCertData.ParticipantInfoData(1, name);
	// byte[] encodeBytes = BinaryEncodingUtils.encode(participantInfoData,
	// ParticipantInfo.class);
	// ParticipantCertData.ParticipantInfoData deParticipantInfoData =
	// BinaryEncodingUtils.decode(encodeBytes, null,
	// ParticipantCertData.ParticipantInfoData.class);
	//
	// //verify start
	// assertEquals(participantInfoData.getId(), deParticipantInfoData.getId());
	// assertEquals(participantInfoData.getName(), deParticipantInfoData.getName());
	//
	// return;
	// }
}
