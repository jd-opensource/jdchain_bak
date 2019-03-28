//package test.com.jd.blockchain.peer.web;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Matchers.any;
//
//import java.util.Arrays;
//import java.util.List;
//
//import com.jd.blockchain.ledger.service.impl.LedgerServiceImpl;
//import org.junit.*;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.util.Base64Utils;
//
//import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
//import com.jd.blockchain.ledger.AccountRegisterOperation;
//import com.jd.blockchain.ledger.AccountStateOperation;
//import com.jd.blockchain.ledger.AccountStateType;
//import com.jd.blockchain.ledger.BlockchainIdentity;
//import com.jd.blockchain.ledger.BlockchainKeyGenerator;
//import com.jd.blockchain.ledger.BlockchainKeyPair;
//import com.jd.blockchain.ledger.BlockchainOperation;
//import com.jd.blockchain.ledger.DigitalSignature;
//import com.jd.blockchain.ledger.Ledger;
//import com.jd.blockchain.ledger.TransactionContent;
//import com.jd.blockchain.ledger.data.BlockchainOperationFactory;
//import com.jd.blockchain.ledger.data.SignatureEncoding;
//import com.jd.blockchain.ledger.service.LedgerBuilder;
//import com.jd.blockchain.ledger.service.LedgerService;
//import com.jd.blockchain.peer.PeerSettings;
//import com.jd.blockchain.peer.service.LedgerDefinition;
//import com.jd.blockchain.peer.service.MessageBroadcaster;
//import com.jd.blockchain.peer.service.PeerKeyStorageService;
//import com.jd.blockchain.peer.web.JoinLedgerParameter;
//import com.jd.blockchain.peer.web.LedgerInitializationContext;
//import com.jd.blockchain.peer.web.LedgerInitializingController;
//import com.jd.blockchain.storage.service.impl.hashmap.HashMapStorageService;
//
//import my.utils.io.ByteArray;
//import my.utils.net.NetworkAddress;
//import my.utils.serialize.binary.BinarySerializeUtils;
//import my.utils.serialize.json.JSONSerializeUtils;
//
//@Ignore
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {ControllerTestConfiguration.class})
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class LedgerInitializingControllerTest {
//
//    @Autowired
//    private PeerKeyStorageService keystoreService;
//
//    @Autowired
//    private LedgerService ledgerService;
//
//    @Autowired
//    private PeerSettings peerSettings;
//
//    @Autowired
//    private MessageBroadcaster msgBroadcaster; // 用于向客户端进行消息通知；
//
//    private LedgerInitializingController controller;
//
//    @BeforeClass
//    public static void setUpBeforeClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownAfterClass() throws Exception {
//    }
//
//    private BlockchainKeyPair keyOfP1 = BlockchainKeyGenerator.getInstance().generate();
//
//    @Before
//    public void setup() {
//        ledgerService = new LedgerServiceImpl(new HashMapStorageService());
//        controller = new LedgerInitializingController(peerSettings, keystoreService, ledgerService, msgBroadcaster);
//
//        //when(ledgerService.newLedger()).thenReturn(new LedgerBuilderImpl(ByteArray.wrap("genesisKey".getBytes()), ledgerService));
//        //when(ledgerService.newLedger(ByteArray.wrap("genesisKey".getBytes()))).thenReturn(new LedgerBuilderImpl(ByteArray.wrap("genesisKey".getBytes()), ledgerService));
//    }
//
//    @After
//    public void after() {
//        controller = null;
//    }
//
//    @Test
//    public void testStartNewLedger() {
//        LedgerInitializationContext context = controller.startNewLedger("TestParticipantName", PeerKeyStorageServiceImpl.keyOfP1.getAddress());
//
//        assertEquals(context.getLocalAccount(), keystoreService.getBlockchainKey(PeerKeyStorageServiceImpl.keyOfP1.getAddress()).getIdentity());
//        assertEquals(context.getLocalConsensusAddress(), new NetworkAddress(peerSettings.getConsensus().getIp(), peerSettings.getConsensus().getPort()));
//        assertEquals(context.getLedgerDefinition().genesisAccountNum(), 1);
//        assertEquals(context.getLedgerDefinition().isConfigReady(), false);
//        assertEquals(context.getLedgerDefinition().getInitializingOperations().length, 2);
//
//        JSONSerializeUtils.disableCircularReferenceDetect();
//        JSONSerializeUtils.configStringSerializer(ByteArray.class);
//        System.out.println(JSONSerializeUtils.serializeToJSON(context));
//    }
//
//    @Test
//    public void testStartJoiningLedger() {
//        LedgerInitializationContext context = controller.startNewLedger("TestParticipantName", PeerKeyStorageServiceImpl.keyOfP1.getAddress());
//
//        JoinLedgerParameter parameter = new JoinLedgerParameter();
//        BlockchainKeyPair identity = BlockchainKeyGenerator.getInstance().generate();
//        parameter.setGenesisAccounts(new BlockchainIdentity[]{identity.getIdentity()});
//
//        AccountRegisterOperation regGenesisAccountOP = BlockchainOperationFactory.getInstance().register(identity.getIdentity(),
//                AccountStateType.MAP);
//        AccountStateOperation initGenesisAccountInfoOP = BlockchainOperationFactory.getInstance()
//                .updateState(identity.getAddress());
//        initGenesisAccountInfoOP.putString("NAME", "joinLedger");
//        initGenesisAccountInfoOP.putString("DESCRIPTION", "");
//
//        parameter.setOperations(new BlockchainOperation[]{regGenesisAccountOP, initGenesisAccountInfoOP});
//        parameter.setNetworkAddress(new NetworkAddress("127.0.0.1", 9001));
//
//        LedgerDefinition definition = decodeObject(controller.joinLedger(context.getLedgerDefinition().getDefinitionId(), encodeObject(parameter)), LedgerDefinition.class);
//
//        assertEquals(definition.genesisAccountNum(), 2);
//        assertEquals(definition.getInitializingOperations().length, 4);
//        assertEquals(definition.isConfigReady(), false);
//    }
//
//    @Test
//    public void testPrepare() {
//        LedgerInitializationContext context = controller.startNewLedger("TestParticipantName",PeerKeyStorageServiceImpl.keyOfP1.getAddress());
//
//        JoinLedgerParameter parameter = new JoinLedgerParameter();
//        BlockchainIdentity identity = keystoreService.generateNewKey("identity").getIdentity();
//
//        parameter.setGenesisAccounts(new BlockchainIdentity[]{identity});
//
//        AccountRegisterOperation regGenesisAccountOP = BlockchainOperationFactory.getInstance().register(identity, AccountStateType.MAP);
//        AccountStateOperation initGenesisAccountInfoOP = BlockchainOperationFactory.getInstance()
//                .updateState(identity.getAddress());
//        initGenesisAccountInfoOP.putString("NAME", "joinLedger");
//        initGenesisAccountInfoOP.putString("DESCRIPTION", "");
//
//        parameter.setOperations(new BlockchainOperation[]{regGenesisAccountOP, initGenesisAccountInfoOP});
//        parameter.setNetworkAddress(new NetworkAddress("127.0.0.1", 9001));
//
//        controller.joinLedger(context.getLedgerDefinition().getDefinitionId(), encodeObject(parameter));
//        context = controller.prepareAndSign();
//
//        assertEquals(context.getLedgerDefinition().isConfigReady(), true);
//    }
//
//    @Test
//    public void testAttachSignature() {
//        LedgerInitializationContext context = controller.startNewLedger("TestParticipantName", PeerKeyStorageServiceImpl.keyOfP1.getAddress());
//        JoinLedgerParameter parameter = new JoinLedgerParameter();
//        BlockchainIdentity identity = keystoreService.generateNewKey("identity").getIdentity();
//
//        parameter.setGenesisAccounts(new BlockchainIdentity[]{identity});
//        parameter.setOperations(new BlockchainOperation[]{});
//        parameter.setNetworkAddress(new NetworkAddress("127.0.0.1", 9001));
//
//        controller.joinLedger(context.getLedgerDefinition().getDefinitionId(), encodeObject(parameter));
//        context = controller.prepareAndSign();
//
//        LedgerBuilder ledgerBuilder = ledgerService.newLedger(context.getLedgerDefinition().getGenesisKey());
//        ledgerBuilder.addLedgerKeys(context.getLedgerDefinition().genesisIdentites());
//        ledgerBuilder.addInitializeOperations(context.getLedgerDefinition().getInitializingOperations());
//
//        TransactionContent txContent = ledgerBuilder.prepare();
//        context.setTransaction(txContent);
//        
//        byte[] txContentBytes = BinaryEncodingUtils.encode(txContent, TransactionContent.class);
//        DigitalSignature signature = keystoreService.sign(txContentBytes, identity.getAddress());
//        ledgerBuilder.attachSignature(signature);
//
//        String base64Signature = SignatureEncoding.encodeToBase64(signature);
//        controller.attachSignature(context.getLedgerDefinition().getDefinitionId(), base64Signature);
//
//        List<DigitalSignature> signatures = Arrays.asList(context.getLedgerDefinition().genesisSignatures());
//
//        assertEquals(context.getLedgerDefinition().genesisSignatures().length, 2);
//        assertEquals(signatures.contains(signature), true);
//        assertEquals(context.getLedgerDefinition().isSignatureReady(), true);
//    }
//
//    @Test
//    public void testconsensusLedger() {
//        LedgerInitializationContext context = controller.startNewLedger("TestParticipantName", PeerKeyStorageServiceImpl.keyOfP1.getAddress());
//
//        BlockchainIdentity identity = PeerKeyStorageServiceImpl.keyOfP2.getIdentity();
//        JoinLedgerParameter parameter = new JoinLedgerParameter();
//        parameter.setGenesisAccounts(new BlockchainIdentity[]{identity});
//        parameter.setOperations(new BlockchainOperation[]{});
//        parameter.setNetworkAddress(new NetworkAddress("127.0.0.1", 9001));
//
//        controller.joinLedger(context.getLedgerDefinition().getDefinitionId(), encodeObject(parameter));
//        context = controller.prepareAndSign();
//
//        // other peer
//        LedgerBuilder builder = ledgerService.newLedger(context.getLedgerDefinition().getGenesisKey());
//        builder.addLedgerKeys(context.getLedgerDefinition().genesisIdentites());
//        builder.addInitializeOperations(context.getLedgerDefinition().getInitializingOperations());
//
//        builder.prepare();
//        String base64Signature = SignatureEncoding.encodeToBase64(builder.sign(PeerKeyStorageServiceImpl.keyOfP2));
//        controller.attachSignature(context.getLedgerDefinition().getDefinitionId(), base64Signature);
//
//        // 附加全部的签名列表到本地，生成创世区块，建立账本，并提交账本hash到协调节点进行共识；
//        context = controller.getLedgerInitContext();
//        for (DigitalSignature signature1 : context.getLedgerDefinition().genesisSignatures()) {
//            builder.attachSignature(signature1);
//        }
//
//        builder.preCommit();
//        Ledger ledger = builder.getLedger();
//
//        // 向协调节点发送账本hash进行共识；
//        controller.consensusLedger(context.getLedgerDefinition().getDefinitionId(), identity.getAddress(), ledger.getLedgerHash().toBase64());
//
//        assertEquals(true, controller.getLedgerInitContext().isConsistent());
//    }
//
//    private String encodeObject(Object obj) {
//        byte[] bts = BinarySerializeUtils.serialize(obj);
//        return Base64Utils.encodeToString(bts);
//    }
//
//    @SuppressWarnings("unchecked")
//    private <T> T decodeObject(String base64Str, Class<T> clazz) {
//        byte[] bts = Base64Utils.decodeFromString(base64Str);
//        // if (BytesReader.class.isAssignableFrom(clazz)) {
//        // BytesReader instance = (BytesReader) BeanUtils.instantiate(clazz);
//        // try {
//        // instance.resolvFrom(new ByteArrayInputStream(bts));
//        // } catch (IOException e) {
//        // throw new IORuntimeException(e.getMessage(), e);
//        // }
//        // return (T) instance;
//        // }
//        return (T) BinarySerializeUtils.deserialize(bts);
//    }
//}
