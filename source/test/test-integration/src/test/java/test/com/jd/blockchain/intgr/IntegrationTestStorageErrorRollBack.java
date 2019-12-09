//package test.com.jd.blockchain.intgr;
//
//import com.jd.blockchain.consensus.ConsensusProviders;
//import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
//import com.jd.blockchain.crypto.*;
//import com.jd.blockchain.gateway.GatewayConfigProperties;
//import com.jd.blockchain.ledger.BlockRollbackException;
//import com.jd.blockchain.ledger.BlockchainKeypair;
//import com.jd.blockchain.ledger.core.LedgerManager;
//import com.jd.blockchain.ledger.core.LedgerQuery;
//import com.jd.blockchain.sdk.BlockchainService;
//import com.jd.blockchain.sdk.client.GatewayServiceFactory;
//import com.jd.blockchain.storage.service.DbConnection;
//import com.jd.blockchain.storage.service.DbConnectionFactory;
//import com.jd.blockchain.storage.service.ExPolicyKVStorage;
//import com.jd.blockchain.storage.service.KVStorageService;
//import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
//import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
//import com.jd.blockchain.utils.Bytes;
//import com.jd.blockchain.utils.concurrent.ThreadInvoker;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import test.com.jd.blockchain.intgr.initializer.LedgerInitializeTest;
//import test.com.jd.blockchain.intgr.initializer.LedgerInitializeWeb4Nodes;
//
//import java.lang.reflect.Method;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyLong;
//import static org.mockito.Mockito.doCallRealMethod;
//import static org.mockito.Mockito.doThrow;
//import static test.com.jd.blockchain.intgr.IntegrationBase.*;
//
//public class IntegrationTestStorageErrorRollBack {
//
//    private static final boolean isRegisterUser = true;
//
//    private static final boolean isRegisterDataAccount = false;
//
//    private static final boolean isWriteKv = false;
//
//    private static final String DB_TYPE_MEM = "mem";
//
//    public static final  String  BFTSMART_PROVIDER = "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider";
//
//    public boolean isRollBack = false;
//
//    @Test
//    public void test4Memory() {
//        test(LedgerInitConsensusConfig.bftsmartProvider, DB_TYPE_MEM, LedgerInitConsensusConfig.memConnectionStrings);
//    }
//
//    public void test(String[] providers, String dbType, String[] dbConnections) {
//
//
//        final ExecutorService sendReqExecutors = Executors.newFixedThreadPool(20);
//
//        // 内存账本初始化
//        HashDigest ledgerHash = initLedger(dbConnections);
//
//        System.out.println("---------------init OK-------------------");
//
//        // 启动Peer节点
//        PeerTestRunner[] peerNodes = peerNodeStart(ledgerHash, dbType);
//
//        System.out.println("---------------peer start OK-------------------");
//        String encodedBase58Pwd = KeyGenUtils.encodePasswordAsBase58(LedgerInitializeTest.PASSWORD);
//
//        GatewayConfigProperties.KeyPairConfig gwkey0 = new GatewayConfigProperties.KeyPairConfig();
//        gwkey0.setPubKeyValue(IntegrationBase.PUB_KEYS[0]);
//        gwkey0.setPrivKeyValue(IntegrationBase.PRIV_KEYS[0]);
//        gwkey0.setPrivKeyPassword(encodedBase58Pwd);
//        GatewayTestRunner gateway = new GatewayTestRunner("127.0.0.1", 11000, gwkey0,
//                peerNodes[0].getServiceAddress(), providers,null);
//
//        ThreadInvoker.AsyncCallback<Object> gwStarting = gateway.start();
//
//        gwStarting.waitReturn();
//
//        LedgerQuery[] ledgers = new LedgerQuery[peerNodes.length];
//        LedgerManager[] ledgerManagers = new LedgerManager[peerNodes.length];
//        LedgerBindingConfig[] ledgerBindingConfigs = new LedgerBindingConfig[peerNodes.length];
//        DbConnection[] connections = new DbConnection[peerNodes.length];
//        MemoryKVStorage[] storageMocks = new MemoryKVStorage[peerNodes.length];
//        for (int i = 0; i < peerNodes.length; i++) {
//            ledgerManagers[i] = new LedgerManager();
//            ledgerBindingConfigs[i] = peerNodes[i].getLedgerBindingConfig();
//            connections[i] = peerNodes[i].getDBConnectionFactory().connect(ledgerBindingConfigs[i].getLedger(ledgerHash).getDbConnection().getUri());
//            System.out.printf("StorageService[%s] -> %s \r\n", i, connections[i].getStorageService());
//            storageMocks[i] = Mockito.spy((MemoryKVStorage)(connections[i].getStorageService()));
//            ledgers[i] = ledgerManagers[i].register(ledgerHash, storageMocks[i]);
//        }
//
//        final MemoryKVStorage STORAGE_Mock = Mockito.mock(MemoryKVStorage.class);
//
//        Answer<String> answers = new Answer() {
//
//            @Override
//            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
//
//                Method method = invocationOnMock.getMethod();
//                if (method.getName().equalsIgnoreCase("set")) {
//                    Object arg2Obj = invocationOnMock.getArguments()[2];
//                    if (isRollBack) {
//                        if (arg2Obj instanceof ExPolicyKVStorage.ExPolicy) {
//                            return false;
//                        } else {
//                            return -1;
//                        }
//                    } else {
//                        if (arg2Obj instanceof ExPolicyKVStorage.ExPolicy) {
//                            return STORAG.set((Bytes) (invocationOnMock.getArguments()[0]), (byte[]) (invocationOnMock.getArguments()[1]), (ExPolicyKVStorage.ExPolicy) (arg2Obj));
//                        } else {
//                            return STORAGE.set((Bytes) (invocationOnMock.getArguments()[0]), (byte[]) (invocationOnMock.getArguments()[1]), (long) (arg2Obj));
//                        }
//                    }
//                } else if ((method.getName().equalsIgnoreCase("get")) && (method.getParameterCount() == 1)) {
//                    return STORAGE.get((Bytes) (invocationOnMock.getArguments()[0]));
//                } else if ((method.getName().equalsIgnoreCase("get")) && (method.getParameterCount() == 2)) {
//                    return STORAGE.get((Bytes) (invocationOnMock.getArguments()[0]), (long) (invocationOnMock.getArguments()[1]));
//                } else if (method.getName().equalsIgnoreCase("getVersion")) {
//                    return STORAGE.getVersion((Bytes) (invocationOnMock.getArguments()[0]));
//                } else if (method.getName().equalsIgnoreCase("getEntry")) {
//                    return STORAGE.get((Bytes) (invocationOnMock.getArguments()[0]), (long) (invocationOnMock.getArguments()[1]));
//                } else if (method.getName().equalsIgnoreCase("exist")) {
//                    return STORAGE.get((Bytes) (invocationOnMock.getArguments()[0]));
//                } else if (method.getName().equalsIgnoreCase("keySet")) {
//                    return STORAGE.keySet();
//                } else if (method.getName().equalsIgnoreCase("getStorageKeySet")) {
//                    return STORAGE.getStorageKeySet();
//                } else if (method.getName().equalsIgnoreCase("getValue")) {
//                    return STORAGE.getValue((Bytes) (invocationOnMock.getArguments()[0]));
//                } else if (method.getName().equalsIgnoreCase("getStorageCount")) {
//                    return STORAGE.getStorageCount();
//                } else if (method.getName().equalsIgnoreCase("getExPolicyKVStorage")) {
//                    return STORAGE.getExPolicyKVStorage();
//                } else if (method.getName().equalsIgnoreCase("getVersioningKVStorage")) {
//                    return STORAGE.getVersioningKVStorage();
//                }
//
//                return null;
//            }
//        };
//
//        when(STORAGE_Mock.set(any(), any(), anyLong())).thenAnswer(answers);
//        when(STORAGE_Mock.set(any(), any(), any(ExPolicyKVStorage.ExPolicy.class))).thenAnswer(answers);
//        when(STORAGE_Mock.get(any())).thenAnswer(answers);
//        when(STORAGE_Mock.get(any(), anyLong())).thenAnswer(answers);
//        when(STORAGE_Mock.getVersion(any())).thenAnswer(answers);
//        when(STORAGE_Mock.getEntry(any(), anyLong())).thenAnswer(answers);
//        when(STORAGE_Mock.exist(any())).thenAnswer(answers);
//        when(STORAGE_Mock.keySet()).thenAnswer(answers);
//        when(STORAGE_Mock.getStorageKeySet()).thenAnswer(answers);
//        when(STORAGE_Mock.getValue(any())).thenAnswer(answers);
//        when(STORAGE_Mock.getStorageCount()).thenAnswer(answers);
//        when(STORAGE_Mock.getExPolicyKVStorage()).thenAnswer(answers);
//        when(STORAGE_Mock.getVersioningKVStorage()).thenAnswer(answers);
//
//
//        IntegrationBase.testConsistencyAmongNodes(ledgers);
//
//        LedgerQuery ledgerRepository = ledgers[0];
//
//        GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway.getServiceAddress());
//
//        PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(IntegrationBase.PRIV_KEYS[0], IntegrationBase.PASSWORD);
//
//        PubKey pubKey0 = KeyGenUtils.decodePubKey(IntegrationBase.PUB_KEYS[0]);
//
//        AsymmetricKeypair adminKey = new AsymmetricKeypair(pubKey0, privkey0);
//
//        BlockchainService blockchainService = gwsrvFact.getBlockchainService();
//
//        int size = 15;
//        CountDownLatch countDownLatch = new CountDownLatch(size);
//        if (isRegisterUser) {
//            for (int i = 0; i < size; i++) {
//                sendReqExecutors.execute(() -> {
//
//                    System.out.printf(" sdk execute time = %s threadId = %s \r\n", System.currentTimeMillis(), Thread.currentThread().getId());
//                    KeyPairResponse userResponse = IntegrationBase.testSDK_RegisterUser(adminKey, ledgerHash, blockchainService);
//
////                    validKeyPair(userResponse, ledgerRepository, IntegrationBase.KeyPairType.USER);
//                    countDownLatch.countDown();
//                });
//            }
//        }
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        for (int i = 0; i < peerNodes.length; i++) {
//            doCallRealMethod().when(storageMocks[i]).set(any(), any(), anyLong());
//        }
//
//        if (isRegisterDataAccount) {
//            KeyPairResponse dataAccountResponse = IntegrationBase.testSDK_RegisterDataAccount(adminKey, ledgerHash, blockchainService);
//
//            validKeyPair(dataAccountResponse, ledgerRepository, KeyPairType.DATAACCOUNT);
//
//            if (isWriteKv) {
//
//                for (int m = 0; m < 13; m++) {
//                    BlockchainKeypair da = dataAccountResponse.keyPair;
//                    KvResponse kvResponse = IntegrationBase.testSDK_InsertData(adminKey, ledgerHash, blockchainService, da.getAddress());
//                    validKvWrite(kvResponse, ledgerRepository, blockchainService);
//                }
//            }
//        }
//
//        try {
//            System.out.println("----------------- Init Completed -----------------");
//            Thread.sleep(Integer.MAX_VALUE);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        IntegrationBase.testConsistencyAmongNodes(ledgers);
//    }
//    private HashDigest initLedger(String[] dbConnections) {
//        LedgerInitializeWeb4Nodes ledgerInit = new LedgerInitializeWeb4Nodes();
//        HashDigest ledgerHash = ledgerInit.testInitWith4Nodes(LedgerInitConsensusConfig.bftsmartConfig, dbConnections);
//        System.out.printf("LedgerHash = %s \r\n", ledgerHash.toBase58());
//        return ledgerHash;
//    }
//}
