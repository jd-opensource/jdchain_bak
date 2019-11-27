package test.com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.utils.Bytes;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class BlockFullRollBackTest {

    static {
        DataContractRegistry.register(TransactionContent.class);
        DataContractRegistry.register(TransactionContentBody.class);
        DataContractRegistry.register(TransactionRequest.class);
        DataContractRegistry.register(NodeRequest.class);
        DataContractRegistry.register(EndpointRequest.class);
        DataContractRegistry.register(TransactionResponse.class);
        DataContractRegistry.register(UserRegisterOperation.class);
        DataContractRegistry.register(DataAccountRegisterOperation.class);
    }

    private static final String LEDGER_KEY_PREFIX = "LDG://";

    private HashDigest ledgerHash = null;

    private boolean isRollBack = false;

    private BlockchainKeypair parti0 = BlockchainKeyGenerator.getInstance().generate();
    private BlockchainKeypair parti1 = BlockchainKeyGenerator.getInstance().generate();
    private BlockchainKeypair parti2 = BlockchainKeyGenerator.getInstance().generate();
    private BlockchainKeypair parti3 = BlockchainKeyGenerator.getInstance().generate();

    private BlockchainKeypair[] participants = { parti0, parti1, parti2, parti3 };


    @Test
    public void testBlockFullkRollBack() {

        final MemoryKVStorage STORAGE = new MemoryKVStorage();

        final MemoryKVStorage STORAGE_Mock = Mockito.mock(MemoryKVStorage.class);


        Answer<String> answers = new Answer() {

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {

                Method method = invocationOnMock.getMethod();
                if (method.getName().equalsIgnoreCase("set")) {
                    Object arg2Obj = invocationOnMock.getArguments()[2];
                    if (isRollBack) {
                        if (arg2Obj instanceof ExPolicyKVStorage.ExPolicy) {
                            return false;
                        } else {
                            return -1;
                        }
                    } else {
                        if (arg2Obj instanceof ExPolicyKVStorage.ExPolicy) {
                            return STORAGE.set((Bytes) (invocationOnMock.getArguments()[0]), (byte[])(invocationOnMock.getArguments()[1]), (ExPolicyKVStorage.ExPolicy)(arg2Obj));
                        } else {
                            return STORAGE.set((Bytes) (invocationOnMock.getArguments()[0]), (byte[])(invocationOnMock.getArguments()[1]), (long)(arg2Obj));
                        }
                    }
                } else if ((method.getName().equalsIgnoreCase("get")) && (method.getParameterCount() == 1)) {
                    return STORAGE.get((Bytes)(invocationOnMock.getArguments()[0]));
                } else if ((method.getName().equalsIgnoreCase("get")) && (method.getParameterCount() == 2)) {
                    return STORAGE.get((Bytes)(invocationOnMock.getArguments()[0]), (long)(invocationOnMock.getArguments()[1]));
                } else if (method.getName().equalsIgnoreCase("getVersion")) {
                    return STORAGE.getVersion((Bytes)(invocationOnMock.getArguments()[0]));
                } else if (method.getName().equalsIgnoreCase("getEntry")) {
                    return STORAGE.get((Bytes)(invocationOnMock.getArguments()[0]), (long)(invocationOnMock.getArguments()[1]));
                } else if (method.getName().equalsIgnoreCase("exist")) {
                    return STORAGE.get((Bytes)(invocationOnMock.getArguments()[0]));
                } else if (method.getName().equalsIgnoreCase("keySet")) {
                    return STORAGE.keySet();
                } else if (method.getName().equalsIgnoreCase("getStorageKeySet")) {
                    return STORAGE.getStorageKeySet();
                } else if (method.getName().equalsIgnoreCase("getValue")) {
                    return STORAGE.getValue((Bytes)(invocationOnMock.getArguments()[0]));
                } else if (method.getName().equalsIgnoreCase("getStorageCount")) {
                    return STORAGE.getStorageCount();
                } else if (method.getName().equalsIgnoreCase("getExPolicyKVStorage")) {
                    return STORAGE.getExPolicyKVStorage();
                } else if (method.getName().equalsIgnoreCase("getVersioningKVStorage")) {
                    return STORAGE.getVersioningKVStorage();
                }

                return null;
            }
        };

        when(STORAGE_Mock.set(any(), any(), anyLong())).thenAnswer(answers);
        when(STORAGE_Mock.set(any(), any(), any(ExPolicyKVStorage.ExPolicy.class))).thenAnswer(answers);
        when(STORAGE_Mock.get(any())).thenAnswer(answers);
        when(STORAGE_Mock.get(any(), anyLong())).thenAnswer(answers);
        when(STORAGE_Mock.getVersion(any())).thenAnswer(answers);
        when(STORAGE_Mock.getEntry(any(), anyLong())).thenAnswer(answers);
        when(STORAGE_Mock.exist(any())).thenAnswer(answers);
        when(STORAGE_Mock.keySet()).thenAnswer(answers);
        when(STORAGE_Mock.getStorageKeySet()).thenAnswer(answers);
        when(STORAGE_Mock.getValue(any())).thenAnswer(answers);
        when(STORAGE_Mock.getStorageCount()).thenAnswer(answers);
        when(STORAGE_Mock.getExPolicyKVStorage()).thenAnswer(answers);
        when(STORAGE_Mock.getVersioningKVStorage()).thenAnswer(answers);


        // 初始化账本到指定的存储库；
        ledgerHash = initLedger(STORAGE_Mock, parti0, parti1, parti2, parti3);

        System.out.println("---------- Ledger init OK !!! ----------");

        // 加载账本；
        LedgerManager ledgerManager = new LedgerManager();

        KVStorageService kvStorageService = new KVStorageService() {
            @Override
            public ExPolicyKVStorage getExPolicyKVStorage() {
                return STORAGE_Mock;
            }

            @Override
            public VersioningKVStorage getVersioningKVStorage() {
                return STORAGE_Mock;
            }
        };

        LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, kvStorageService);

        // 构造存储错误，并产生区块回滚
        isRollBack = true;
        LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();

        OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
        LedgerSecurityManager securityManager = getSecurityManager();
        TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(securityManager, newBlockEditor,
                ledgerRepo, opReg);

        // 注册新用户；
        BlockchainKeypair userKeypair = BlockchainKeyGenerator.getInstance().generate();
        TransactionRequest transactionRequest = LedgerTestUtils.createTxRequest_UserReg(userKeypair, ledgerHash,
                parti0, parti0);
        TransactionResponse txResp = txbatchProcessor.schedule(transactionRequest);

        LedgerBlock newBlock = newBlockEditor.prepare();
        try {
            newBlockEditor.commit();
        } catch (BlockRollbackException e) {
            newBlockEditor.cancel();
        }

        // 验证正确性；
        ledgerManager = new LedgerManager();
        ledgerRepo = ledgerManager.register(ledgerHash, STORAGE_Mock);
        LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
        assertEquals(ledgerRepo.getBlockHash(0), latestBlock.getHash());
        assertEquals(0, latestBlock.getHeight());

        LedgerDataQuery ledgerDS = ledgerRepo.getLedgerData(latestBlock);
        boolean existUser = ledgerDS.getUserAccountSet().contains(userKeypair.getAddress());

        assertFalse(existUser);

        //区块正常提交
        isRollBack = false;
        // 生成新区块；
        LedgerEditor newBlockEditor1 = ledgerRepo.createNextBlock();

        OperationHandleRegisteration opReg1 = new DefaultOperationHandleRegisteration();
        LedgerSecurityManager securityManager1 = getSecurityManager();
        TransactionBatchProcessor txbatchProcessor1 = new TransactionBatchProcessor(securityManager1, newBlockEditor1,
                ledgerRepo, opReg1);

        // 注册新用户；
        BlockchainKeypair userKeypair1 = BlockchainKeyGenerator.getInstance().generate();
        TransactionRequest transactionRequest1 = LedgerTestUtils.createTxRequest_UserReg(userKeypair1, ledgerHash,
                parti0, parti0);
        TransactionResponse txResp1 = txbatchProcessor1.schedule(transactionRequest1);

        LedgerBlock newBlock1 = newBlockEditor1.prepare();

        try {
            newBlockEditor1.commit();
        } catch (BlockRollbackException e) {
            newBlockEditor1.cancel();
        }

        ledgerManager = new LedgerManager();
        ledgerRepo = ledgerManager.register(ledgerHash, STORAGE_Mock);
        LedgerBlock latestBlock1 = ledgerRepo.getLatestBlock();
        assertEquals(newBlock1.getHash(), latestBlock1.getHash());
        assertEquals(1, latestBlock1.getHeight());

        LedgerDataQuery ledgerDS1 = ledgerRepo.getLedgerData(latestBlock1);
        boolean existUser1 = ledgerDS1.getUserAccountSet().contains(userKeypair1.getAddress());

        assertTrue(existUser1);

    }

    private static LedgerSecurityManager getSecurityManager() {
        LedgerSecurityManager securityManager = Mockito.mock(LedgerSecurityManager.class);

        SecurityPolicy securityPolicy = Mockito.mock(SecurityPolicy.class);
        when(securityPolicy.isEndpointEnable(any(LedgerPermission.class), any())).thenReturn(true);
        when(securityPolicy.isEndpointEnable(any(TransactionPermission.class), any())).thenReturn(true);
        when(securityPolicy.isNodeEnable(any(LedgerPermission.class), any())).thenReturn(true);
        when(securityPolicy.isNodeEnable(any(TransactionPermission.class), any())).thenReturn(true);

        when(securityManager.createSecurityPolicy(any(), any())).thenReturn(securityPolicy);

        return securityManager;
    }

    private HashDigest initLedger(MemoryKVStorage storage, BlockchainKeypair... partiKeys) {
        // 创建初始化配置；
        LedgerInitSetting initSetting = LedgerTestUtils.createLedgerInitSetting(partiKeys);

        // 创建账本；
        LedgerEditor ldgEdt = LedgerTransactionalEditor.createEditor(initSetting, LEDGER_KEY_PREFIX, storage, storage);

        TransactionRequest genesisTxReq = LedgerTestUtils.createLedgerInitTxRequest(partiKeys);
        LedgerTransactionContext genisisTxCtx = ldgEdt.newTransaction(genesisTxReq);
        LedgerDataset ldgDS = genisisTxCtx.getDataset();

        for (int i = 0; i < partiKeys.length; i++) {
            UserAccount userAccount = ldgDS.getUserAccountSet().register(partiKeys[i].getAddress(),
                    partiKeys[i].getPubKey());
            userAccount.setProperty("Name", "参与方-" + i, -1);
            userAccount.setProperty("Share", "" + (10 + i), -1);
        }

        LedgerTransaction tx = genisisTxCtx.commit(TransactionState.SUCCESS);

        assertEquals(genesisTxReq.getTransactionContent().getHash(), tx.getTransactionContent().getHash());
        assertEquals(0, tx.getBlockHeight());

        LedgerBlock block = ldgEdt.prepare();

        assertEquals(0, block.getHeight());
        assertNotNull(block.getHash());
        assertNull(block.getPreviousHash());

        // 创世区块的账本哈希为 null；
        assertNull(block.getLedgerHash());
        assertNotNull(block.getHash());

        // 提交数据，写入存储；
        ldgEdt.commit();

        HashDigest ledgerHash = block.getHash();
        return ledgerHash;
    }
}
