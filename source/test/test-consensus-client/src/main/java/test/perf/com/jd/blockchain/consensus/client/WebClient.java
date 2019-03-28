//package test.perf.com.jd.blockchain.consensus.client;
//
//import java.util.Random;
//
//import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
//import com.jd.blockchain.consensus.bftsmart.BftsmartTopology;
//import com.jd.blockchain.consensus.bftsmart.client.BftsmartClientConfig;
//import com.jd.blockchain.consensus.bftsmart.client.BftsmartClientSettings;
//import com.jd.blockchain.consensus.bftsmart.client.BftsmartConsensusClient;
//import com.jd.blockchain.crypto.asymmetric.PubKey;
//import com.jd.blockchain.ledger.BlockchainKeyGenerator;
//import my.utils.http.agent.HttpServiceAgent;
//import my.utils.http.agent.ServiceEndpoint;
//import my.utils.serialize.binary.BinarySerializeUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//
//import my.utils.codec.HexUtils;
//
//@RestController
//@RequestMapping(path="bft")
//public class WebClient {
//
//	@Autowired
//	private Settings settings;
//
//	private Random random=new Random();
//
//	private volatile byte[] msgBytes;
//
//	private BftsmartConsensusClient client;
//
//	private BftsmartConsensusSettings setting;
//
//	private BftsmartTopology topology;
//
//	public WebClient() {
//		random = new Random();
//		updateMessage(32);
//	}
//
//	private void init() {
//	}
//
//	@RequestMapping(path="/message", method=RequestMethod.GET)
//	public String getMessage() {
//		return  String.format("[size=%s]--[%s]", msgBytes.length, HexUtils.encode(msgBytes));
//	}
//
//	private void updateMessage(int size) {
//		msgBytes = new byte[size];
//		random.nextBytes(msgBytes);
//	}
//
//	@RequestMapping(path="/message/set/{size}", method=RequestMethod.GET)
//	public String setMessage(@PathVariable("size") int size) {
//		if (size < 1 || size > 1024*1024*200) {
//			return "Size cann't be less than 1 byte or great than 200 MB! ";
//		}
//		updateMessage(size);
//		return getMessage();
//	}
//
//	@RequestMapping(path="/test/ordered", method=RequestMethod.GET)
//	public String testOrdered() {
//		if (client == null) {
//			throw new IllegalStateException("client not exist");
//		}
//
//		//CallerInfo info = Profiler.registerInfo("jd-chain-bftsmart-performence-client-proxy", false, true);
//		byte[] retn = client.getMessageService().sendOrdered(msgBytes).get();
//		if(retn.length == 1 && retn[0] == 1){
//			//Profiler.registerInfoEnd(info);
//			return "OK";
//		}
//		//Profiler.functionError(info);
//		return "FAIL";
//	}
//
//
//	@RequestMapping(path="/connect/{from}/{port}", method=RequestMethod.GET)
//	public String connect(@PathVariable("from") String from, @PathVariable("port") int port){
//		if (client != null) {
//			throw new IllegalStateException("Has been connected to nodes!");
//		}
//		ServiceEndpoint endpoint = new ServiceEndpoint(from, port, false);
//		ConsensusSettingService settingService = HttpServiceAgent.createService(ConsensusSettingService.class, endpoint);
//		String hexSetting = settingService.getConsensusSettingsHex();
//		String hexTopology = settingService.getConsensusTopologyHex();
//		setting = BinarySerializeUtils.deserialize(HexUtils.decode(hexSetting));
//		topology = BinarySerializeUtils.deserialize(HexUtils.decode(hexTopology));
//
//        // 0.8.0 version
////		client = new BftsmartConsensusClient(0, setting, topology);
//
//		// 0.8.1 version
//		PubKey clientPubKey= BlockchainKeyGenerator.getInstance().generate().getPubKey();
//
//		BftsmartClientSettings clientSettings = new BftsmartClientConfig(0, clientPubKey,setting, topology);
//
//		BftsmartConsensusClient client = new BftsmartConsensusClient(clientSettings);
//
//		return "OK";
//	}
//
//}
