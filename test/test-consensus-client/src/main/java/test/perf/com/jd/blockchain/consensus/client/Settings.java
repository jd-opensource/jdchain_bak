package test.perf.com.jd.blockchain.consensus.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.jd.blockchain.utils.io.ByteArray;

/**
 * @author huanghaiquan
 *
 */
@Configuration
@ConfigurationProperties(prefix = "client")
public class Settings {
	
	private String name;

	private ConsensusSetting consensus;

	public ByteArray getLedgerHash() {
		return ledgerHash;
	}

	public void setLedgerHash(ByteArray ledgerHash) {
		this.ledgerHash = ledgerHash;
	}

	private ByteArray ledgerHash;

	public ConsensusSetting getConsensus() {
		return consensus;
	}

	public void setConsensus(ConsensusSetting consensus) {
		this.consensus = consensus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// ===================================================================================================
	/**
	 * 共识相关的参数设置；
	 * 
	 * @author huanghaiquan
	 *
	 */
	public static class ConsensusSetting {

		/**
		 * 本机用于共识的IP地址；
		 */
		private String ip;

		/**
		 * 本机用于共识的端口；
		 */
		private int port;

		public ConsensusSetting() {
		}

		public ConsensusSetting(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		private BftsmartSetting bftsmartConfig = new BftsmartSetting("config/system.config", "config/hosts.config");

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public BftsmartSetting getBftsmartConfig() {
			return bftsmartConfig;
		}

		public void setBftsmartConfig(BftsmartSetting bftsmartConfig) {
			this.bftsmartConfig = bftsmartConfig;
		}

	}

	public static class BftsmartSetting {

		private String hosts;

		private String system;

		private String home;

		public BftsmartSetting() {
		}

		public BftsmartSetting(String systemConfig, String hostsConfig) {
			this.system = systemConfig;
			this.hosts = hostsConfig;
		}

		public String getHosts() {
			return hosts;
		}

		public void setHosts(String hosts) {
			this.hosts = hosts;
		}

		public String getSystem() {
			return system;
		}

		public void setSystem(String system) {
			this.system = system;
		}

		public String getHome() {
			return home;
		}

		public void setHome(String home) {
			this.home = home;
		}

	}
}
