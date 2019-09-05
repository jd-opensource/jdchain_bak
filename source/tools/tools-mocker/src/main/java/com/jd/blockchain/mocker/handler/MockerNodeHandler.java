package com.jd.blockchain.mocker.handler;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.gateway.GatewayConfigProperties;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.mocker.config.MockerConstant;
import com.jd.blockchain.mocker.config.PresetAnswerPrompter;
import com.jd.blockchain.mocker.node.GatewayNodeRunner;
import com.jd.blockchain.mocker.node.NodeWebContext;
import com.jd.blockchain.mocker.node.PeerNodeRunner;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.net.NetworkAddress;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jd.blockchain.mocker.config.MockerConstant.*;

public class MockerNodeHandler {

    private static final String LEDGER_BINDING_FORMAT = "binding.%s.";

    private static final String PARTI_FORMAT = LEDGER_BINDING_FORMAT + "parti.";

    private static final String BINDING_ID_FORMAT = PARTI_FORMAT + "id";

    private static final String BINDING_PK_PATH_FORMAT = PARTI_FORMAT + "pk-path";

    private static final String BINDING_PK_FORMAT = PARTI_FORMAT + "pk";

    private static final String BINDING_PWD_FORMAT = PARTI_FORMAT + "pwd";

    private static final String BINDING_ADDRESS_FORMAT = PARTI_FORMAT + "address";

    private static final String DB_FORMAT = LEDGER_BINDING_FORMAT + "db.";

    private static final String BINDING_DB_URI_FORMAT = DB_FORMAT + "uri";

    private static final String BINDING_DB_PWD_FORMAT = DB_FORMAT + "pwd";

    private PeerNodeRunner[] peerNodes;

    private GatewayNodeRunner gatewayNodeRunner;

    public void start(int nodeSize) throws Exception {

        HashDigest ledgerHash = ledgerInit(nodeSize);

        // 启动Peer节点
        peerNodes = peerNodeStart(nodeSize, ledgerHash);

        // 启动网关节点
        gatewayNodeRunner = gatewayNodeStart(peerNodes[0].getServiceAddress());
    }

    public LedgerInitProperties initLedgerProperties(int nodeSize) throws Exception {

        File ledgerInitFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX +
                String.format(MockerConstant.LEDGER_INIT_FORMATTER, nodeSize));

        final LedgerInitProperties ledgerInitProperties = LedgerInitProperties.resolve(new FileInputStream(ledgerInitFile));

        return ledgerInitProperties;
    }

    private HashDigest ledgerInit(int nodeSize) throws Exception {

        System.out.println("----------- is daemon=" + Thread.currentThread().isDaemon());

        Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();

        LedgerInitProperties initSetting = initLedgerProperties(nodeSize);

        Set<HashDigest> hashDigestSet = new HashSet<>();

        CountDownLatch quitLatch = new CountDownLatch(nodeSize);

        ExecutorService peerThreads = Executors.newFixedThreadPool(nodeSize);

        for (int i = 0; i < nodeSize; i++) {
            final int nodeIndex = i;
            peerThreads.execute(() -> {
                // 启动服务器；
                NetworkAddress initAddr = initSetting.getConsensusParticipant(nodeIndex).getInitializerAddress();
                NodeWebContext node = new NodeWebContext(nodeIndex, initAddr);
                PrivKey privkey = KeyGenUtils.decodePrivKeyWithRawPassword(PRIVATE_KEYS[nodeIndex], PASSWORD);
                DBConnectionConfig dbConn = new DBConnectionConfig();
                dbConn.setConnectionUri(MockerConstant.DB_MEMS[nodeIndex]);
                ThreadInvoker.AsyncCallback<HashDigest> nodeCallback = node.startInit(privkey, initSetting, dbConn, consolePrompter,
                        quitLatch);
                hashDigestSet.add(nodeCallback.waitReturn());
            });
        }

        quitLatch.await();

        if (hashDigestSet.size() != 1) {
            throw new IllegalStateException(String.format("%s Node Ledger Init Fail !!!", nodeSize));
        }
        return hashDigestSet.toArray(new HashDigest[hashDigestSet.size()])[0];
    }


    public PeerNodeRunner[] peerNodeStart(int nodeSize, HashDigest ledgerHash) {

        int portStart = PEER_PORT_START;

        List<ThreadInvoker.AsyncCallback<Object>> threadInvokers = new ArrayList<>();

        final PeerNodeRunner[] peerNodeRunners = new PeerNodeRunner[nodeSize];

        for (int i = 0; i < nodeSize; i++) {
            NetworkAddress peerSrvAddr = new NetworkAddress(LOCAL_ADDRESS, portStart);
            LedgerBindingConfig bindingConfig = loadBindingConfig(i, ledgerHash);
            PeerNodeRunner peerNodeRunner = new PeerNodeRunner(peerSrvAddr, bindingConfig);
            peerNodeRunners[i] = peerNodeRunner;
            portStart += 10;
            threadInvokers.add(peerNodeRunner.start());
        }

        // 等待结果
        for (ThreadInvoker.AsyncCallback<Object> threadInvoker : threadInvokers) {
            threadInvoker.waitReturn();
        }

        return peerNodeRunners;
    }

    public GatewayNodeRunner gatewayNodeStart(NetworkAddress peerAddress) {
        GatewayConfigProperties.KeyPairConfig gwKeyPair = new GatewayConfigProperties.KeyPairConfig();
        gwKeyPair.setPubKeyValue(PUBLIC_KEYS[0]);
        gwKeyPair.setPrivKeyValue(PRIVATE_KEYS[0]);
        gwKeyPair.setPrivKeyPassword(PASSWORD_ENCODE);
        GatewayNodeRunner gateway = new GatewayNodeRunner(LOCAL_ADDRESS, GATEWAY_PORT, gwKeyPair,
                peerAddress, CONSENSUS_PROVIDERS,null);

        ThreadInvoker.AsyncCallback<Object> gwStarting = gateway.start();

        gwStarting.waitReturn();

        return gateway;
    }

    public void stop() {
        if (peerNodes != null) {
            for (PeerNodeRunner peerNodeRunner : peerNodes) {
                peerNodeRunner.stop();
            }
        }
        if (gatewayNodeRunner != null) {
            gatewayNodeRunner.stop();
        }
    }

    private LedgerBindingConfig loadBindingConfig(int nodeIndex, HashDigest ledgerHash) {

        Properties properties = new Properties();

        String ledgerHashBase58 = ledgerHash.toBase58();

        properties.put("ledger.bindings", ledgerHashBase58);

        properties.put(String.format(BINDING_ID_FORMAT, ledgerHashBase58), nodeIndex);

        properties.put(String.format(BINDING_PK_PATH_FORMAT, ledgerHashBase58), "");

        properties.put(String.format(BINDING_PK_FORMAT, ledgerHashBase58), PUBLIC_KEYS[nodeIndex]);

        properties.put(String.format(BINDING_PWD_FORMAT, ledgerHashBase58), PASSWORD_ENCODE);

        properties.put(String.format(BINDING_ADDRESS_FORMAT, ledgerHashBase58), ADDRESS_ARRAY[nodeIndex]);

        properties.put(String.format(BINDING_DB_URI_FORMAT, ledgerHashBase58), DB_MEMS[nodeIndex]);

        properties.put(String.format(BINDING_DB_PWD_FORMAT, ledgerHashBase58), "");

        return LedgerBindingConfig.resolve(properties);
    }
}
