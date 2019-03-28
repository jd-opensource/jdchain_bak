package test.perf.com.jd.blockchain.consensus.node;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author huanghaiquan
 *
 */
@Configuration
@ConfigurationProperties(prefix = "bft")
public class Settings {

	private String name;

	private String systemConfig = "config/system.config";

	private String nodesConfig = "config/hosts.config";

//	private String runtimeHome = "./runtime";


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// ===================================================================================================

	public String getSystemConfig() {
		return systemConfig;
	}

	public void setSystemConfig(String systemConfig) {
		this.systemConfig = systemConfig;
	}

	public String getNodesConfig() {
		return nodesConfig;
	}

	public void setNodesConfig(String nodesConfig) {
		this.nodesConfig = nodesConfig;
	}

//	public String getRuntimeHome() {
//		return runtimeHome;
//	}
//
//	public void setRuntimeHome(String runtimeHome) {
//		this.runtimeHome = runtimeHome;
//	}

}
