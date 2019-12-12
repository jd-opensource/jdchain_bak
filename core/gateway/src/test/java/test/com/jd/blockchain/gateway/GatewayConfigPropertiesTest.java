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
			assertEquals("0.0.0.0", configProps.http().getHost());
			assertEquals(8081, configProps.http().getPort());
			assertNull(configProps.http().getContextPath());

			assertEquals("127.0.0.1", configProps.masterPeerAddress().getHost());
			assertEquals(12000, configProps.masterPeerAddress().getPort());
			assertTrue(configProps.masterPeerAddress().isSecure());

			assertEquals("http://127.0.0.1:10001", configProps.dataRetrievalUrl());

			assertEquals("3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9", configProps.keys().getDefault().getPubKeyValue());
			assertNull(configProps.keys().getDefault().getPrivKeyPath());
			assertEquals("177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x", configProps.keys().getDefault().getPrivKeyValue());
			assertEquals("DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY", configProps.keys().getDefault().getPrivKeyPassword());

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
