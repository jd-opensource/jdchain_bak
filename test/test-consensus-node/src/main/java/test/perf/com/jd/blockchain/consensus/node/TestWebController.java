package test.perf.com.jd.blockchain.consensus.node;

import java.util.Properties;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.Topology;
import com.jd.blockchain.utils.codec.HexUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bftsmart.reconfiguration.util.HostsConfig;

@RestController
public class TestWebController {

	@Autowired
	private Settings settings;

	private Properties systemProperties;

	private HostsConfig nodesConfig;

	private TestReplica replica;

	public TestWebController() {
	}

	@PostConstruct
	private void init() {
		nodesConfig = new HostsConfig(settings.getNodesConfig());
		systemProperties = FileUtils.readProperties(settings.getSystemConfig());
	}

	@RequestMapping(path = "/configs/consensus", method = RequestMethod.GET)
	public String getSystemProperties() {
		TreeSet<String> names = new TreeSet<>(systemProperties.stringPropertyNames());
		StringBuilder content = new StringBuilder();
		for (String n : names) {
			content.append(String.format("%s=%s<br>\r\n", n, systemProperties.getProperty(n)));
		}
		return content.toString();
	}

	@RequestMapping(path = "/configs/consensus/set/{key}/{value}", method = RequestMethod.GET)
	public String setSystemProperty(@PathVariable("key") String key, @PathVariable("value") String value) {
		systemProperties.setProperty(key, value);
		return getSystemProperties();
	}

	@RequestMapping(path = "/configs/consensus/set/default", method = RequestMethod.GET)
	public String setSystemDefaultProperty() {
		systemProperties = FileUtils.readProperties(settings.getSystemConfig());
		return getSystemProperties();
	}

	@RequestMapping(path = "/configs/nodes", method = RequestMethod.GET)
	public String getNodes() {
		StringBuilder content = new StringBuilder();
		int[] ids = nodesConfig.getHostsIds();
		for (int id : ids) {
			content.append(String.format("%s - %s:%s [%s] <br>\r\n", id, nodesConfig.getHost(id), nodesConfig.getPort(id),
					nodesConfig.getServerToServerPort(id)));
		}
		return content.toString();
	}


	@RequestMapping(path = "/configs/nodes/set/{id}/{host}/{port}", method = RequestMethod.GET)
	public String setNode(@PathVariable("id") int id, @PathVariable("host") String host,
						  @PathVariable("port") int port) {
		nodesConfig.add(id, host, port);
		return getNodes();
	}

	@RequestMapping(path = "/node/id", method = RequestMethod.GET)
	public String getNodeID() {
		return "Node.ID=" + (replica == null ? 0 : replica.getId());
	}

	@RequestMapping(path = "/node/status", method = RequestMethod.GET)
	public String getStatus() {
		return replica == null ? "STOPPED" : "RUNNING";
	}

	@RequestMapping(path = "/node/start/{id}", method = RequestMethod.GET)
	public String start(@PathVariable("id")int id) {
		if (replica != null ) {
			return "Already started!";
		}
		TestReplica replica = new TestReplica(id, systemProperties, nodesConfig);
		//after new TestConsensusReplica systemProperties field will be removed
		replica.start();
		this.replica = replica;
		return "success!";
	}

	@RequestMapping(path = "/node/stop", method = RequestMethod.GET)
	public String stop() {
		if (replica == null ) {
			return "Already stopped!";
		}
		TestReplica replica = this.replica;
		this.replica = null;
		replica.stop();
		return "stopped!";
	}


	@RequestMapping(path = "/node/topology", method = RequestMethod.GET)
	public String getNodesTopology(){
		if (replica == null ) {
			throw new IllegalStateException("Replica not start");
		}
		Topology tp = replica.getTopology().copyOf();
		byte[] bytesTP = BinarySerializeUtils.serialize(tp);
		return HexUtils.encode(bytesTP);
	}

	@RequestMapping(path = "/node/settings", method = RequestMethod.GET)
	public String getNodesSettings(){
		if (replica == null ) {
			throw new IllegalStateException("Replica not start");
		}
		ConsensusSettings settings = replica.getConsensusSetting();
		byte[] bytesSettings = BinarySerializeUtils.serialize(settings);
		return HexUtils.encode(bytesSettings);
	}

}
