package test.com.jd.blockchain.tools.initializer;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig.BindingConfig;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LedgerBindingConfigTest {

	@Test
	public void testResolveAndStore() throws IOException {
		ClassPathResource ledgerBindingConfigFile = new ClassPathResource("ledger-binding.conf");
		InputStream in = ledgerBindingConfigFile.getInputStream();
		try {
			LedgerBindingConfig conf = LedgerBindingConfig.resolve(in);
			assertLedgerBindingConfig(conf);

			conf.store(System.out);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			conf.store(out);

			ByteArrayInputStream newIn = new ByteArrayInputStream(out.toByteArray());
			LedgerBindingConfig newConf = LedgerBindingConfig.resolve(newIn);

			assertLedgerBindingConfig(newConf);
		} finally {
			in.close();
		}
	}

//	@Test
//	public void testLedgerBindingRegister() throws IOException {
//		LedgerManager ledgerManager = new LedgerManager();
//		ClassPathResource ledgerBindingConfigFile = new ClassPathResource("ledger-binding-1.conf");
//		InputStream in = ledgerBindingConfigFile.getInputStream();
//		Exception ex = null;
//		try {
//			LedgerBindingConfig conf = LedgerBindingConfig.resolve(in);
////			assertLedgerBindingConfig(conf);
//
//			HashDigest[] existingLedgerHashs = ledgerManager.getLedgerHashs();
//			for (HashDigest lh : existingLedgerHashs) {
//				ledgerManager.unregister(lh);
//			}
//			HashDigest[] ledgerHashs = conf.getLedgerHashs();
//			for (HashDigest ledgerHash : ledgerHashs) {
////				setConfig(conf,ledgerHash);
//				LedgerBindingConfig.BindingConfig bindingConfig = conf.getLedger(ledgerHash);
//			}
//		} catch (Exception e) {
//			ex =e;
//		} finally {
//			in.close();
//		}
//
//		assertNull(ex);
//	}

	/**
	 * 判断指定的对象跟测试模板是否一致；
	 * 
	 * @param conf
	 */
	private void assertLedgerBindingConfig(LedgerBindingConfig conf) {
		String[] expectedHashs = { "j5ptBmn67B2p3yki3ji1j2ZMjnJhrUvP4kFpGmcXgvrhmk",
				"j5kLUENMvcUooZjKfz2bEYU6zoK9DAqbdDDU8aZEZFR4qf" };
		HashDigest[] hashs = conf.getLedgerHashs();
		for (int i = 0; i < hashs.length; i++) {
			assertEquals(expectedHashs[i], hashs[i].toBase58());
		}

		BindingConfig bindingConf_0 = conf.getLedger(hashs[0]);
		assertEquals("1", bindingConf_0.getParticipant().getAddress());
		assertEquals("keys/jd-com.priv", bindingConf_0.getParticipant().getPkPath());
		assertEquals("AdSXsf5QJpy", bindingConf_0.getParticipant().getPk());
		assertNull(bindingConf_0.getParticipant().getPassword());

		assertEquals("redis://ip:port/1", bindingConf_0.getDbConnection().getUri());
		assertEquals("kksfweffj", bindingConf_0.getDbConnection().getPassword());

		BindingConfig bindingConf_1 = conf.getLedger(hashs[1]);
		assertEquals("2", bindingConf_1.getParticipant().getAddress());
		assertEquals("keys/jd-com-1.priv", bindingConf_1.getParticipant().getPkPath());
		assertNull(bindingConf_1.getParticipant().getPk());
		assertEquals("kksafe", bindingConf_1.getParticipant().getPassword());

		assertEquals("redis://ip:port/2", bindingConf_1.getDbConnection().getUri());
		assertNull(bindingConf_1.getDbConnection().getPassword());
	}

}
