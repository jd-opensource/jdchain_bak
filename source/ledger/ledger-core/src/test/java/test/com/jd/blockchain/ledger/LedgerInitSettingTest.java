package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.ParticipantCertData;
import com.jd.blockchain.transaction.ConsensusParticipantData;
import com.jd.blockchain.transaction.LedgerInitOpTemplate;
import com.jd.blockchain.transaction.LedgerInitSettingData;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.net.NetworkAddress;

public class LedgerInitSettingTest {
	byte[] seed = null;
	byte[] csSysSettingBytes = null;
	LedgerInitSettingData ledgerInitSettingData = new LedgerInitSettingData();
	LedgerInitOpTemplate template = new LedgerInitOpTemplate();

	@Before
	public void initCfg() {

		DataContractRegistry.register(LedgerInitSetting.class);
		Random rand = new Random();

		seed = new byte[8];
		rand.nextBytes(seed);
		csSysSettingBytes = new byte[64];
		rand.nextBytes(csSysSettingBytes);

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setAutoVerifyHash(true);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);

		ledgerInitSettingData.setConsensusSettings(new Bytes(csSysSettingBytes));
		ledgerInitSettingData.setConsensusProvider("cons-provider");

		ledgerInitSettingData.setLedgerSeed(seed);

		ledgerInitSettingData.setCryptoSetting(cryptoConfig);

	}

	@Test
	public void test_ledgerinitsetting_ConsensusParticipantData() {

		ConsensusParticipantData[] parties = new ConsensusParticipantData[4];
		BlockchainKeypair[] keys = new BlockchainKeypair[parties.length];
		for (int i = 0; i < parties.length; i++) {
			keys[i] = BlockchainKeyGenerator.getInstance().generate();
			parties[i] = new ConsensusParticipantData();
			// parties[i].setId(i);
			parties[i].setAddress(AddressEncoding.generateAddress(keys[i].getPubKey()).toBase58());
			parties[i].setHostAddress(new NetworkAddress("192.168.10." + (10 + i), 10010 + 10 * i));
			parties[i].setName("Participant[" + i + "]");
			parties[i].setPubKey(keys[i].getPubKey());
		}
		ConsensusParticipantData[] parties1 = Arrays.copyOf(parties, 4);

		ledgerInitSettingData.setConsensusParticipants(parties1);

		byte[] encode = BinaryEncodingUtils.encode(ledgerInitSettingData, LedgerInitSetting.class);

		LedgerInitSetting decode = BinaryEncodingUtils.decode(encode);

		for (int i = 0; i < ledgerInitSettingData.getConsensusParticipants().length; i++) {
			assertEquals(ledgerInitSettingData.getConsensusParticipants()[i].getAddress(),
					decode.getConsensusParticipants()[i].getAddress());
			assertEquals(ledgerInitSettingData.getConsensusParticipants()[i].getName(),
					decode.getConsensusParticipants()[i].getName());
			assertEquals(ledgerInitSettingData.getConsensusParticipants()[i].getPubKey(),
					decode.getConsensusParticipants()[i].getPubKey());

		}
		assertArrayEquals(ledgerInitSettingData.getLedgerSeed(), decode.getLedgerSeed());
		assertArrayEquals(ledgerInitSettingData.getConsensusSettings().toBytes(),
				decode.getConsensusSettings().toBytes());
		assertEquals(ledgerInitSettingData.getCryptoSetting().getHashAlgorithm(),
				decode.getCryptoSetting().getHashAlgorithm());
		assertEquals(ledgerInitSettingData.getCryptoSetting().getAutoVerifyHash(),
				decode.getCryptoSetting().getAutoVerifyHash());
		assertEquals(ledgerInitSettingData.getConsensusProvider(), decode.getConsensusProvider());

	}

	// @Test
	// public void test_ledgerinitsetting_ConsensusParticipantConfig() {
	// }

	@Test
	public void test_ledgerinitsetting_ParticipantCertData() {

		ParticipantCertData[] parties = new ParticipantCertData[4];
		BlockchainKeypair[] keys = new BlockchainKeypair[parties.length];

		for (int i = 0; i < parties.length; i++) {
			keys[i] = BlockchainKeyGenerator.getInstance().generate();
			parties[i] = new ParticipantCertData(AddressEncoding.generateAddress(keys[i].getPubKey()).toBase58(),
					"Participant[" + i + "]", keys[i].getPubKey());
		}

		ParticipantCertData[] parties1 = Arrays.copyOf(parties, 4);

		ledgerInitSettingData.setConsensusParticipants(parties1);

		byte[] encode = BinaryEncodingUtils.encode(ledgerInitSettingData, LedgerInitSetting.class);

		LedgerInitSetting decode = BinaryEncodingUtils.decode(encode);

		for (int i = 0; i < ledgerInitSettingData.getConsensusParticipants().length; i++) {
			assertEquals(ledgerInitSettingData.getConsensusParticipants()[i].getAddress(),
					decode.getConsensusParticipants()[i].getAddress());
			assertEquals(ledgerInitSettingData.getConsensusParticipants()[i].getName(),
					decode.getConsensusParticipants()[i].getName());
			assertEquals(ledgerInitSettingData.getConsensusParticipants()[i].getPubKey(),
					decode.getConsensusParticipants()[i].getPubKey());

		}
		assertArrayEquals(ledgerInitSettingData.getLedgerSeed(), decode.getLedgerSeed());
		assertArrayEquals(ledgerInitSettingData.getConsensusSettings().toBytes(),
				decode.getConsensusSettings().toBytes());
		assertEquals(ledgerInitSettingData.getCryptoSetting().getHashAlgorithm(),
				decode.getCryptoSetting().getHashAlgorithm());
		assertEquals(ledgerInitSettingData.getCryptoSetting().getAutoVerifyHash(),
				decode.getCryptoSetting().getAutoVerifyHash());
		assertEquals(ledgerInitSettingData.getConsensusProvider(), decode.getConsensusProvider());
	}
}
