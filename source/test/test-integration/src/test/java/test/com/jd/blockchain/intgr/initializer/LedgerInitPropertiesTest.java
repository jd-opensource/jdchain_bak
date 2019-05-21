package test.com.jd.blockchain.intgr.initializer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import com.jd.blockchain.crypto.AddressEncoding;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.tools.initializer.LedgerInitProperties;
import com.jd.blockchain.tools.initializer.LedgerInitProperties.ConsensusParticipantConfig;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
import com.jd.blockchain.utils.codec.HexUtils;
import test.com.jd.blockchain.intgr.IntegrationBase;

public class LedgerInitPropertiesTest {

	@Test
	public void test() throws IOException {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger.init");
		InputStream in = ledgerInitSettingResource.getInputStream();
		try {
			LedgerInitProperties initProps = LedgerInitProperties.resolve(in);
			assertEquals(4, initProps.getConsensusParticipantCount());
			String expectedLedgerSeed = "932dfe23-fe23232f-283f32fa-dd32aa76-8322ca2f-56236cda-7136b322-cb323ffe"
					.replace("-", "");
			String actualLedgerSeed = HexUtils.encode(initProps.getLedgerSeed());
			assertEquals(expectedLedgerSeed, actualLedgerSeed);

			assertEquals("com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider",
					initProps.getConsensusProvider());

			String[] cryptoProviders = initProps.getCryptoProviders();
			assertEquals(2, cryptoProviders.length);
			assertEquals("com.jd.blockchain.crypto.service.classic.ClassicCryptoService", cryptoProviders[0]);
			assertEquals("com.jd.blockchain.crypto.service.sm.SMCryptoService", cryptoProviders[1]);

			ConsensusParticipantConfig part0 = initProps.getConsensusParticipant(0);
			assertEquals("jd.com", part0.getName());
			PubKey pubKey0 = KeyGenCommand.decodePubKey("3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9");
			assertEquals(pubKey0, part0.getPubKey());
			assertEquals("127.0.0.1", part0.getInitializerAddress().getHost());
			assertEquals(8800, part0.getInitializerAddress().getPort());
			assertEquals(true, part0.getInitializerAddress().isSecure());

			ConsensusParticipantConfig part1 = initProps.getConsensusParticipant(1);
			assertEquals(false, part1.getInitializerAddress().isSecure());
			PubKey pubKey1 = KeyGenCommand.decodePubKey("3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX");
			assertEquals(pubKey1, part1.getPubKey());

			ConsensusParticipantConfig part2 = initProps.getConsensusParticipant(2);
			assertEquals("7VeRAr3dSbi1xatq11ZcF7sEPkaMmtZhV9shonGJWk9T4pLe", part2.getPubKey().toBase58());

		} finally {
			in.close();
		}
	}

	@Test
	public void testPubKeyAddress() {
		String[] pubKeys = IntegrationBase.PUB_KEYS;
		int index = 0;
		for (String pubKeyStr : pubKeys) {
			System.out.println("[" + index + "][配置] = " + pubKeyStr);
			PubKey pubKey = KeyGenCommand.decodePubKey(pubKeyStr);
			System.out.println("[" + index + "][公钥Base58] = " + pubKey.toBase58());
			System.out.println("[" + index + "][地址] = " + AddressEncoding.generateAddress(pubKey).toBase58());
			System.out.println("--------------------------------------------------------------------");
			index++;
		}
	}

}
