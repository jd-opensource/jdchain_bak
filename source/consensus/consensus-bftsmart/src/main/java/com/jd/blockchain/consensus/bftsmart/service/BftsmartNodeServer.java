package com.jd.blockchain.consensus.bftsmart.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import bftsmart.tom.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.consensus.ConsensusManageService;
import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartCommitBlockSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartNodeSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartTopology;
import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.consensus.service.NodeServer;
import com.jd.blockchain.consensus.service.ServerSettings;
import com.jd.blockchain.consensus.service.StateHandle;
import com.jd.blockchain.consensus.service.StateMachineReplicate;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRespHandle;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.data.TxContentBlob;
import com.jd.blockchain.ledger.data.TxRequestMessage;
import com.jd.blockchain.utils.PropertiesUtils;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.jd.blockchain.utils.concurrent.CompletableAsyncFuture;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

import bftsmart.reconfiguration.util.HostsConfig;
import bftsmart.reconfiguration.util.TOMConfiguration;
import bftsmart.reconfiguration.views.MemoryBasedViewStorage;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;

public class BftsmartNodeServer extends DefaultRecoverable implements NodeServer {

    private static Logger LOGGER = LoggerFactory.getLogger(BftsmartNodeServer.class);

    private List<StateHandle> stateHandles = new CopyOnWriteArrayList<>();

//    private List<BatchConsensusListener> batchConsensusListeners = new LinkedList<>();

//    private Map<ReplyContextMessage, AsyncFuture<byte[]>> replyContextMessages = new ConcurrentHashMap<>();

    // TODO 暂不处理队列溢出问题
    private ExecutorService notifyReplyExecutors = Executors.newSingleThreadExecutor();

//    private ExecutorService sendCommitExecutors = Executors.newFixedThreadPool(2);

    private volatile Status status = Status.STOPPED;

    private final Object mutex = new Object();

    private volatile ServiceReplica replica;

    private StateMachineReplicate stateMachineReplicate;

    private ServerSettings serverSettings;

    private BftsmartConsensusManageService manageService;


    private volatile BftsmartTopology topology;

    private volatile BftsmartConsensusSettings setting;

    private TOMConfiguration tomConfig;

    private HostsConfig hostsConfig;
    private Properties systemConfig;

    private MessageHandle messageHandle;

    private String providerName;

    private String realmName;

    private int serverId;

//    private volatile String  batchId = null;
//
////    private List<AsyncFuture<byte[]>> replyMessages ;
//
//    private boolean leader_has_makedicision = false;
//
//    private boolean commit_block_condition = false;
//
//    private final AtomicLong txIndex = new AtomicLong();
//
//    private final AtomicLong blockIndex = new AtomicLong();
//
//    private static final AtomicInteger incrementNum = new AtomicInteger();
//
////    private final BlockingQueue<ActionRequest> txRequestQueue = new LinkedBlockingQueue();
//
//    private final ExecutorService queueExecutor = Executors.newSingleThreadExecutor();
//
//    private final ScheduledExecutorService timerEexecutorService = new ScheduledThreadPoolExecutor(10);
//    private ServiceProxy peerProxy;


    public BftsmartNodeServer() {

    }

    public BftsmartNodeServer(ServerSettings serverSettings, MessageHandle messageHandler, StateMachineReplicate stateMachineReplicate) {
        this.serverSettings = serverSettings;
        this.realmName = serverSettings.getRealmName();
        //used later
        this.stateMachineReplicate = stateMachineReplicate;
        this.messageHandle = messageHandler;
        this.manageService = new BftsmartConsensusManageService(this);
        createConfig();
        serverId = findServerId();
        initConfig(serverId, systemConfig, hostsConfig);
    }

    //aim to send commit block message
//    protected void createProxyClient() {
//        BftsmartTopology topologyCopy = (BftsmartTopology) topology.copyOf();
//
//        MemoryBasedViewStorage viewStorage = new MemoryBasedViewStorage(topologyCopy.getView());
//
//        byte[]  bytes = BinarySerializeUtils.serialize(tomConfig);
//
//        TOMConfiguration decodeTomConfig = BinarySerializeUtils.deserialize(bytes);
//
//        decodeTomConfig.setProcessId(0);
//
//        peerProxy = new ServiceProxy(decodeTomConfig, viewStorage, null, null);
//
//    }

