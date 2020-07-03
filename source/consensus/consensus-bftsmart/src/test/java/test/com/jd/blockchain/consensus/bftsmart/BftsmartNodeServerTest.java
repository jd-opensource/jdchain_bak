package test.com.jd.blockchain.consensus.bftsmart;

import bftsmart.consensus.app.BatchAppResultImpl;
import bftsmart.tom.MessageContext;
import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.consensus.bftsmart.service.BftsmartNodeServer;
import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.consensus.service.StateSnapshot;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.transaction.BlockchainOperationFactory;
import com.jd.blockchain.transaction.DigitalSignatureBlob;
import com.jd.blockchain.transaction.TxContentBlob;
import com.jd.blockchain.transaction.TxRequestMessage;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.jd.blockchain.utils.concurrent.CompletableAsyncFuture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BftsmartNodeServerTest {

    private static final byte[] jdChainBytes = "JDChain".getBytes(StandardCharsets.UTF_8);

    private BftsmartNodeServer bftsmartNodeServer;

    public static final long currentBlockHeight = 100;

    private BftsmartNodeServer.InnerStateHolder innerStateHolder;

    @Before
    public void init() throws Exception {
        bftsmartNodeServer = new BftsmartNodeServer();
        Class<?> clazz = BftsmartNodeServer.class;
        Field field = clazz.getDeclaredField("messageHandle");
        field.setAccessible(true); // 将私有变量的访问权限设置为true
        field.set(bftsmartNodeServer, new MessageHandleImpl());

        field = clazz.getDeclaredField("stateHolder");
        field.setAccessible(true); // 将私有变量的访问权限设置为true
        innerStateHolder = innerStateHolder();
        field.set(bftsmartNodeServer, innerStateHolder);
    }


    /**
     * 在正常的执行环境下，发送了一个比原来更大的CID
     * 预期中是不应该被执行的，可以通过获取lastCID的方式来判断
     * 然后由于其他原因触发了Write消息的再次执行（相同的CID），执行完成后执行Accept消息成功
     */
    @Test
    public void testMoreWriteMsgRepeat() throws Exception {
        int cidTemp = (int)innerStateHolder.getLastCid();
        int cid = cidTemp + 10;
        byte[][] commands = commands();
        for (int i = cid; i < cid + 5; i++) {
            System.out.printf("Start execute messages cid = %s \r\n", i);
            BatchAppResultImpl batchAppResult;
            batchAppResult = bftsmartNodeServer.preComputeAppHash(i, commands);
            bftsmartNodeServer.preComputeAppCommit(i, batchAppResult.getBatchId());
            System.out.printf("Execute messages cid = %s complete \r\n", i);
        }
        // 获取innerStateHolder中的lastCID
        System.out.println("LastCID = " + innerStateHolder.getLastCid());
        Assert.assertEquals(cidTemp, innerStateHolder.getLastCid());
    }

    /**
     * 第一次执行完Write消息（预计算后）未提交；
     * 然后由于其他原因触发了Write消息的再次执行（相同的CID），执行完成后执行Accept消息成功
     */
    @Test
    public void testWriteMsgRepeat() throws Exception {
        int cid = (int)innerStateHolder.getLastCid() + 1;
        byte[][] commands = commands();
        BatchAppResultImpl batchAppResult;
        System.out.printf("Execute prepare compute phase for cid = %s \r\n", cid);
        bftsmartNodeServer.preComputeAppHash(cid, commands);
        // 此处未提交，直接进行重复进行二阶段操作
        System.out.printf("Execute prepare compute phase for cid = %s repeat \r\n", cid);
        batchAppResult = bftsmartNodeServer.preComputeAppHash(cid, commands);
        System.out.printf("Prepare compute phase for cid = %s success, will commit \r\n", cid);
        bftsmartNodeServer.preComputeAppCommit(cid, batchAppResult.getBatchId());
    }

    /**
     * 未知原因导致小的CID重复执行
     * 预期：不执行，即不打印MessageHandleImpl中的相关信息
     * 理论上不会出现该情况
     *
     */
    @Test
    public void testLittleWriteMsgRepeat() throws Exception {
        int cid = (int)innerStateHolder.getLastCid() - 10;
        byte[][] commands = commands();
        for (int i = cid; i < cid + 5; i++) {
            BatchAppResultImpl batchAppResult;
            System.out.printf("Execute prepare compute phase for cid = %s \r\n", i);
            batchAppResult = bftsmartNodeServer.preComputeAppHash(i, commands);
            if (!batchAppResult.getBatchId().equals("") && batchAppResult.getErrprCode() == 0) {
                // 不打印才是正常的
                System.out.printf("Prepare compute phase for cid = %s success, will commit \r\n", i);
                bftsmartNodeServer.preComputeAppCommit(i, batchAppResult.getBatchId());
            }
        }
    }

    /**
     * 状态传输导致小的CID重复执行
     * 预期：不执行比当前CID小的值，只打印比当前CID大的值
     *
     */
    @Test
    public void testStateTransferLittleWriteMsgRepeat() throws Exception {
        System.out.println("----- cid = " + innerStateHolder.getLastCid() + " -----");
        int cid = (int)innerStateHolder.getLastCid() - 10;
        byte[][] commands = commands();
        for (int i = cid; i < cid + 20; i++) {
            System.out.printf("Execute state transfer phase for cid = %s start \r\n", i);
            MessageContext[] msgCtxs = msgCtxs(i);
            bftsmartNodeServer.appExecuteBatch(commands, msgCtxs, false);
            System.out.printf("Execute state transfer phase for cid = %s complete \r\n", i);
        }
    }


    /**
     * 状态传输出错（出现跨Checkpoint）的情况，此时的CID比正常要大，这种情况下会打印错误信息，但不会操作账本
     *
     */
    @Test
    public void testStateTransferMoreWriteMsgRepeat() throws Exception {
        int cidTemp = (int)innerStateHolder.getLastCid();
        System.out.println("----- cid = " + cidTemp + " -----");
        int cid = cidTemp + 10;
        byte[][] commands = commands();
        for (int i = cid; i < cid + 5; i++) {
            System.out.printf("Execute state transfer phase for cid = %s start \r\n", i);
            MessageContext[] msgCtxs = msgCtxs(i);
            bftsmartNodeServer.appExecuteBatch(commands, msgCtxs, false);
            System.out.printf("Execute state transfer phase for cid = %s complete \r\n", i);
        }
        Assert.assertEquals(cidTemp, innerStateHolder.getLastCid());
    }

    /**
     * 状态传输导致小的CID重复执行
     * 预期：不执行比当前CID小的值，只打印比当前CID大的值
     *
     */
    @Test
    public void testStateTransferAfterWriteMsgRepeat() throws Exception {
        int stateTransferStartCid = (int)innerStateHolder.getLastCid() - 10;
        // 正常执行二阶段、三阶段，最后一次不执行三阶段
        for (int j = 0; j < 5; j++) {
            int normalCid = (int)innerStateHolder.getLastCid() + 1;
            System.out.printf("Start normal consensus for cid = %s \r\n", normalCid);

            BatchAppResultImpl batchAppResult = null;
            System.out.printf("Execute normal prepare compute phase for cid = %s \r\n", normalCid);
            batchAppResult = bftsmartNodeServer.preComputeAppHash(normalCid, commands());
            if (j != 4) {
                System.out.printf("Prepare compute phase for cid = %s success, will commit \r\n", normalCid);
                bftsmartNodeServer.preComputeAppCommit(normalCid, batchAppResult.getBatchId());
            }
            System.out.println("======================================================= \r\n\r\n");
        }
        byte[][] commands = commands();
        for (int i = stateTransferStartCid; i < stateTransferStartCid + 20; i++) {
            System.out.printf("Execute state transfer phase for cid = %s start \r\n", i);
            MessageContext[] msgCtxs = msgCtxs(i);
            bftsmartNodeServer.appExecuteBatch(commands, msgCtxs, false);
            System.out.printf("Execute state transfer phase for cid = %s complete \r\n", i);
        }
    }

    @Test
    public void testNormalExecute() {
        int cidTemp = (int)innerStateHolder.getLastCid();
        for (int j = 0; j < 10; j++) {
            int normalCid = (int)innerStateHolder.getLastCid() + 1;
            System.out.printf("Start normal consensus for cid = %s \r\n", normalCid);

            BatchAppResultImpl batchAppResult = null;
            try {
                System.out.printf("Execute normal prepare compute phase for cid = %s \r\n", normalCid);
                batchAppResult = bftsmartNodeServer.preComputeAppHash(normalCid, commands());
            } catch (Exception e) {
                System.out.printf("Prepare compute phase for cid = %s fail, will rollback \r\n", normalCid);
                bftsmartNodeServer.preComputeAppRollback(normalCid, batchAppResult.getBatchId());
            }
            System.out.printf("Prepare compute phase for cid = %s success, will commit \r\n", normalCid);
            bftsmartNodeServer.preComputeAppCommit(normalCid, batchAppResult.getBatchId());
            System.out.println("======================================================= \r\n\r\n");
        }
        Assert.assertEquals(cidTemp + 10, (int)innerStateHolder.getLastCid());
    }

    private byte[][] commands() throws Exception {
        byte[][] commands = new byte[10][];
        for (int i = 0; i < commands.length; i++) {
            byte[] bytes = BinaryProtocol.encode(initTxRequestMessage(), TransactionRequest.class);
            commands[i] = bytes;
        }
        return commands;
    }

    private TxRequestMessage initTxRequestMessage() throws Exception {
        TxRequestMessage txRequestMessage = new TxRequestMessage(initTransactionContent());

        SignatureDigest digest1 = new SignatureDigest(ClassicAlgorithm.ED25519, "zhangsan".getBytes());
        SignatureDigest digest2 = new SignatureDigest(ClassicAlgorithm.ED25519, "lisi".getBytes());
        DigitalSignatureBlob endPoint1 = new DigitalSignatureBlob(
                new PubKey(ClassicAlgorithm.ED25519, "jd1.com".getBytes()), digest1);
        DigitalSignatureBlob endPoint2 = new DigitalSignatureBlob(
                new PubKey(ClassicAlgorithm.ED25519, "jd2.com".getBytes()), digest2);
        txRequestMessage.addEndpointSignatures(endPoint1);
        txRequestMessage.addEndpointSignatures(endPoint2);

        SignatureDigest digest3 = new SignatureDigest(ClassicAlgorithm.ED25519, "wangwu".getBytes());
        SignatureDigest digest4 = new SignatureDigest(ClassicAlgorithm.ED25519, "zhaoliu".getBytes());
        DigitalSignatureBlob node1 = new DigitalSignatureBlob(
                new PubKey(ClassicAlgorithm.ED25519, "jd3.com".getBytes()), digest3);
        DigitalSignatureBlob node2 = new DigitalSignatureBlob(
                new PubKey(ClassicAlgorithm.ED25519, "jd4.com".getBytes()), digest4);
        txRequestMessage.addNodeSignatures(node1);
        txRequestMessage.addNodeSignatures(node2);

        return txRequestMessage;
    }

    private TransactionContent initTransactionContent() throws Exception {
        TxContentBlob contentBlob = null;
        BlockchainKeypair id = BlockchainKeyGenerator.getInstance().generate(ClassicAlgorithm.ED25519);
        HashDigest ledgerHash = Crypto.getHashFunction("SHA256")
                .hash(UUID.randomUUID().toString().getBytes("UTF-8"));
        BlockchainOperationFactory opFactory = new BlockchainOperationFactory();
        contentBlob = new TxContentBlob(ledgerHash);
        contentBlob.setHash(new HashDigest(ClassicAlgorithm.SHA256, "jd.com".getBytes()));
        // contentBlob.setSubjectAccount(id.getAddress());
        // contentBlob.setSequenceNumber(1);
        DataAccountKVSetOperation kvsetOP = opFactory.dataAccount(id.getAddress())
                .setText("Name", "AAA", -1).getOperation();
        contentBlob.addOperation(kvsetOP);
        return contentBlob;
    }

    private MessageContext[] msgCtxs(int consensusId) {
        MessageContext[] messageContexts = new MessageContext[10];
        messageContexts[0] = new MessageContext(-1, 0, null, -1, 0,
                0, 0, null, -1L, 0, 0L, 0, 0,
                consensusId, null, null, false);
        return messageContexts;
    }

    private BftsmartNodeServer.InnerStateHolder innerStateHolder() {
        return new BftsmartNodeServer.InnerStateHolder(currentBlockHeight - 1);
    }

    private static class MessageHandleImpl implements MessageHandle {

        private String realName = "JDChain";

        private final Lock lock = new ReentrantLock();

        private final AtomicLong counter = new AtomicLong(currentBlockHeight);

        private String batchId;

        private List<CompletableAsyncFuture<byte[]>> list = new ArrayList<>();

        @Override
        public String beginBatch(String realmName) {
            lock.lock();
            try {
                if (batchId == null) {
                    batchId = this.realName + "-" + counter.getAndIncrement();
                }
            } finally {
                lock.unlock();
            }
            return batchId;
        }

        @Override
        public AsyncFuture<byte[]> processOrdered(int messageId, byte[] message, String realmName, String batchId) {
            CompletableAsyncFuture<byte[]> future = new CompletableAsyncFuture<>();
            list.add(future);
            return future;
        }

        @Override
        public StateSnapshot completeBatch(String realmName, String batchId) {
            InnerStateSnapshot innerStateSnapshot = new InnerStateSnapshot(counter.get());
            lock.lock();
            try {
                for (CompletableAsyncFuture<byte[]> future : list) {
                    future.complete(jdChainBytes);
                }
                list.clear();
            } finally {
                lock.unlock();
            }
            return innerStateSnapshot;
        }

        @Override
        public void commitBatch(String realmName, String batchId) {
            lock.lock();
            try {
                if (this.batchId.equals(batchId)) {
                    System.out.printf("BatchId[%s] execute commit...... \r\n", batchId);
                    this.batchId = null;
                } else {
                    System.out.printf("Local[BatchId = %s] not equal with %s in commit phase...... \r\n", this.batchId, batchId);
                }
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void rollbackBatch(String realmName, String batchId, int reasonCode) {
            lock.lock();
            try {
                if (this.batchId.equals(batchId)) {
                    System.out.printf("BatchId[%s] execute rollback...... \r\n", batchId);
                    this.batchId = null;
                } else {
                    System.out.printf("Local[BatchId = %s] not equal with %s in rollback phase...... \r\n", this.batchId, batchId);
                }
            } finally {
                lock.unlock();
            }
        }

        @Override
        public AsyncFuture<byte[]> processUnordered(byte[] message) {
            return null;
        }

        @Override
        public StateSnapshot getStateSnapshot(String realmName) {
            return new InnerStateSnapshot(counter.get() - 1);
        }

        @Override
        public StateSnapshot getGenisStateSnapshot(String realmName) {
            return new InnerStateSnapshot(0);
        }
    }

    private static class InnerStateSnapshot implements StateSnapshot {

        private long id;

        private byte[] snapshot;

        public InnerStateSnapshot(long id) {
            this.id = id;
            snapshot = "JDChain".getBytes(StandardCharsets.UTF_8);
        }

        public InnerStateSnapshot(long id, byte[] snapshot) {
            this.id = id;
            this.snapshot = snapshot;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public byte[] getSnapshot() {
            return snapshot;
        }
    }
}
