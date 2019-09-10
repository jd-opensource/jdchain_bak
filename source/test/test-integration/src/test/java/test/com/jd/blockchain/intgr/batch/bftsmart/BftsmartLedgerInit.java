/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: BftsmartLedgerInit
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/10 下午5:03
 * Description:
 */
package test.com.jd.blockchain.intgr.batch.bftsmart;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.gateway.GatewayConfigProperties;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.peer.PeerServerBooter;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.LedgerInitCommand;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import test.com.jd.blockchain.intgr.GatewayTestRunner;
import test.com.jd.blockchain.intgr.IntegrationBase;
import test.com.jd.blockchain.intgr.LedgerInitConsensusConfig;
import test.com.jd.blockchain.intgr.PeerTestRunner;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static test.com.jd.blockchain.intgr.IntegrationBase.buildLedgers;
import static test.com.jd.blockchain.intgr.IntegrationBase.validKeyPair;
import static test.com.jd.blockchain.intgr.IntegrationBase.validKvWrite;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/10
 * @since 1.0.0
 */

public class BftsmartLedgerInit {

//    private final ExecutorService ledgerInitPools = Executors.newFixedThreadPool(64);

    private final ExecutorService nodeStartPools = Executors.newCachedThreadPool();

    private final ExecutorService txSendPools = Executors.newFixedThreadPool(20);

    private static String localPath = FileUtils.getCurrentDir() + File.separator + "bftsmart-rocks.db";

    private BftsmartConfig bftsmartConfig = new BftsmartConfig();

    private static final int peerStartPort = 20000;

    private static final int registerUserSize = 500;

    private static final int kvStorageSize = 1000;

    private static final boolean isBrowser = true;

    @Before
    public void before() {
        File localDir = new File(localPath);
        if (!localDir.exists()) {
            localDir.mkdir();
        }
    }

    @Test
    public void localConf4NodesLoad() {
        bftsmartConfig.test4ConfigLoad();
        localConfLoad(4);
        ledgerInitNodes(4);
    }

    @Test
    public void start4Nodes() {
        localConf4NodesLoad();
        PeerTestRunner[] peerNodes = startNodes(4);
        // 检查账本一致性
        LedgerQuery[] ledgers = checkNodes(peerNodes);

        txRequestTest(peerNodes, ledgers);
    }

    @Test
    public void localConf8NodesLoad() {
        bftsmartConfig.test8ConfigLoad();
        localConfLoad(8);
        ledgerInitNodes(8);
    }

    @Test
    public void start8Nodes() {
        localConf8NodesLoad();
        PeerTestRunner[] peerNodes = startNodes(8);
        // 检查账本一致性
        LedgerQuery[] ledgers = checkNodes(peerNodes);

        txRequestTest(peerNodes, ledgers);
    }

    @Test
    public void localConf16NodesLoad() {
        bftsmartConfig.test16ConfigLoad();
        localConfLoad(16);
        ledgerInitNodes(16);
    }

    @Test
    public void start16Nodes() {
        localConf16NodesLoad();
        PeerTestRunner[] peerNodes = startNodes(16);
        // 检查账本一致性
        LedgerQuery[] ledgers = checkNodes(peerNodes);

        txRequestTest(peerNodes, ledgers);
    }

    @Test
    public void localConf32NodesLoad() {
        bftsmartConfig.test32ConfigLoad();
        localConfLoad(32);
        ledgerInitNodes(32);
    }

    @Test
    public void start32Nodes() {
        localConf32NodesLoad();
//        ledgerInitPools.shutdown();
        PeerTestRunner[] peerNodes = startNodes(32);
        // 检查账本一致性
        LedgerQuery[] ledgers = checkNodes(peerNodes);

        txRequestTest(peerNodes, ledgers);
    }

    @Test
    public void localConf64NodesLoad() {
        bftsmartConfig.test64ConfigLoad();
        localConfLoad(64);
        ledgerInitNodes(64);
    }

    @Test
    public void start64Nodes() {
        localConf64NodesLoad();
        PeerTestRunner[] peerNodes = startNodes(64);
        // 检查账本一致性
        LedgerQuery[] ledgers = checkNodes(peerNodes);

        txRequestTest(peerNodes, ledgers);
    }

    public void txRequestTest(PeerTestRunner[] peerNodes, LedgerQuery[] ledgers) {
        // 测试K-V
        GatewayTestRunner gateway = initGateWay(peerNodes[0]);

        LedgerQuery ledgerRepository = ledgers[0];

        HashDigest ledgerHash = ledgerRepository.getHash();

        GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway.getServiceAddress());

        PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(BftsmartConfig.PRIV_KEY[0], IntegrationBase.PASSWORD);

        PubKey pubKey0 = KeyGenUtils.decodePubKey(BftsmartConfig.PUB_KEY[0]);

        AsymmetricKeypair adminKey = new AsymmetricKeypair(pubKey0, privkey0);

        BlockchainService blockchainService = gwsrvFact.getBlockchainService();

        CountDownLatch cdlUser = new CountDownLatch(registerUserSize);
        System.out.println("--------- Register Users Start ---------");

        for (int i = 0; i < registerUserSize; i++) {
            txSendPools.execute(() -> {
                IntegrationBase.KeyPairResponse userResponse = IntegrationBase.testSDK_RegisterUser(adminKey, ledgerHash, blockchainService);
//                validKeyPair(userResponse, ledgerRepository, IntegrationBase.KeyPairType.USER);
                cdlUser.countDown();
            });
        }
        IntegrationBase.KeyPairResponse dataAccountResponse = null;
        try {
            System.out.println("--------- Register Users Waiting ---------");
            cdlUser.await();
            IntegrationBase.testConsistencyAmongNodes(ledgers);
            System.out.println("--------- Register Users Success ---------");
            System.out.println("--------- Register DataAccount Start ---------");
            dataAccountResponse = IntegrationBase.testSDK_RegisterDataAccount(adminKey, ledgerHash, blockchainService);
            validKeyPair(dataAccountResponse, ledgerRepository, IntegrationBase.KeyPairType.DATAACCOUNT);
            System.out.println("--------- Register DataAccount Success ---------");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        CountDownLatch cdlKv = new CountDownLatch(kvStorageSize);
//        BlockchainKeyPair da = dataAccountResponse.getKeyPair();
//        for (int i = 0; i < kvStorageSize; i++) {
//            txSendPools.execute(() -> {
//                IntegrationBase.testSDK_InsertData(adminKey, ledgerHash, blockchainService, da.getAddress());
//                cdlKv.countDown();
//            });
//        }

        try {
//            cdlKv.await();
            Thread.sleep(2000);
            IntegrationBase.testConsistencyAmongNodes(ledgers);
            System.out.println("--------- TestConsistencyAmongNodes Success ---------");
            if (isBrowser) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public GatewayTestRunner initGateWay(PeerTestRunner peerNode) {
        String encodedBase58Pwd = KeyGenUtils.encodePasswordAsBase58(LedgerInitializeTest.PASSWORD);

        GatewayConfigProperties.KeyPairConfig gwkey0 = new GatewayConfigProperties.KeyPairConfig();
        gwkey0.setPubKeyValue(BftsmartConfig.PUB_KEY[0]);
        gwkey0.setPrivKeyValue(BftsmartConfig.PRIV_KEY[0]);
        gwkey0.setPrivKeyPassword(encodedBase58Pwd);
        GatewayTestRunner gateway = new GatewayTestRunner("127.0.0.1", 11000, gwkey0,
                peerNode.getServiceAddress(), LedgerInitConsensusConfig.bftsmartProvider,null);

        ThreadInvoker.AsyncCallback<Object> gwStarting = gateway.start();

        gwStarting.waitReturn();

        return gateway;
    }

    public LedgerQuery[] checkNodes(PeerTestRunner[] peerNodes) {
        int size = peerNodes.length;
        LedgerBindingConfig[] ledgerBindingConfigs = new LedgerBindingConfig[size];
        DbConnectionFactory[] connectionFactories = new DbConnectionFactory[size];
        for (int i = 0; i < size; i++) {
            ledgerBindingConfigs[i] = peerNodes[i].getLedgerBindingConfig();
            connectionFactories[i] = peerNodes[i].getDBConnectionFactory();
        }

        // 执行测试用例之前，校验每个节点的一致性；
        LedgerQuery[] ledgers = buildLedgers(ledgerBindingConfigs, connectionFactories);
        IntegrationBase.testConsistencyAmongNodes(ledgers);
        return ledgers;
    }

    public PeerTestRunner[] startNodes(int size) {
        PeerTestRunner[] peerNodes = new PeerTestRunner[size];
        CountDownLatch countDownLatch = new CountDownLatch(size);
        try {
            for (int i = 0; i < size; i++) {
                final int index = i;
                nodeStartPools.execute(() -> {
                    try {
                        NetworkAddress peerSrvAddr = new NetworkAddress("127.0.0.1", peerStartPort + index * 10);
                        String ledgerBindingConf = BftsmartConfig.BFTSMART_DIR + "conf" + File.separator + index + File.separator + "ledger-binding.conf";
                        ClassPathResource ledgerBindingConfRes = new ClassPathResource(ledgerBindingConf);
                        LedgerBindingConfig bindingConfig = LedgerBindingConfig.resolve(ledgerBindingConfRes.getInputStream());
                        PeerTestRunner peer = new PeerTestRunner(peerSrvAddr, bindingConfig);
                        ThreadInvoker.AsyncCallback<Object> peerStarting = peer.start();
                        peerStarting.waitReturn();
                        peerNodes[index] = peer;
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return peerNodes;
    }

    public void ledgerInitNodes(int size) {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(size);
            for (int i = 0; i < size; i++) {
                final int index = i;
                nodeStartPools.execute((() -> {
                    try {
                        // 启动tool-booter
                        String[] args = new String[4];
                        args[0] = "-l";
                        String localConf = BftsmartConfig.BFTSMART_DIR + "conf" + File.separator + index + File.separator + "local-bftsmart-" + index + ".conf";
                        ClassPathResource localConfRes = new ClassPathResource(localConf);
                        args[1] = localConfRes.getFile().getAbsolutePath();
                        args[2] = "-i";
                        String ledgerInit = BftsmartConfig.BFTSMART_DIR + "ledger_init_bftsmart-" + size + ".init";
                        ClassPathResource ledgerInitRes = new ClassPathResource(ledgerInit);
                        args[3] = ledgerInitRes.getFile().getAbsolutePath();
                        LedgerInitCommand.main(args);
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
            }
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void localConfLoad(int size) {
        for (int i = 0; i < size; i++) {
            localConfAllLoad(i, size);
        }
    }

    public void localConfAllLoad(int index, int size) {
        String file = BftsmartConfig.BFTSMART_DIR + "conf" + File.separator + index + File.separator + "local-bftsmart-" + index + ".conf";
        ClassPathResource res = new ClassPathResource(file);
        try {
            File resFile = res.getFile();
            // 处理新内容
            List<String> newFileContent = handleLocalConfContent(index, size);
            org.apache.commons.io.FileUtils.writeLines(resFile, newFileContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> handleLocalConfContent(int index, int size) throws Exception {
        List<String> newFileContent = new ArrayList<>();
        newFileContent.add("#当前参与方的 id");
        newFileContent.add("local.parti.id=" + index);
        newFileContent.add("");

        newFileContent.add("#当前参与方的公钥");
        newFileContent.add("local.parti.pubkey=" + BftsmartConfig.PUB_KEY[index]);
        newFileContent.add("");

        newFileContent.add("#当前参与方的私钥（密文编码）");
        newFileContent.add("local.parti.privkey=" + BftsmartConfig.PRIV_KEY[index]);
        newFileContent.add("");

        newFileContent.add("#当前参与方的私钥解密密钥(原始口令的一次哈希，Base58格式)，如果不设置，则启动过程中需要从控制台输入");
        newFileContent.add("local.parti.pwd=" + BftsmartConfig.PWD);
        newFileContent.add("");

        String outDir = BftsmartConfig.BFTSMART_DIR + "conf" + File.separator + index + File.separator;
        ClassPathResource outDirRes = new ClassPathResource(outDir);

        newFileContent.add("#账本初始化完成后生成的\"账本绑定配置文件\"的输出目录");
        newFileContent.add("ledger.binding.out=" + outDirRes.getFile().getAbsolutePath());
        newFileContent.add("");

        String dbDir = new File(localPath, "rocksdb" + index + ".db").getAbsolutePath();
        FileUtils.deleteFile(dbDir);

        newFileContent.add("#账本数据库的连接字符");
        newFileContent.add("ledger.db.uri=rocksdb://" + dbDir);
        newFileContent.add("");

        newFileContent.add("#账本数据库的连接口令");
        newFileContent.add("ledger.db.pwd=");
        newFileContent.add("");


        String consensusDir = BftsmartConfig.BFTSMART_DIR + "bftsmart-" + size + ".config";
        ClassPathResource consensusDirRes = new ClassPathResource(consensusDir);

        newFileContent.add("#共识配置文件路径");
        newFileContent.add("consensus.conf=" + consensusDirRes.getFile().getAbsolutePath());
        newFileContent.add("");

        newFileContent.add("#共识Providers配置");
        newFileContent.add("consensus.service-provider=com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider");
        return newFileContent;
    }
}