//package test.com.jd.blockchain.peer.service;
//
//import com.jd.blockchain.ledger.*;
//import com.jd.blockchain.ledger.service.LedgerService;
//import com.jd.blockchain.ledger.service.impl.LedgerServiceImpl;
//import com.jd.blockchain.peer.service.LedgerInitCordinator;
//import com.jd.blockchain.storage.service.KeyValueStorageService;
//import com.jd.blockchain.storage.service.impl.hashmap.HashMapStorageService;
//import my.utils.net.NetworkAddress;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class LedgerInitCordinatorTest {
//
//    private LedgerService ledgerService;
//    private NetworkAddress localpeer = new NetworkAddress("127.0.0.1", 9000);
//
//    @Before
//    public void setup() {
//        KeyValueStorageService storageService = new HashMapStorageService();
//        ledgerService = new LedgerServiceImpl(storageService);
//    }
//
//    @After
//    public void teardown() {
//        ledgerService = null;
//    }
//
//    @Test
//    public void testStart() {
//        BlockchainKeyPair genesisAccount = BlockchainKeyGenerator.getInstance().generate();
//        LedgerInitCordinator cordinator = new LedgerInitCordinator(ledgerService, localpeer, genesisAccount.getIdentity());
//        cordinator.start();
//
//        assertEquals(cordinator.getLedgerBuilder().getGenesisSeed(), cordinator.getLedgerDefinition().getGenesisKey());
//        assertEquals(cordinator.getLedgerDefinition().isConfigReady(), false);
//        assertEquals(cordinator.getLedgerDefinition().genesisAccountNum(), 1);
//        assertEquals(cordinator.getLedgerDefinition().getInitializingOperations().length, 0);
//    }
//
//    @Test
//    public void testJoinLedger() {
//        BlockchainKeyPair genesisAccount = BlockchainKeyGenerator.getInstance().generate();
//        LedgerInitCordinator cordinator = new LedgerInitCordinator(ledgerService, localpeer, genesisAccount.getIdentity());
//        cordinator.start();
//
//        NetworkAddress remotepeer1 = new NetworkAddress("127.0.0.1", 9001);
//        BlockchainKeyPair remoteAccount1 = BlockchainKeyGenerator.getInstance().generate();
//        BlockchainIdentity[] remoteIdentities1 = new BlockchainIdentity[1];
//        remoteIdentities1[0] = remoteAccount1.getIdentity();
//
//        NetworkAddress remotepeer2 = new NetworkAddress("127.0.0.1", 9002);
//        BlockchainKeyPair remoteAccount2 = BlockchainKeyGenerator.getInstance().generate();
//        BlockchainIdentity[] remoteIdentities2 = new BlockchainIdentity[1];
//        remoteIdentities2[0] = remoteAccount2.getIdentity();
//
//        cordinator.joinLedger(cordinator.getLedgerDefinition().getDefinitionId(), remotepeer1, remoteIdentities1, new BlockchainOperation[0]);
//        cordinator.joinLedger(cordinator.getLedgerDefinition().getDefinitionId(), remotepeer2, remoteIdentities2, new BlockchainOperation[0]);
//
//        assertEquals(cordinator.getLedgerDefinition().genesisAccountNum(), 3);
//        assertEquals(cordinator.getLedgerDefinition().getInitializingOperations().length, 0);
//    }
//
//    @Test
//    public void testPrepare() {
//        BlockchainKeyPair genesisAccount = BlockchainKeyGenerator.getInstance().generate();
//        LedgerInitCordinator cordinator = new LedgerInitCordinator(ledgerService, localpeer, genesisAccount.getIdentity());
//        cordinator.start();
//        TransactionContent content = cordinator.prepare(cordinator.getLedgerDefinition().getDefinitionId());
//
//        assertEquals(cordinator.getLedgerDefinition().isConfigReady(), true);
//        assertEquals(content.getOperations().length, 1);
//    }
//
//    @Test
//    public void testSign() {
//        BlockchainKeyPair genesisAccount = BlockchainKeyGenerator.getInstance().generate();
//        LedgerInitCordinator cordinator = new LedgerInitCordinator(ledgerService, localpeer, genesisAccount.getIdentity());
//        cordinator.start();
//
//        NetworkAddress remotepeer1 = new NetworkAddress("127.0.0.1", 9001);
//        BlockchainKeyPair remoteAccount1 = BlockchainKeyGenerator.getInstance().generate();
//        BlockchainIdentity[] remoteIdentities1 = new BlockchainIdentity[1];
//        remoteIdentities1[0] = remoteAccount1.getIdentity();
//
//        NetworkAddress remotepeer2 = new NetworkAddress("127.0.0.1", 9002);
//        BlockchainKeyPair remoteAccount2 = BlockchainKeyGenerator.getInstance().generate();
//        BlockchainIdentity[] remoteIdentities2 = new BlockchainIdentity[1];
//        remoteIdentities2[0] = remoteAccount2.getIdentity();
//
//        cordinator.joinLedger(cordinator.getLedgerDefinition().getDefinitionId(), remotepeer1, remoteIdentities1, new BlockchainOperation[0]);
//        cordinator.joinLedger(cordinator.getLedgerDefinition().getDefinitionId(), remotepeer2, remoteIdentities2, new BlockchainOperation[0]);
//
//        TransactionContent content = cordinator.prepare(cordinator.getLedgerDefinition().getDefinitionId());
//
//        DigitalSignature genesisSign = cordinator.sign(genesisAccount);
//        DigitalSignature remoteSign1 = cordinator.sign(remoteAccount1);
//        DigitalSignature remoteSign2 = cordinator.sign(remoteAccount2);
//
//        List<DigitalSignature> signatures = Arrays.asList(cordinator.getLedgerDefinition().genesisSignatures());
//
//        assertEquals(cordinator.getLedgerDefinition().genesisSignatures().length, 3);
//        assertEquals(signatures.contains(genesisSign), true);
//        assertEquals(signatures.contains(remoteSign1), true);
//        assertEquals(signatures.contains(remoteSign2), true);
//        assertEquals(cordinator.getLedgerDefinition().isSignatureReady(), true);
//    }
//
//    @Test
//    public void testConsistent() {
//        BlockchainKeyPair genesisAccount = BlockchainKeyGenerator.getInstance().generate();
//        LedgerInitCordinator cordinator = new LedgerInitCordinator(ledgerService, localpeer, genesisAccount.getIdentity());
//        cordinator.start();
//
//        NetworkAddress remotepeer1 = new NetworkAddress("127.0.0.1", 9001);
//        BlockchainKeyPair remoteAccount1 = BlockchainKeyGenerator.getInstance().generate();
//        BlockchainIdentity[] remoteIdentities1 = new BlockchainIdentity[1];
//        remoteIdentities1[0] = remoteAccount1.getIdentity();
//
//        NetworkAddress remotepeer2 = new NetworkAddress("127.0.0.1", 9002);
//        BlockchainKeyPair remoteAccount2 = BlockchainKeyGenerator.getInstance().generate();
//        BlockchainIdentity[] remoteIdentities2 = new BlockchainIdentity[1];
//        remoteIdentities2[0] = remoteAccount2.getIdentity();
//
//        cordinator.joinLedger(cordinator.getLedgerDefinition().getDefinitionId(), remotepeer1, remoteIdentities1, new BlockchainOperation[0]);
//        cordinator.joinLedger(cordinator.getLedgerDefinition().getDefinitionId(), remotepeer2, remoteIdentities2, new BlockchainOperation[0]);
//
//        TransactionContent content = cordinator.prepare(cordinator.getLedgerDefinition().getDefinitionId());
//
//        cordinator.sign(genesisAccount);
//        cordinator.sign(remoteAccount1);
//        cordinator.sign(remoteAccount2);
//        cordinator.preGenerateLedger();
//
//        cordinator.addConsensusLedgerHash(cordinator.getLedgerDefinition().getDefinitionId(), genesisAccount.getAddress(), cordinator.getLedgerBuilder().getLedger().getBlockHash());
//
//        cordinator.addConsensusLedgerHash(cordinator.getLedgerDefinition().getDefinitionId(), remoteAccount1.getAddress(), cordinator.getLedgerBuilder().getLedger().getBlockHash());
//
//        cordinator.addConsensusLedgerHash(cordinator.getLedgerDefinition().getDefinitionId(), remoteAccount2.getAddress(), cordinator.getLedgerBuilder().getLedger().getBlockHash());
//
//        assertEquals(cordinator.isLedgerConsistent(), true);
//    }
//}