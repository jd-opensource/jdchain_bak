package test.com.jd.blockchain.intgr;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.gateway.GatewayConfigProperties;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import org.junit.Test;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeTest;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeWeb4Nodes;

import static test.com.jd.blockchain.intgr.IntegrationBase.*;

public class IntegrationBlockFullRollback {

    private static final String DB_TYPE_MEM = "mem";

    @Test
    public void test4Memory() {
        test(LedgerInitConsensusConfig.bftsmartProvider, DB_TYPE_MEM, LedgerInitConsensusConfig.memConnectionStrings);
    }

    public void test(String[] providers, String dbType, String[] dbConnections) {

        // 内存账本初始化
        HashDigest ledgerHash = initLedger(dbConnections);

        // 启动Peer节点
        PeerTestRunner[] peerNodes = peerNodeStart(ledgerHash, dbType);

        DbConnectionFactory dbConnectionFactory0 = peerNodes[0].getDBConnectionFactory();
        DbConnectionFactory dbConnectionFactory1 = peerNodes[1].getDBConnectionFactory();
        DbConnectionFactory dbConnectionFactory2 = peerNodes[2].getDBConnectionFactory();
        DbConnectionFactory dbConnectionFactory3 = peerNodes[3].getDBConnectionFactory();

        String encodedBase58Pwd = KeyGenUtils.encodePasswordAsBase58(LedgerInitializeTest.PASSWORD);

        GatewayConfigProperties.KeyPairConfig gwkey0 = new GatewayConfigProperties.KeyPairConfig();
        gwkey0.setPubKeyValue(IntegrationBase.PUB_KEYS[0]);
        gwkey0.setPrivKeyValue(IntegrationBase.PRIV_KEYS[0]);
        gwkey0.setPrivKeyPassword(encodedBase58Pwd);
        GatewayTestRunner gateway = new GatewayTestRunner("127.0.0.1", 11000, gwkey0,
                peerNodes[0].getServiceAddress(), providers,null);

        ThreadInvoker.AsyncCallback<Object> gwStarting = gateway.start();

        gwStarting.waitReturn();

        // 执行测试用例之前，校验每个节点的一致性；
        LedgerQuery[] ledgers = buildLedgers(new LedgerBindingConfig[]{
                        peerNodes[0].getLedgerBindingConfig(),
                        peerNodes[1].getLedgerBindingConfig(),
                        peerNodes[2].getLedgerBindingConfig(),
                        peerNodes[3].getLedgerBindingConfig(),
                },
                new DbConnectionFactory[]{
                        dbConnectionFactory0,
                        dbConnectionFactory1,
                        dbConnectionFactory2,
                        dbConnectionFactory3});

        IntegrationBase.testConsistencyAmongNodes(ledgers);

        LedgerQuery ledgerRepository = ledgers[0];

        GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway.getServiceAddress());

        PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(IntegrationBase.PRIV_KEYS[0], IntegrationBase.PASSWORD);

        PubKey pubKey0 = KeyGenUtils.decodePubKey(IntegrationBase.PUB_KEYS[0]);

        AsymmetricKeypair adminKey = new AsymmetricKeypair(pubKey0, privkey0);

        BlockchainService blockchainService = gwsrvFact.getBlockchainService();

        //Block commit will be fail, due to storage error
        MemoryKVStorage.isBlockFullRollbackTest = true;

        IntegrationBase.KeyPairResponse userResponse = IntegrationBase.testSDK_BlockFullRollBack(adminKey, ledgerHash, blockchainService);

        //Block commit will be normal
        MemoryKVStorage.isBlockFullRollbackTest = false;

        IntegrationBase.KeyPairResponse userResponse1 = IntegrationBase.testSDK_RegisterUser(adminKey, ledgerHash, blockchainService);

        try {
            System.out.println("----------------- Init Completed -----------------");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IntegrationBase.testConsistencyAmongNodes(ledgers);
    }
    private HashDigest initLedger(String[] dbConnections) {
        LedgerInitializeWeb4Nodes ledgerInit = new LedgerInitializeWeb4Nodes();
        HashDigest ledgerHash = ledgerInit.testInitWith4Nodes(LedgerInitConsensusConfig.bftsmartConfig, dbConnections);
        System.out.printf("LedgerHash = %s \r\n", ledgerHash.toBase58());
        return ledgerHash;
    }
}
