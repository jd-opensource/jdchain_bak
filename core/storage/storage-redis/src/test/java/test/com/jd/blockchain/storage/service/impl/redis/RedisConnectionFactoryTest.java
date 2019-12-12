package test.com.jd.blockchain.storage.service.impl.redis;
import static org.junit.Assert.*;

import org.junit.Test;

import com.jd.blockchain.storage.service.impl.redis.RedisConnectionFactory;

public class RedisConnectionFactoryTest {

	@Test
	public void testConnectionString() {
		String connStr = "redis://192.168.1.2:6379/1";
		boolean match = RedisConnectionFactory.URI_PATTER.matcher(connStr).matches();
		
		connStr = "redis://192.168.1.2:6379/";
		match = RedisConnectionFactory.URI_PATTER.matcher(connStr).matches();
		assertTrue(match);
		
		connStr = "redis://192.168.1.2:6379";
		match = RedisConnectionFactory.URI_PATTER.matcher(connStr).matches();
		assertTrue(match);
		
		connStr = "redis://192.168.1.2:6379/33/kkew";
		match = RedisConnectionFactory.URI_PATTER.matcher(connStr).matches();
		assertTrue(match);
		
		connStr = "redis://192.168.1.2:6379/kkf/";
		match = RedisConnectionFactory.URI_PATTER.matcher(connStr).matches();
		assertFalse(match);
	}

}