    protected int findServerId() {
        int serverId = 0;

        for (int i = 0; i < hostsConfig.getNum(); i++) {
            String host = ((BftsmartNodeSettings)serverSettings.getReplicaSettings()).getNetworkAddress().getHost();
            int port = ((BftsmartNodeSettings)serverSettings.getReplicaSettings()).getNetworkAddress().getPort();

            if (hostsConfig.getHost(i).equals(host) && hostsConfig.getPort(i) == port) {
                serverId = i;
                break;
            }
        }

        return serverId;
    }

    public int getServerId() {
        return serverId;
    }

    protected void createConfig() {

        setting = ((BftsmartServerSettings) serverSettings).getConsensusSettings();

        List<HostsConfig.Config> configList = new ArrayList<HostsConfig.Config>();

        NodeSettings[] nodeSettingsArray = setting.getNodes();
        for (NodeSettings nodeSettings : nodeSettingsArray) {
            BftsmartNodeSettings node = (BftsmartNodeSettings)nodeSettings;
            configList.add(new HostsConfig.Config(node.getId(), node.getNetworkAddress().getHost(), node.getNetworkAddress().getPort()));
        }

        //create HostsConfig instance based on consensus realm nodes
        hostsConfig = new HostsConfig(configList.toArray(new HostsConfig.Config[configList.size()]));

        systemConfig = PropertiesUtils.createProperties(setting.getSystemConfigs());

        return;
    }

    protected void initConfig(int id, String systemConfig, String hostsConfig) {

        this.tomConfig = new TOMConfiguration(id, systemConfig, hostsConfig);

    }

    protected void initConfig(int id, Properties systemsConfig, HostsConfig hostConfig) {
        this.tomConfig = new TOMConfiguration(id, systemsConfig, hostConfig);
    }

    @Override
    public ConsensusManageService getManageService() {
        return manageService;
    }

    @Override
    public ServerSettings getSettings() {
        return serverSettings;
    }

    @Override
    public String getProviderName() {
        return BftsmartConsensusProvider.NAME;
    }

    public TOMConfiguration getTomConfig() {
        return tomConfig;
    }

    public int getId() {
        return tomConfig.getProcessId();
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ReplicaID is negative!");
        }
        this.tomConfig.setProcessId(id);

    }

    public BftsmartConsensusSettings getConsensusSetting() {
        return setting;
    }

    public BftsmartTopology getTopology() {
        return topology;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean isRunning() {
        return status == Status.RUNNING;
    }

    public byte[] appExecuteUnordered(byte[] bytes, MessageContext messageContext) {
        return messageHandle.processUnordered(bytes).get();
    }

    @Override
    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs, boolean fromConsensus) {
        return appExecuteBatch(commands, msgCtxs, fromConsensus, null);
    }

//    private boolean checkLeaderId(MessageContext[] msgCtxs) {
//        boolean result = false;
//
//        for (int i = 0; i < msgCtxs.length - 1; i++) {
//            if (msgCtxs[i].getLeader() != msgCtxs[i+1].getLeader())
//                return result;
//        }
//
//        result = true;
//
//        return result;
//    }
//
//    //普通消息处理
//    private void normalMsgProcess(ReplyContextMessage replyContextMsg, byte[] msg, String realmName, String batchId) {
//        AsyncFuture<byte[]> replyMsg = messageHandle.processOrdered(replyContextMsg.getMessageContext().getOperationId(), msg, realmName, batchId);
//        replyContextMessages.put(replyContextMsg, replyMsg);
//    }
//
//    //结块消息处理
//    private void commitMsgProcess(ReplyContextMessage replyContextMsg, String realmName, String batchId) {
//        try{
//            //receive messages before commitblock message, then execute commit
//            if (replyContextMessages.size() != 0) {
//                messageHandle.completeBatch(realmName, batchId);
//                messageHandle.commitBatch(realmName, batchId);
//            }
//
//            // commit block msg need response too
//            CompletableAsyncFuture<byte[]> asyncFuture = new CompletableAsyncFuture<>();
//            TransactionResponse transactionRespHandle = new TransactionRespHandle(newBlockCommitRequest(),
//                    TransactionState.SUCCESS, TransactionState.SUCCESS);
//
//            asyncFuture.complete(BinaryEncodingUtils.encode(transactionRespHandle, TransactionResponse.class));
//            replyContextMessages.put(replyContextMsg, asyncFuture);
//        }catch (Exception e){
//            LOGGER.error("Error occurred on commit batch transactions, so the new block is canceled! --" + e.getMessage(), e);
//            messageHandle.rollbackBatch(realmName, batchId, -1);
//        }finally{
//            this.batchId = null;
//        }
//    }

