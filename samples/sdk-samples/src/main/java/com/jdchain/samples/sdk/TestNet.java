package com.jdchain.samples.sdk;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.gateway.GatewayConfigProperties;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.PresetAnswerPrompter;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jdchain.samples.sdk.testnet.GatewayRunner;
import com.jdchain.samples.sdk.testnet.NodeWebContext;
import com.jdchain.samples.sdk.testnet.PartNode;
import com.jdchain.samples.sdk.testnet.PeerServer;

import utils.concurrent.ThreadInvoker;
import utils.io.FileUtils;
import utils.net.NetworkAddress;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 测试网络
 * 初始化启动基于内存的4节点JD Chain网络
 */
public class TestNet {

    // 测试网络公私钥及私钥密码信息
    private static final String[] PUB_KEYS = {
            "7VeRLdGtSz1Y91gjLTqEdnkotzUfaAqdap3xw6fQ1yKHkvVq",
            "7VeRBsHM2nsGwP8b2ufRxz36hhNtSqjKTquzoa4WVKWty5sD",
            "7VeRAr3dSbi1xatq11ZcF7sEPkaMmtZhV9shonGJWk9T4pLe",
            "7VeRKoM5RE6iFXr214Hsiic2aoqCQ7MEU1dHQFRnjXQcReAS"};
    private static final String[] PRIV_KEYS = {
            "177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x",
            "177gju9p5zrNdHJVEQnEEKF4ZjDDYmAXyfG84V5RPGVc5xFfmtwnHA7j51nyNLUFffzz5UT",
            "177gjtwLgmSx5v1hFb46ijh7L9kdbKUpJYqdKVf9afiEmAuLgo8Rck9yu5UuUcHknWJuWaF",
            "177gk1pudweTq5zgJTh8y3ENCTwtSFsKyX7YnpuKPo7rKgCkCBXVXh5z2syaTCPEMbuWRns"};
    private static final String PASSWORD = "abc";

    // 存储配置
    private static final String[] dbConnections = {
            "memory://local/0",
            "memory://local/1",
            "memory://local/2",
            "memory://local/3"};

    // 共识协议
    private static final String BFTSMART_PROVIDER = "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider";

    // node节点服务端口，共识节点还会占用8910/8920/8930/8940/8911/8921/8931/8941用于共识服务，可以通过修改resources/network/bftsmart.config修改
    private static final int[] NODE_PORTS = {12000, 12010, 12020, 12030};

    // 网关服务端口
    private static final int GATEWAY_PORT = 11000;

