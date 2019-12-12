package test.com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.bftsmart.*;
import com.jd.blockchain.consensus.bftsmart.client.BftsmartClientConfig;
import com.jd.blockchain.consensus.bftsmart.client.BftsmartConsensusClient;
import com.jd.blockchain.consensus.bftsmart.client.BftsmartMessageService;
import com.jd.blockchain.consensus.bftsmart.service.BftsmartNodeServer;
import com.jd.blockchain.consensus.bftsmart.service.BftsmartServerSettingConfig;
import com.jd.blockchain.consensus.service.ServerSettings;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.utils.PropertiesUtils;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.net.NetworkAddress;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyClientTest {

    int number = 1500000;

    int peerStartPort = 11000;

    int nodeNum = 4;

    Random random = new Random();

    byte[] bytes = null;

    CountDownLatch startPeer = new CountDownLatch(nodeNum);

    private static Properties  bftsmartConf;

    private final ExecutorService nodeStartPools = Executors.newCachedThreadPool();

    private final ExecutorService txSendPools = Executors.newFixedThreadPool(20);

    static {
        ClassPathResource configResource = new ClassPathResource("system.config");
        try {
            try (InputStream in = configResource.getInputStream()) {
                bftsmartConf = PropertiesUtils.load(in, BytesUtils.DEFAULT_CHARSET);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void peerStart(BftsmartNodeServer[] nodeServers) {

        BftsmartNodeSettings[] nodesSettings = new BftsmartNodeSettings[nodeNum];

        for (int i = 0; i < nodeNum; i++) {
            BlockchainKeypair keyPair = BlockchainKeyGenerator.getInstance().generate();
            PubKey pubKey = keyPair.getPubKey();
            NetworkAddress peerNodeServ = new NetworkAddress("127.0.0.1", peerStartPort + i * 10);
            NodeSettings node = new BftsmartNodeConfig(pubKey, i, peerNodeServ);
            nodesSettings[i] = (BftsmartNodeSettings) node;
        }

        BftsmartConsensusConfig consensusConfig = new BftsmartConsensusConfig(nodesSettings,
//                null,
                PropertiesUtils.getOrderedValues(bftsmartConf));

        for (int j = 0; j < nodeNum; j++) {
            BftsmartServerSettingConfig serverSettings = new BftsmartServerSettingConfig();
            serverSettings.setReplicaSettings(nodesSettings[j]);
            serverSettings.setConsensusSettings(consensusConfig);
            BftsmartNodeServer server = new BftsmartNodeServer(serverSettings, null, null);
            nodeServers[j] = server;
            nodeStartPools.execute(() -> {
                server.start();
                startPeer.countDown();
            });
        }
    }

    public void proxyClientSend(BftsmartNodeServer nodeServer) {
        BftsmartClientIncomingConfig clientIncomingConfig = new BftsmartClientIncomingConfig();
        BlockchainKeypair keyPair = BlockchainKeyGenerator.getInstance().generate();
        clientIncomingConfig.setPubKey(keyPair.getPubKey());
        clientIncomingConfig.setClientId(0);
        clientIncomingConfig.setConsensusSettings(nodeServer.getConsensusSetting());
        clientIncomingConfig.setTomConfig(BinarySerializeUtils.serialize(nodeServer.getTomConfig()));
        clientIncomingConfig.setTopology(BinarySerializeUtils.serialize(nodeServer.getTopology()));

        BftsmartClientConfig clientSettings = new BftsmartClientConfig(clientIncomingConfig);
        BftsmartConsensusClient consensusClient = new BftsmartConsensusClient(clientSettings);
        bytes = new byte[1024];

        BftsmartMessageService messageService = (BftsmartMessageService) consensusClient.getMessageService();

        for (int j = 0; j < number; j++) {
                txSendPools.execute(() -> {
                    random.nextBytes(bytes);
                    messageService.sendOrdered(bytes);
                });
        }

    }

//    @Test
    public void sendTest() {

        BftsmartNodeServer[] nodeServers = new BftsmartNodeServer[nodeNum];
        //启动服务
        peerStart(nodeServers);

        try {
            startPeer.await();
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        proxyClientSend(nodeServers[0]);


        try {
            Thread.sleep(50000);
            System.out.println("send test complete!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