//    private void sendCommitMessage() {
//
//        HashDigest ledgerHash = new HashDigest(Base58Utils.decode(realmName));
//
//        BlockchainKeyPair userKeyPeer = BlockchainKeyGenerator.getInstance().generate();
//
//        TxContentBlob txContentBlob = new TxContentBlob(ledgerHash);
//
//        byte[] reqBytes = BinaryEncodingUtils.encode(txContentBlob, TransactionContent.class);
//
//        HashDigest reqHash = CryptoUtils.hash(CryptoAlgorithm.SHA256).hash(reqBytes);
//
//        txContentBlob.setHash(reqHash);
//
//        TxRequestMessage transactionRequest = new TxRequestMessage(txContentBlob);
//
//        byte[] msg = BinaryEncodingUtils.encode(transactionRequest, TransactionRequest.class);
//
//        byte[] type = BytesUtils.toBytes(1);
//
//        byte[] wrapMsg = new byte[msg.length + 4];
//
//        System.arraycopy(type, 0, wrapMsg, 0, 4);
//        System.arraycopy(msg, 0, wrapMsg, 4, msg.length);
//
//        peerProxy.invokeOrdered(wrapMsg);
//
//        LOGGER.info("Send commit block msg success!");
//    }

//    private TransactionRequest newBlockCommitRequest() {
//
//        HashDigest ledgerHash = new HashDigest(Base58Utils.decode(realmName));
//
//        TxContentBlob txContentBlob = new TxContentBlob(ledgerHash);
//
//        byte[] reqBytes = BinaryEncodingUtils.encode(txContentBlob, TransactionContent.class);
//
//        HashDigest reqHash = CryptoUtils.hash(CryptoAlgorithm.SHA256).hash(reqBytes);
//
//        txContentBlob.setHash(reqHash);
//
//        TxRequestMessage transactionRequest = new TxRequestMessage(txContentBlob);
//
//        return transactionRequest;
//    }

//    private void checkConsensusFinish() {
//        BftsmartCommitBlockSettings commitBlockSettings = ((BftsmartServerSettings)serverSettings).getConsensusSettings().getCommitBlockSettings();
//        int txSize = commitBlockSettings.getTxSizePerBlock();
//        long maxDelay = commitBlockSettings.getMaxDelayMilliSecondsPerBlock();
//
//        long currIndex = txIndex.incrementAndGet();
//        if (currIndex == txSize) {
//            txIndex.set(0);
//            this.blockIndex.getAndIncrement();
//            sendCommitExecutors.execute(()-> {
//                sendCommitMessage();
//            });
//        } else if (currIndex == 1) {
////            System.out.printf("checkConsensusFinish schedule blockIndex = %s \r\n", this.blockIndex.get());
//            timerEexecutorService.schedule(timeTask(this.blockIndex.get()), maxDelay, TimeUnit.MILLISECONDS);
//        }
//
//        return;
//    }

