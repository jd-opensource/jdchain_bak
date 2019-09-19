package com.jd.blockchain.tools.initializer.web;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

@Configuration
@ComponentScan
public class InitWebServerConfiguration extends WebMvcConfigurerAdapter {
	static {
		JSONSerializeUtils.disableCircularReferenceDetect();
		JSONSerializeUtils.configStringSerializer(ByteArray.class);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, new LedgerInitMessageConverter());
	}

	@Bean
	public InitConsensusServiceFactory initCsServiceFactory() {
		return new HttpInitConsensServiceFactory();
	}
	
	@Bean
	public LedgerManager getLedgerManager() {
		return new LedgerManager();
	}
	
}
