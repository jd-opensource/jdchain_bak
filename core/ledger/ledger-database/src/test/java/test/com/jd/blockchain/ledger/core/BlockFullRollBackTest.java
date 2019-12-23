package test.com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
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

    private BlockchainKeypair parti0 = BlockchainKeyGenerator.getInstance().generate();
    private BlockchainKeypair parti1 = BlockchainKeyGenerator.getInstance().generate();
    private BlockchainKeypair parti2 = BlockchainKeyGenerator.getInstance().generate();
    private BlockchainKeypair parti3 = BlockchainKeyGenerator.getInstance().generate();

    private BlockchainKeypair[] participants = { parti0, parti1, parti2, parti3 };


    @Test
    public void testBlockFullkRollBack() {

        final MemoryKVStorage STORAGE = new MemoryKVStorage();

        final MemoryKVStorage STORAGE_Mock = Mockito.spy(STORAGE);

        // 初始化账本到指定的存储库；
        ledgerHash = initLedger(STORAGE_Mock, parti0, parti1, parti2, parti3);

        System.out.println("---------- Ledger init OK !!! ----------");

        // 加载账本；
        LedgerManager ledgerManager = new LedgerManager();

        LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, STORAGE_Mock);

        // 构造存储错误，并产生区块回滚
        doThrow(BlockRollbackException.class).when(STORAGE_Mock).set(any(), any(), anyLong());

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

        doCallRealMethod().when(STORAGE_Mock).set(any(), any(), anyLong());

        //区块正常提交
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
