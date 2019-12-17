package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import com.jd.blockchain.ledger.*;
import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.ParticipantCertData;
import com.jd.blockchain.transaction.ConsensusParticipantData;
import com.jd.blockchain.transaction.LedgerInitOpTemplate;
import com.jd.blockchain.transaction.LedgerInitData;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.net.NetworkAddress;

public class LedgerInitOperationTest {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	byte[] seed = null;
	byte[] csSysSettingBytes = null;
	LedgerInitData ledgerInitSettingData = new LedgerInitData();

	@Before
	public void initCfg() {

		DataContractRegistry.register(LedgerInitSetting.class);
		DataContractRegistry.register(LedgerInitOperation.class);

		Random rand = new Random();

		seed = new byte[8];
		rand.nextBytes(seed);
		csSysSettingBytes = new byte[64];
		rand.nextBytes(csSysSettingBytes);

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}
		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);

		ledgerInitSettingData.setConsensusSettings(new Bytes(csSysSettingBytes));
		ledgerInitSettingData.setConsensusProvider("cons-provider");

		ledgerInitSettingData.setLedgerSeed(seed);

		ledgerInitSettingData.setCryptoSetting(cryptoConfig);
	}

	@Test
	public void test_LedgerInitOperation_ConsensusParticipantData() {
		ConsensusParticipantData[] parties = new ConsensusParticipantData[4];
		BlockchainKeypair[] keys = new BlockchainKeypair[parties.length];
		for (int i = 0; i < parties.length; i++) {
			keys[i] = BlockchainKeyGenerator.getInstance().generate();
			parties[i] = new ConsensusParticipantData();
			// parties[i].setId(i);
			parties[i].setAddress(AddressEncoding.generateAddress(keys[i].getPubKey()));
			parties[i].setHostAddress(new NetworkAddress("192.168.10." + (10 + i), 10010 + 10 * i));
			parties[i].setName("Participant[" + i + "]");
			parties[i].setPubKey(keys[i].getPubKey());
			parties[i].setParticipantState(ParticipantNodeState.ACTIVED);
		}
		ConsensusParticipantData[] parties1 = Arrays.copyOf(parties, 4);

		ledgerInitSettingData.setConsensusParticipants(parties1);

		LedgerInitOpTemplate template = new LedgerInitOpTemplate(ledgerInitSettingData);

		byte[] encode = BinaryProtocol.encode(template, LedgerInitOperation.class);
		LedgerInitOperation decode = BinaryProtocol.decode(encode);

		for (int i = 0; i < template.getInitSetting().getConsensusParticipants().length; i++) {
			assertEquals(template.getInitSetting().getConsensusParticipants()[i].getAddress(),
					decode.getInitSetting().getConsensusParticipants()[i].getAddress());
			assertEquals(template.getInitSetting().getConsensusParticipants()[i].getName(),
					decode.getInitSetting().getConsensusParticipants()[i].getName());
			assertEquals(template.getInitSetting().getConsensusParticipants()[i].getPubKey(),
					decode.getInitSetting().getConsensusParticipants()[i].getPubKey());

		}
		assertArrayEquals(template.getInitSetting().getLedgerSeed(), decode.getInitSetting().getLedgerSeed());
		assertArrayEquals(template.getInitSetting().getConsensusSettings().toBytes(),
				decode.getInitSetting().getConsensusSettings().toBytes());
		assertEquals(template.getInitSetting().getCryptoSetting().getHashAlgorithm(),
				decode.getInitSetting().getCryptoSetting().getHashAlgorithm());
		assertEquals(template.getInitSetting().getCryptoSetting().getAutoVerifyHash(),
				decode.getInitSetting().getCryptoSetting().getAutoVerifyHash());
		assertEquals(template.getInitSetting().getConsensusProvider(), decode.getInitSetting().getConsensusProvider());

	}

	@Test
	public void test_LedgerInitOperation_ParticipantCertData() {
		ParticipantCertData[] parties = new ParticipantCertData[4];
		BlockchainKeypair[] keys = new BlockchainKeypair[parties.length];

		for (int i = 0; i < parties.length; i++) {
			keys[i] = BlockchainKeyGenerator.getInstance().generate();
			parties[i] = new ParticipantCertData(AddressEncoding.generateAddress(keys[i].getPubKey()),
					"Participant[" + i + "]", keys[i].getPubKey(), ParticipantNodeState.ACTIVED);
		}

		ParticipantCertData[] parties1 = Arrays.copyOf(parties, 4);

		ledgerInitSettingData.setConsensusParticipants(parties1);

		LedgerInitOpTemplate template = new LedgerInitOpTemplate(ledgerInitSettingData);

		byte[] encode = BinaryProtocol.encode(template, LedgerInitOperation.class);
		LedgerInitOperation decode = BinaryProtocol.decode(encode);

		for (int i = 0; i < template.getInitSetting().getConsensusParticipants().length; i++) {
			assertEquals(template.getInitSetting().getConsensusParticipants()[i].getAddress(),
					decode.getInitSetting().getConsensusParticipants()[i].getAddress());
			assertEquals(template.getInitSetting().getConsensusParticipants()[i].getName(),
					decode.getInitSetting().getConsensusParticipants()[i].getName());
			assertEquals(template.getInitSetting().getConsensusParticipants()[i].getPubKey(),
					decode.getInitSetting().getConsensusParticipants()[i].getPubKey());

		}
		assertArrayEquals(template.getInitSetting().getLedgerSeed(), decode.getInitSetting().getLedgerSeed());
		assertArrayEquals(template.getInitSetting().getConsensusSettings().toBytes(),
				decode.getInitSetting().getConsensusSettings().toBytes());
		assertEquals(template.getInitSetting().getCryptoSetting().getHashAlgorithm(),
				decode.getInitSetting().getCryptoSetting().getHashAlgorithm());
		assertEquals(template.getInitSetting().getCryptoSetting().getAutoVerifyHash(),
				decode.getInitSetting().getCryptoSetting().getAutoVerifyHash());
		assertEquals(template.getInitSetting().getConsensusProvider(), decode.getInitSetting().getConsensusProvider());
	}
}