//    @Override
//    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs, boolean fromConsensus, List<ReplyContextMessage> replyList) {
//
//        if (!checkLeaderId(msgCtxs)) {
//            throw new IllegalArgumentException();
//        }
//
//        boolean isLeader = (msgCtxs[0].getLeader() == getId());
//
//        if (isLeader) {
//            for (int i = 0; i < commands.length; i++) {
//                byte[] wrapMsg = commands[i];
//                byte[] type = new byte[4];
//                byte[] msg= new byte[wrapMsg.length - 4];
//
//                System.arraycopy(wrapMsg, 0, type, 0, 4);
//                System.arraycopy(wrapMsg, 4, msg, 0, wrapMsg.length - 4);
//
//                MessageContext messageContext = msgCtxs[i];
//                ReplyContextMessage replyContextMessage = replyList.get(i);
//                replyContextMessage.setMessageContext(messageContext);
//
//                if (batchId == null) {
//                    batchId = messageHandle.beginBatch(realmName);
//                }
//
//                int msgType = BytesUtils.readInt(new ByteArrayInputStream(type));
//
//                if (msgType == 0) {
//
//                    //only leader do it
//                    checkConsensusFinish();
//                    //normal message process
//                    normalMsgProcess(replyContextMessage, msg, realmName, batchId);
//                }
//                if (!leader_has_makedicision) {
//                    if (msgType == 1) {
//                        LOGGER.error("Error occurred on appExecuteBatch msg process, leader confilicting error!");
//                    }
//
//                    if (commit_block_condition) {
//                        leader_has_makedicision = true;
//
////                        sendCommitExecutors.execute(() -> {
//                            commit_block_condition = false;
//                            LOGGER.info("Txcount execute commit block!");
//                            sendCommitMessage();
////                        });
//
//                    }
//                } else if (msgType == 1) {
//                    //commit block message
//                    commitMsgProcess(replyContextMessage, realmName, batchId);
//                    leader_has_makedicision = false;
//                    sendReplyMessage();
//                }
//            }
//        } else {
//            for (int i = 0; i < commands.length; i++) {
//                byte[] wrapMsg = commands[i];
//                byte[] type = new byte[4];
//                byte[] msg= new byte[wrapMsg.length - 4];
//
//                System.arraycopy(wrapMsg, 0, type, 0, 4);
//                System.arraycopy(wrapMsg, 4, msg, 0, wrapMsg.length - 4);
//
//                MessageContext messageContext = msgCtxs[i];
//                ReplyContextMessage replyContextMessage = replyList.get(i);
//                replyContextMessage.setMessageContext(messageContext);
//
//                if (batchId == null) {
//                    batchId = messageHandle.beginBatch(realmName);
//                }
//
//                int msgType = BytesUtils.readInt(new ByteArrayInputStream(type));
//
//                if (msgType == 0) {
//                    //normal message
//                    normalMsgProcess(replyContextMessage, msg, realmName, batchId);
//                } else if (msgType == 1) {
//                    // commit block message
//                    commitMsgProcess(replyContextMessage, realmName, batchId);
//                    sendReplyMessage();
//                }
//            }
//        }
//
//        return null;
//    }

    @Override
    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs, boolean fromConsensus, List<ReplyContextMessage> replyList) {

        if (replyList == null || replyList.size() == 0) {
            throw new IllegalArgumentException();
        }


        // todo 此部分需要重新改造
        /**
         * 默认BFTSmart接口提供的commands是一个或多个共识结果的顺序集合
         * 根据共识的规定，目前的做法是将其根据msgCtxs的内容进行分组，每组都作为一个结块标识来处理
         * 从msgCtxs可以获取对应commands的分组情况
         */
        int manageConsensusId = msgCtxs[0].getConsensusId();
        List<byte[]> manageConsensusCmds = new ArrayList<>();
        List<ReplyContextMessage> manageReplyMsgs = new ArrayList<>();

        int index = 0;
        for (MessageContext msgCtx : msgCtxs) {
            if (msgCtx.getConsensusId() == manageConsensusId) {
                manageConsensusCmds.add(commands[index]);
                manageReplyMsgs.add(replyList.get(index));
            } else {
                // 达到结块标准，需要进行结块并应答
                blockAndReply(manageConsensusCmds, manageReplyMsgs);
                // 重置链表和共识ID
                manageConsensusCmds = new ArrayList<>();
                manageReplyMsgs = new ArrayList<>();
                manageConsensusId = msgCtx.getConsensusId();
                manageConsensusCmds.add(commands[index]);
                manageReplyMsgs.add(replyList.get(index));
            }
            index++;
        }
        // 结束时，肯定有最后一个结块请求未处理
        if (!manageConsensusCmds.isEmpty()) {
            blockAndReply(manageConsensusCmds, manageReplyMsgs);
        }


////        if (!checkLeaderId(msgCtxs)) {
////            throw new IllegalArgumentException();
////        }
//
//        if (replyList == null || replyList.size() == 0) {
//            throw new IllegalArgumentException();
//        }
//
//        for (int i = 0; i < commands.length; i++) {
//            byte[] wrapMsg = commands[i];
//            byte[] type = new byte[4];
//            byte[] msg= new byte[wrapMsg.length - 4];
//            // batch messages, maybe in different consensus instance, leader also maybe different
//            boolean isLeader = (msgCtxs[i].getLeader() == getId());
//
//            System.arraycopy(wrapMsg, 0, type, 0, 4);
//            System.arraycopy(wrapMsg, 4, msg, 0, wrapMsg.length - 4);
//
//            MessageContext messageContext = msgCtxs[i];
//            ReplyContextMessage replyContextMessage = replyList.get(i);
//            replyContextMessage.setMessageContext(messageContext);
//
//            if (batchId == null) {
//                batchId = messageHandle.beginBatch(realmName);
//            }
//
//            int msgType = BytesUtils.readInt(new ByteArrayInputStream(type));
//
//            if (msgType == 0) {
//
//                //only leader do it
//                if (isLeader) {
//                    checkConsensusFinish();
//                }
//                //normal message process
//                normalMsgProcess(replyContextMessage, msg, realmName, batchId);
//            }
//            else if (msgType == 1) {
//                //commit block message
//                commitMsgProcess(replyContextMessage, realmName, batchId);
//                sendReplyMessage();
//            }
//        }

        return null;
    }

    private void blockAndReply(List<byte[]> manageConsensusCmds, List<ReplyContextMessage> replyList) {
        String batchId = messageHandle.beginBatch(realmName);
        List<AsyncFuture<byte[]>> asyncFutureLinkedList = new ArrayList<>(manageConsensusCmds.size());
        try {
            int msgId = 0;
            for (byte[] txContent : manageConsensusCmds) {
                AsyncFuture<byte[]> asyncFuture = messageHandle.processOrdered(msgId++, txContent, realmName, batchId);
                asyncFutureLinkedList.add(asyncFuture);
            }
            messageHandle.completeBatch(realmName, batchId);
            messageHandle.commitBatch(realmName, batchId);
        } catch (Exception e) {
            // todo 需要处理应答码 404
            messageHandle.rollbackBatch(realmName, batchId, TransactionState.CONSENSUS_ERROR.CODE);
        }

        // 通知线程单独处理应答
        notifyReplyExecutors.execute(() -> {
            // 应答对应的结果
            int replyIndex = 0;
            for(ReplyContextMessage msg : replyList) {
                msg.setReply(asyncFutureLinkedList.get(replyIndex).get());
                TOMMessage request = msg.getTomMessage();
                ReplyContext replyContext = msg.getReplyContext();
                request.reply = new TOMMessage(replyContext.getId(), request.getSession(), request.getSequence(),
                        request.getOperationId(), msg.getReply(), replyContext.getCurrentViewId(),
                        request.getReqType());

                if (replyContext.getNumRepliers() > 0) {
                    bftsmart.tom.util.Logger.println("(ServiceReplica.receiveMessages) sending reply to "
                            + request.getSender() + " with sequence number " + request.getSequence()
                            + " and operation ID " + request.getOperationId() + " via ReplyManager");
                    replyContext.getRepMan().send(request);
                } else {
                    bftsmart.tom.util.Logger.println("(ServiceReplica.receiveMessages) sending reply to "
                            + request.getSender() + " with sequence number " + request.getSequence()
                            + " and operation ID " + request.getOperationId());
                    replyContext.getReplier().manageReply(request, msg.getMessageContext());
                }
                replyIndex++;
            }
        });
    }

