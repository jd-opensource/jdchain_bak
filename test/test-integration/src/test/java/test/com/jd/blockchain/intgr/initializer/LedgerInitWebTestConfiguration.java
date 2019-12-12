package test.com.jd.blockchain.intgr.initializer;

import com.jd.blockchain.storage.service.impl.composite.CompositeConnectionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.jd.blockchain.tools.initializer.web.InitWebSecurityConfiguration;
import com.jd.blockchain.tools.initializer.web.InitWebServerConfiguration;

@SpringBootApplication
@Configuration
@EnableConfigurationProperties
@Import(value = { InitWebServerConfiguration.class, InitWebSecurityConfiguration.class })
public class LedgerInitWebTestConfiguration {

	@Bean
	public CompositeConnectionFactory getCompositeConnectionFactory() {
		return new CompositeConnectionFactory();
	}

//	@Bean
//	public LedgerManager getLedgerManager() {
//		return new LedgerManager();
//	}

}