    public static void main(String[] args) {
        try {
            Configurator.setRootLevel(Level.OFF);

            // 内存账本初始化
            HashDigest ledgerHash = initLedger();

            // 启动Peer节点
            PeerServer[] peerNodes = peerNodeStart(ledgerHash);

            // 睡20秒，等待共识节点启动成功
            Thread.sleep(20000);

            // 启动网关
            startGateway(peerNodes);

            // 睡10秒，等待网关启动成功
            Thread.sleep(10000);

            // 初始化样例数据
            initSampleData(ledgerHash);

            System.out.println(" ------------------- START NETWORK SUCCESS ------------------- ");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" ------------------- START NETWORK FAILED ------------------- ");
            System.exit(-1);
        }
    }

    private static HashDigest initLedger() throws IOException {
        Prompter consolePrompter = new PresetAnswerPrompter("N");
        LedgerInitProperties initSetting = LedgerInitProperties.resolve(new ClassPathResource("testnet/ledger.init").getInputStream());

        ParticipantNode[] participantNodes = new ParticipantNode[PUB_KEYS.length];
        for (int i = 0; i < PUB_KEYS.length; i++) {
            participantNodes[i] = new PartNode(i, KeyGenUtils.decodePubKey(PUB_KEYS[i]), ParticipantNodeState.CONSENSUS);
        }

        NetworkAddress initAddr0 = initSetting.getConsensusParticipant(0).getInitializerAddress();
        NodeWebContext node0 = new NodeWebContext(0, initAddr0);

        NetworkAddress initAddr1 = initSetting.getConsensusParticipant(1).getInitializerAddress();
        NodeWebContext node1 = new NodeWebContext(1, initAddr1);

        NetworkAddress initAddr2 = initSetting.getConsensusParticipant(2).getInitializerAddress();
        NodeWebContext node2 = new NodeWebContext(2, initAddr2);

        NetworkAddress initAddr3 = initSetting.getConsensusParticipant(3).getInitializerAddress();
        NodeWebContext node3 = new NodeWebContext(3, initAddr3);

        PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD);
        PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[1], PASSWORD);
        PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[2], PASSWORD);
        PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[3], PASSWORD);

        CountDownLatch quitLatch = new CountDownLatch(4);

        DBConnectionConfig testDb0 = new DBConnectionConfig();
        testDb0.setConnectionUri(dbConnections[0]);
        ThreadInvoker.AsyncCallback<HashDigest> callback0 = node0.startInit(privkey0, initSetting, testDb0, consolePrompter,
                quitLatch);

        DBConnectionConfig testDb1 = new DBConnectionConfig();
        testDb1.setConnectionUri(dbConnections[1]);
        ThreadInvoker.AsyncCallback<HashDigest> callback1 = node1.startInit(privkey1, initSetting, testDb1, consolePrompter,
                quitLatch);

        DBConnectionConfig testDb2 = new DBConnectionConfig();
        testDb2.setConnectionUri(dbConnections[2]);
        ThreadInvoker.AsyncCallback<HashDigest> callback2 = node2.startInit(privkey2, initSetting, testDb2, consolePrompter,
                quitLatch);

        DBConnectionConfig testDb03 = new DBConnectionConfig();
        testDb03.setConnectionUri(dbConnections[3]);
        ThreadInvoker.AsyncCallback<HashDigest> callback3 = node3.startInit(privkey3, initSetting, testDb03, consolePrompter,
                quitLatch);

        HashDigest ledgerHash0 = callback0.waitReturn();
        HashDigest ledgerHash1 = callback1.waitReturn();
        HashDigest ledgerHash2 = callback2.waitReturn();
        HashDigest ledgerHash3 = callback3.waitReturn();
        assertNotNull(ledgerHash0);
        assertEquals(ledgerHash0, ledgerHash1);
        assertEquals(ledgerHash0, ledgerHash2);
        assertEquals(ledgerHash0, ledgerHash3);

        return ledgerHash0;
    }

    private static PeerServer[] peerNodeStart(HashDigest ledgerHash) {
        NetworkAddress peerSrvAddr0 = new NetworkAddress("127.0.0.1", NODE_PORTS[0]);
        LedgerBindingConfig bindingConfig0 = loadBindingConfig(0, ledgerHash);
        PeerServer peer0 = new PeerServer(peerSrvAddr0, bindingConfig0);

        NetworkAddress peerSrvAddr1 = new NetworkAddress("127.0.0.1", NODE_PORTS[1]);
        LedgerBindingConfig bindingConfig1 = loadBindingConfig(1, ledgerHash);
        PeerServer peer1 = new PeerServer(peerSrvAddr1, bindingConfig1);

        NetworkAddress peerSrvAddr2 = new NetworkAddress("127.0.0.1", NODE_PORTS[2]);
        LedgerBindingConfig bindingConfig2 = loadBindingConfig(2, ledgerHash);
        PeerServer peer2 = new PeerServer(peerSrvAddr2, bindingConfig2);

        NetworkAddress peerSrvAddr3 = new NetworkAddress("127.0.0.1", NODE_PORTS[3]);
        LedgerBindingConfig bindingConfig3 = loadBindingConfig(3, ledgerHash);
        PeerServer peer3 = new PeerServer(peerSrvAddr3, bindingConfig3);

        ThreadInvoker.AsyncCallback<Object> peerStarting0 = peer0.start();
        ThreadInvoker.AsyncCallback<Object> peerStarting1 = peer1.start();
        ThreadInvoker.AsyncCallback<Object> peerStarting2 = peer2.start();
        ThreadInvoker.AsyncCallback<Object> peerStarting3 = peer3.start();

        peerStarting0.waitReturn();
        peerStarting1.waitReturn();
        peerStarting2.waitReturn();
        peerStarting3.waitReturn();

        return new PeerServer[]{peer0, peer1, peer2, peer3};
    }

    private static LedgerBindingConfig loadBindingConfig(int id, HashDigest ledgerHash) {
        LedgerBindingConfig ledgerBindingConfig;
        String newLedger = ledgerHash.toBase58();
        String resourceClassPath = "testnet/ledger-binding-mem-" + id + ".conf";
        String ledgerBindingUrl = TestNet.class.getResource("/") + resourceClassPath;

        try {
            URL url = new URL(ledgerBindingUrl);
            File ledgerBindingConf = new File(url.getPath());
            if (ledgerBindingConf.exists()) {
                List<String> readLines = org.apache.commons.io.FileUtils.readLines(ledgerBindingConf);

                List<String> writeLines = new ArrayList<>();

                if (readLines != null && !readLines.isEmpty()) {
                    String oldLedgerLine = null;
                    for (String readLine : readLines) {
                        if (readLine.startsWith("ledger")) {
                            oldLedgerLine = readLine;
                            break;
                        }
                    }
                    String[] oldLedgerArray = oldLedgerLine.split("=");

                    String oldLedger = oldLedgerArray[1];
                    if (!oldLedger.equalsIgnoreCase(newLedger)) {
                        for (String readLine : readLines) {
                            String newLine = readLine.replace(oldLedger, newLedger);
                            writeLines.add(newLine);
                        }
                    }
                    if (!writeLines.isEmpty()) {
                        org.apache.commons.io.FileUtils.writeLines(ledgerBindingConf, writeLines);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClassPathResource res = new ClassPathResource(resourceClassPath);
        try (InputStream in = res.getInputStream()) {
            ledgerBindingConfig = LedgerBindingConfig.resolve(in);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return ledgerBindingConfig;
    }

    private static void startGateway(PeerServer[] peerNodes) {
        GatewayConfigProperties.KeyPairConfig keyPairConfig = new GatewayConfigProperties.KeyPairConfig();
        keyPairConfig.setPubKeyValue(PUB_KEYS[0]);
        keyPairConfig.setPrivKeyValue(PRIV_KEYS[0]);
        keyPairConfig.setPrivKeyPassword(KeyGenUtils.encodePasswordAsBase58(PASSWORD));

        GatewayRunner gateway = new GatewayRunner("127.0.0.1", GATEWAY_PORT, keyPairConfig, new String[]{BFTSMART_PROVIDER}, null, peerNodes[0].getServiceAddress());

        ThreadInvoker.AsyncCallback<Object> gwStarting = gateway.start();

        gwStarting.waitReturn();
    }

    private static void initSampleData(HashDigest ledgerHash) throws IOException {
        BlockchainKeypair admin = new BlockchainKeypair(KeyGenUtils.decodePubKey(PUB_KEYS[0]), KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD));
        BlockchainKeypair user = new BlockchainKeypair(
                KeyGenUtils.decodePubKey("7VeRCfSaoBW3uRuvTqVb26PYTNwvQ1iZ5HBY92YKpEVN7Qht"),
                KeyGenUtils.decodePrivKey("177gjuGapUVdLnEDAkqjQWhZxHh5jL5W6Hg1q8kbdsbk1BKht4QkmuB6dKvyJrgKTRmXSgK", "8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG"));

        BlockchainService blockchainService = GatewayServiceFactory.connect(
                "127.0.0.1", GATEWAY_PORT, false, admin).getBlockchainService();

        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        // 初始化一个用户
        txTemp.users().register(user.getIdentity());
        // 创建角色 MANAGER
        txTemp.security().roles().configure("SAMPLE-ROLE")
                .enable(LedgerPermission.WRITE_DATA_ACCOUNT)
                .enable(TransactionPermission.DIRECT_OPERATION);

        // 设置用户角色权限
        txTemp.security().authorziations().forUser(user.getAddress()).authorize("SAMPLE-ROLE");
        // 初始化一个数据账户并设置KV
        txTemp.dataAccounts().register(user.getIdentity());
        txTemp.dataAccount(user.getAddress()).setText("sample-key", "sample-value", -1);
        // 初始化一个事件账户并发布一个事件
        txTemp.eventAccounts().register(user.getIdentity());
        txTemp.eventAccount(user.getAddress()).publish("sample-event", "sample-content", -1);
        // 初始化一个合约
        txTemp.contracts().deploy(user.getIdentity(), FileUtils.readBytes(new ClassPathResource("contract-samples-1.5.0.RELEASE.car").getFile()));

        PreparedTransaction ptx = txTemp.prepare();
        ptx.sign(admin);

        TransactionResponse response = ptx.commit();
        Assert.assertTrue(response.isSuccess());
    }

}