//    private void sendReplyMessage() {
//        for (ReplyContextMessage  msg: replyContextMessages.keySet()) {
//            byte[] reply = replyContextMessages.get(msg).get();
//            msg.setReply(reply);
//            TOMMessage request = msg.getTomMessage();
//            ReplyContext replyContext = msg.getReplyContext();
//            request.reply = new TOMMessage(replyContext.getId(), request.getSession(), request.getSequence(),
//                    request.getOperationId(), msg.getReply(), replyContext.getCurrentViewId(),
//                    request.getReqType());
//
//            if (replyContext.getNumRepliers() > 0) {
//                bftsmart.tom.util.Logger.println("(ServiceReplica.receiveMessages) sending reply to "
//                        + request.getSender() + " with sequence number " + request.getSequence()
//                        + " and operation ID " + request.getOperationId() + " via ReplyManager");
//                replyContext.getRepMan().send(request);
//            } else {
//                bftsmart.tom.util.Logger.println("(ServiceReplica.receiveMessages) sending reply to "
//                        + request.getSender() + " with sequence number " + request.getSequence()
//                        + " and operation ID " + request.getOperationId());
//                replyContext.getReplier().manageReply(request, msg.getMessageContext());
//                // cs.send(new int[]{request.getSender()}, request.reply);
//            }
//        }
//        replyContextMessages.clear();
//    }

