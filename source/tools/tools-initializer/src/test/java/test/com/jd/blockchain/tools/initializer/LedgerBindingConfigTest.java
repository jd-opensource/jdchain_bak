package test.com.jd.blockchain.tools.initializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig.BindingConfig;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesUtils;

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

	/**
	 * 判断指定的对象跟测试模板是否一致；
	 * 
	 * @param conf
	 */
	private void assertLedgerBindingConfig(LedgerBindingConfig conf) {
		String[] expectedHashs = { "6HaDnSu4kY6vNAdSXsf5QJpyYxrtxxoH1tn8dDRvbRD8K",
				"64hnH4a8n48LeEP5HU2bMWmNxUPcaZ1JRCehRwvuNS8Ty" };
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
