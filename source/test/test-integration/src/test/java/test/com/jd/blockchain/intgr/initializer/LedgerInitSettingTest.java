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

public class LedgerInitSettingTest {

	@Test
	public void test() throws IOException {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger.init");
		InputStream in = ledgerInitSettingResource.getInputStream();
		try {
			LedgerInitProperties setting = LedgerInitProperties.resolve(in);
			assertEquals(4, setting.getConsensusParticipantCount());
			String expectedLedgerSeed = "932dfe23-fe23232f-283f32fa-dd32aa76-8322ca2f-56236cda-7136b322-cb323ffe".replace("-", "");
			String actualLedgerSeed = HexUtils.encode(setting.getLedgerSeed());
			assertEquals(expectedLedgerSeed, actualLedgerSeed);
			
			ConsensusParticipantConfig part0 = setting.getConsensusParticipant(0);
			assertEquals("jd.com", part0.getName());
			assertEquals("keys/jd-com.pub", part0.getPubKeyPath());
			PubKey pubKey0 = KeyGenCommand.decodePubKey("3snPdw7i7PapsDoW185c3kfK6p8s6SwiJAdEUzgnfeuUox12nxgzXu");
			assertEquals(pubKey0, part0.getPubKey());
//			assertEquals("127.0.0.1", part0.getConsensusAddress().getHost());
//			assertEquals(8900, part0.getConsensusAddress().getPort());
//			assertEquals(true, part0.getConsensusAddress().isSecure());
			assertEquals("127.0.0.1", part0.getInitializerAddress().getHost());
			assertEquals(8800, part0.getInitializerAddress().getPort());
			assertEquals(true, part0.getInitializerAddress().isSecure());
			
			ConsensusParticipantConfig part1 = setting.getConsensusParticipant(1);
			assertEquals(false, part1.getInitializerAddress().isSecure());
			PubKey pubKey1 = KeyGenCommand.decodePubKey("3snPdw7i7Ph1SYLQt9uqVEqiuvNXjxCdGvEdN6otJsg5rbr7Aze7kf");
			assertEquals(pubKey1, part1.getPubKey());
			
			ConsensusParticipantConfig part2 = setting.getConsensusParticipant(2);
			assertEquals(null, part2.getPubKey());
			
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