//    private Runnable timeTask(final long currBlockIndex) {
//        Runnable task = () -> {
//            boolean isAdd = this.blockIndex.compareAndSet(currBlockIndex, currBlockIndex + 1);
//            if (isAdd) {
//                LOGGER.info("TimerTask execute commit block! ");
//                this.txIndex.set(0);
//                timerEexecutorService.execute(()-> {
//                    sendCommitMessage();
//                });
//            }
//        };
//        return task;
//    }

    //notice
    public byte[] getSnapshot() {
        LOGGER.debug("------- GetSnapshot...[replica.id=" + this.getId() + "]");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BytesUtils.writeInt(stateHandles.size(), out);
        for (StateHandle stateHandle : stateHandles) {
            // TODO: 测试代码；
            return stateHandle.takeSnapshot();

            // byte[] state = stateHandle.takeSnapshot();
            // BytesEncoding.writeInNormal(state, out);
        }
        return out.toByteArray();
    }

    public void installSnapshot(byte[] snapshot) {
//        System.out.println("Not implement!");
    }

    @Override
    public void start() {
        if (this.getId() < 0) {
            throw new IllegalStateException("Unset server node ID！");
        }
        LOGGER.debug("=============================== Start replica ===================================");

        if (status != Status.STOPPED) {
            return;
        }
        synchronized (mutex) {
            if (status != Status.STOPPED) {
                return;
            }
            status = Status.STARTING;

            try {
                LOGGER.debug("Start replica...[ID=" + getId() + "]");
                this.replica = new ServiceReplica(tomConfig, this, this);
                this.topology = new BftsmartTopology(replica.getReplicaContext().getCurrentView());
                status = Status.RUNNING;
//                createProxyClient();
                LOGGER.debug(
                        "=============================== Replica started success! ===================================");
            } catch (RuntimeException e) {
                status = Status.STOPPED;
                throw e;
            }
        }

    }

    @Override
    public void stop() {
        if (status != Status.RUNNING) {
            return;
        }
        synchronized (mutex) {
            if (status != Status.RUNNING) {
                return;
            }
            status = Status.STOPPING;

            try {
                ServiceReplica rep = this.replica;
                if (rep != null) {
                    LOGGER.debug("Stop replica...[ID=" + rep.getId() + "]");
                    this.replica = null;
                    this.topology = null;

                    rep.kill();
                    LOGGER.debug("Replica had stopped! --[ID=" + rep.getId() + "]");
                }
            } finally {
                status = Status.STOPPED;
            }
        }
    }

//    private static class ActionRequestExtend {
//
//
//        ReplyContextMessage replyContextMessage;
//
//        private byte[] message;
//
//        private ActionRequest actionRequest;
//
//        public ActionRequestExtend(byte[] message) {
//            this.message = message;
//            actionRequest = BinaryEncodingUtils.decode(message);
//        }
//
//        public byte[] getMessage() {
//            return message;
//        }
//
//        public ReplyContextMessage getReplyContextMessage() {
//            return replyContextMessage;
//        }
//
//        public ActionRequest getActionRequest() {
//            return actionRequest;
//        }
//    }

    enum Status {

        STARTING,

        RUNNING,

        STOPPING,

        STOPPED

    }

}
