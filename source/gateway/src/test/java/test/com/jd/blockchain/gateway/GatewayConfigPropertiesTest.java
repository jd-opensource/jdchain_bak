package test.com.jd.blockchain.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.gateway.GatewayConfigProperties;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.security.ShaUtils;

public class GatewayConfigPropertiesTest {

	@Test
	public void test() {
		ClassPathResource gatewayConfigResource = new ClassPathResource("gateway.conf");
		try (InputStream in = gatewayConfigResource.getInputStream()) {
			GatewayConfigProperties configProps = GatewayConfigProperties.resolve(in);
			assertEquals("192.168.10.108", configProps.http().getHost());
			assertEquals(80, configProps.http().getPort());
			assertNull(configProps.http().getContextPath());

			assertEquals("10.1.6.61", configProps.masterPeerAddress().getHost());
			assertEquals(7100, configProps.masterPeerAddress().getPort());
			assertTrue(configProps.masterPeerAddress().isSecure());

			assertEquals("http://192.168.1.1:10001", configProps.dataRetrievalUrl());
			
			assertEquals("keys/default.priv", configProps.keys().getDefault().getPrivKeyPath());
			assertEquals("64hnH4a8n48LeEmNxUPcaZ1J", configProps.keys().getDefault().getPrivKeyValue());
			assertEquals("a8n48LeEP", configProps.keys().getDefault().getPrivKeyPassword());

		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}


	@Test
	public void generateDefaultPassword() {
		//generate default base58 password for gateway.conf
		String password = "abc";
		String encodePassword;
		byte[] pwdBytes = BytesUtils.toBytes(password, "UTF-8");
		encodePassword = Base58Utils.encode(ShaUtils.hash_256(pwdBytes));

		return;

	}

}
