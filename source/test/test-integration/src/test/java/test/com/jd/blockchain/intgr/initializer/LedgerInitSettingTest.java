package test.com.jd.blockchain.intgr.initializer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.tools.initializer.LedgerInitProperties;
import com.jd.blockchain.tools.initializer.LedgerInitProperties.ConsensusParticipantConfig;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
import com.jd.blockchain.utils.codec.HexUtils;

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
			PubKey pubKey0 = KeyGenCommand.decodePubKey("endPsK36koyFr1D245Sa9j83vt6pZUdFBJoJRB3xAsWM6cwhRbna");
			assertEquals(pubKey0, part0.getPubKey());
//			assertEquals("127.0.0.1", part0.getConsensusAddress().getHost());
//			assertEquals(8900, part0.getConsensusAddress().getPort());
//			assertEquals(true, part0.getConsensusAddress().isSecure());
			assertEquals("127.0.0.1", part0.getInitializerAddress().getHost());
			assertEquals(8800, part0.getInitializerAddress().getPort());
			assertEquals(true, part0.getInitializerAddress().isSecure());
			
			ConsensusParticipantConfig part1 = setting.getConsensusParticipant(1);
			assertEquals(false, part1.getInitializerAddress().isSecure());
			PubKey pubKey1 = KeyGenCommand.decodePubKey("endPsK36sC5JdPCDPDAXUwZtS3sxEmqEhFcC4whayAsTTh8Z6eoZ");
			assertEquals(pubKey1, part1.getPubKey());
			
			ConsensusParticipantConfig part2 = setting.getConsensusParticipant(2);
			assertEquals(null, part2.getPubKey());
			
		} finally {
			in.close();
		}
	}

}
