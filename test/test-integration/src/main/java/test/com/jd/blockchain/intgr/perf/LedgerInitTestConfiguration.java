package test.com.jd.blockchain.intgr.perf;

import com.jd.blockchain.storage.service.utils.MemoryDBConnFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//import com.jd.blockchain.storage.service.utils.MemoryBasedDb;
import com.jd.blockchain.tools.initializer.web.InitWebSecurityConfiguration;
import com.jd.blockchain.tools.initializer.web.InitWebServerConfiguration;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@Import(value = { InitWebServerConfiguration.class, InitWebSecurityConfiguration.class })
public class LedgerInitTestConfiguration {

	@Bean
	public MemoryDBConnFactory getStorageDB() {
		return new MemoryDBConnFactory();
	}

//	@Bean
//	public LedgerManager getLedgerManager() {
//		return new LedgerManager();
//	}

}
