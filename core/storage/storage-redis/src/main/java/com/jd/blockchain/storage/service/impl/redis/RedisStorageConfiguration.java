package com.jd.blockchain.storage.service.impl.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.jd.blockchain.storage.service.DbConnectionFactory;

@Configuration
@ComponentScan
public class RedisStorageConfiguration {
	
//	@Autowired
//	private JedisProperties jedisProps;
	
//	@Bean
//	public Jedis jedis() {
//		Jedis jedis = new Jedis(jedisProps.getHost(), jedisProps.getPort());
//		jedis.connect();
//		jedis.select(jedisProps.getDb());
//		return jedis;
//	}
	
	@ConditionalOnMissingBean
	@Bean
	public DbConnectionFactory redisConnectionFactory() {
		return new RedisConnectionFactory();
	}
	
}
