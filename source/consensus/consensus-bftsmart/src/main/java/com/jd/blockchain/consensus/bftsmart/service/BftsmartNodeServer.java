package com.jd.blockchain.consensus.bftsmart.service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bftsmart.consensus.app.BatchAppResultImpl;
import bftsmart.tom.*;
import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.consensus.service.*;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.transaction.TxResponseMessage;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jd.blockchain.consensus.ConsensusManageService;
import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartNodeSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartTopology;
import com.jd.blockchain.utils.PropertiesUtils;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.jd.blockchain.utils.io.BytesUtils;
import bftsmart.reconfiguration.util.HostsConfig;
import bftsmart.reconfiguration.util.TOMConfiguration;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;

public class BftsmartNodeServer extends DefaultRecoverable implements NodeServer {

    private static Logger LOGGER = LoggerFactory.getLogger(BftsmartNodeServer.class);

    private static final String DEFAULT_BINDING_HOST = "0.0.0.0";

    private List<StateHandle> stateHandles = new CopyOnWriteArrayList<>();

    // TODO 暂不处理队列溢出问题
    private ExecutorService notifyReplyExecutors = Executors.newSingleThreadExecutor();

    private volatile Status status = Status.STOPPED;

    private final Object mutex = new Object();

    private volatile ServiceReplica replica;

    private StateMachineReplicate stateMachineReplicate;

    private ServerSettings serverSettings;

    private BftsmartConsensusManageService manageService;


    private volatile BftsmartTopology topology;

    private volatile BftsmartConsensusSettings setting;

    private TOMConfiguration tomConfig;

    private TOMConfiguration outerTomConfig;

    private HostsConfig hostsConfig;
    private Properties systemConfig;

    private MessageHandle messageHandle;

    private String providerName;

    private String realmName;

    private int serverId;

    public BftsmartNodeServer() {

    }

    public BftsmartNodeServer(ServerSettings serverSettings, MessageHandle messageHandler, StateMachineReplicate stateMachineReplicate) {
        this.serverSettings = serverSettings;
        this.realmName = serverSettings.getRealmName();
        //used later
        this.stateMachineReplicate = stateMachineReplicate;
        this.messageHandle = messageHandler;
        createConfig();
        serverId = findServerId();
        initConfig(serverId, systemConfig, hostsConfig);
        this.manageService = new BftsmartConsensusManageService(this);
    }

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

        List<HostsConfig.Config> configList = new ArrayList<>();

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

    protected void initConfig(int id, Properties systemsConfig, HostsConfig hostConfig) {
        byte[] serialHostConf = BinarySerializeUtils.serialize(hostConfig);
        Properties sysConfClone = (Properties)systemsConfig.clone();
        int port = hostConfig.getPort(id);
        hostConfig.add(id, DEFAULT_BINDING_HOST, port);
        this.tomConfig = new TOMConfiguration(id, systemsConfig, hostConfig);
        this.outerTomConfig = new TOMConfiguration(id, sysConfClone, BinarySerializeUtils.deserialize(serialHostConf));
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
        return outerTomConfig;
    }

    public int getId() {
        return tomConfig.getProcessId();
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ReplicaID is negative!");
        }
        this.tomConfig.setProcessId(id);
        this.outerTomConfig.setProcessId(id);
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

    /**
     *
     *  Only block, no reply， used by state transfer when peer start
     *
     */
    private void block(List<byte[]> manageConsensusCmds) {

        String batchId = messageHandle.beginBatch(realmName);
        try {
            int msgId = 0;
            for (byte[] txContent : manageConsensusCmds) {
                AsyncFuture<byte[]> asyncFuture = messageHandle.processOrdered(msgId++, txContent, realmName, batchId);
            }
            messageHandle.completeBatch(realmName, batchId);
            messageHandle.commitBatch(realmName, batchId);
        } catch (Exception e) {
            // todo 需要处理应答码 404
            LOGGER.error("Error occurred while processing ordered messages! --" + e.getMessage(), e);
            messageHandle.rollbackBatch(realmName, batchId, TransactionState.CONSENSUS_ERROR.CODE);
        }

    }

    /**
     *
     *  Local peer has cid diff with remote peer, used by state transfer when peer start
     *
     */
    private byte[][] appExecuteDiffBatch(byte[][] commands, MessageContext[] msgCtxs) {

        int manageConsensusId = msgCtxs[0].getConsensusId();
        List<byte[]> manageConsensusCmds = new ArrayList<>();

        int index = 0;
        for (MessageContext msgCtx : msgCtxs) {
            if (msgCtx.getConsensusId() == manageConsensusId) {
                manageConsensusCmds.add(commands[index]);
            } else {
                // 达到结块标准，需要进行结块并应答
                block(manageConsensusCmds);
                // 重置链表和共识ID
                manageConsensusCmds = new ArrayList<>();
                manageConsensusId = msgCtx.getConsensusId();
                manageConsensusCmds.add(commands[index]);
            }
            index++;
        }
        // 结束时，肯定有最后一个结块请求未处理
        if (!manageConsensusCmds.isEmpty()) {
            block(manageConsensusCmds);
        }
        return null;

    }

    /**
     *
     *  Invoked by state transfer when peer start
     *
     */
    @Override
    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs, boolean fromConsensus) {

        // Not from consensus outcomes， from state transfer
        if (!fromConsensus) {
            return appExecuteDiffBatch(commands, msgCtxs);
        }

        return null;
    }

    /**
     *
     *  From consensus outcomes, do nothing now
     *  The operation of executing the batch was moved to the consensus stage 2 and 3, in order to guaranteed ledger consistency
     */
    @Override
    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs, boolean fromConsensus, List<ReplyContextMessage> replyList) {

//        if (replyList == null || replyList.size() == 0) {
//            throw new IllegalArgumentException();
//        }
//        // todo 此部分需要重新改造
//        /**
//         * 默认BFTSmart接口提供的commands是一个或多个共识结果的顺序集合
//         * 根据共识的规定，目前的做法是将其根据msgCtxs的内容进行分组，每组都作为一个结块标识来处理
//         * 从msgCtxs可以获取对应commands的分组情况
//         */
//        int manageConsensusId = msgCtxs[0].getConsensusId();
//        List<byte[]> manageConsensusCmds = new ArrayList<>();
//        List<ReplyContextMessage> manageReplyMsgs = new ArrayList<>();
//
//        int index = 0;
//        for (MessageContext msgCtx : msgCtxs) {
//            if (msgCtx.getConsensusId() == manageConsensusId) {
//                manageConsensusCmds.add(commands[index]);
//                manageReplyMsgs.add(replyList.get(index));
//            } else {
//                // 达到结块标准，需要进行结块并应答
//                blockAndReply(manageConsensusCmds, manageReplyMsgs);
//                // 重置链表和共识ID
//                manageConsensusCmds = new ArrayList<>();
//                manageReplyMsgs = new ArrayList<>();
//                manageConsensusId = msgCtx.getConsensusId();
//                manageConsensusCmds.add(commands[index]);
//                manageReplyMsgs.add(replyList.get(index));
//            }
//            index++;
//        }
//        // 结束时，肯定有最后一个结块请求未处理
//        if (!manageConsensusCmds.isEmpty()) {
//            blockAndReply(manageConsensusCmds, manageReplyMsgs);
//        }
        return null;
    }

    /**
     *
     *  Block and reply are moved to consensus completion stage
     *
     */
    private void blockAndReply(List<byte[]> manageConsensusCmds, List<ReplyContextMessage> replyList) {
//        consensusBatchId = messageHandle.beginBatch(realmName);
//        List<AsyncFuture<byte[]>> asyncFutureLinkedList = new ArrayList<>(manageConsensusCmds.size());
//        try {
//            int msgId = 0;
//            for (byte[] txContent : manageConsensusCmds) {
//                AsyncFuture<byte[]> asyncFuture = messageHandle.processOrdered(msgId++, txContent, realmName, consensusBatchId);
//                asyncFutureLinkedList.add(asyncFuture);
//            }
//            messageHandle.completeBatch(realmName, consensusBatchId);
//            messageHandle.commitBatch(realmName, consensusBatchId);
//        } catch (Exception e) {
//            // todo 需要处理应答码 404
//        	LOGGER.error("Error occurred while processing ordered messages! --" + e.getMessage(), e);
//            messageHandle.rollbackBatch(realmName, consensusBatchId, TransactionState.CONSENSUS_ERROR.CODE);
//        }
//
//        // 通知线程单独处理应答
//        notifyReplyExecutors.execute(() -> {
//            // 应答对应的结果
//            int replyIndex = 0;
//            for(ReplyContextMessage msg : replyList) {
//                msg.setReply(asyncFutureLinkedList.get(replyIndex).get());
//                TOMMessage request = msg.getTomMessage();
//                ReplyContext replyContext = msg.getReplyContext();
//                request.reply = new TOMMessage(replyContext.getId(), request.getSession(), request.getSequence(),
//                        request.getOperationId(), msg.getReply(), replyContext.getCurrentViewId(),
//                        request.getReqType());
//
//                if (replyContext.getNumRepliers() > 0) {
//                    bftsmart.tom.util.Logger.println("(ServiceReplica.receiveMessages) sending reply to "
//                            + request.getSender() + " with sequence number " + request.getSequence()
//                            + " and operation ID " + request.getOperationId() + " via ReplyManager");
//                    replyContext.getRepMan().send(request);
//                } else {
//                    bftsmart.tom.util.Logger.println("(ServiceReplica.receiveMessages) sending reply to "
//                            + request.getSender() + " with sequence number " + request.getSequence()
//                            + " and operation ID " + request.getOperationId());
//                    replyContext.getReplier().manageReply(request, msg.getMessageContext());
//                }
//                replyIndex++;
//            }
//        });
    }

    /**
     * Used by consensus write phase, pre compute new block hash
     */
    public BatchAppResultImpl preComputeAppHash(byte[][] commands) {

        List<AsyncFuture<byte[]>> asyncFutureLinkedList = new ArrayList<>(commands.length);
        List<byte[]> responseLinkedList = new ArrayList<>();
        StateSnapshot newStateSnapshot = null;
        StateSnapshot preStateSnapshot = null;
        StateSnapshot genisStateSnapshot = null;
        BatchAppResultImpl result = null;
        String batchId = null;
        int msgId = 0;

        try {

            batchId = messageHandle.beginBatch(realmName);
            genisStateSnapshot = messageHandle.getGenisStateSnapshot(realmName);
            preStateSnapshot = messageHandle.getStateSnapshot(realmName);

            if (preStateSnapshot == null) {
               throw new IllegalStateException("Pre block state snapshot is null!");
            }

            for (int i = 0; i < commands.length; i++) {
                byte[] txContent = commands[i];
                AsyncFuture<byte[]> asyncFuture = messageHandle.processOrdered(msgId++, txContent, realmName, batchId);
                asyncFutureLinkedList.add(asyncFuture);
            }
            newStateSnapshot = messageHandle.completeBatch(realmName, batchId);

            for (int i = 0; i < asyncFutureLinkedList.size(); i++) {
                responseLinkedList.add(asyncFutureLinkedList.get(i).get());
            }

            result = new BatchAppResultImpl(responseLinkedList, newStateSnapshot.getSnapshot(), batchId, genisStateSnapshot.getSnapshot());
            result.setErrorCode((byte) 0);

        } catch (Exception e) {
            LOGGER.error("Error occurred while pre compute app! --" + e.getMessage(), e);
            for (int i = 0; i < commands.length; i++) {
                responseLinkedList.add(createAppResponse(commands[i],TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK));
            }

            result = new BatchAppResultImpl(responseLinkedList,preStateSnapshot.getSnapshot(), batchId, genisStateSnapshot.getSnapshot());
            result.setErrorCode((byte) 1);
        }

        return result;
    }

    // Block full rollback responses, generated in pre compute phase, due to tx exception
    private byte[] createAppResponse(byte[] command, TransactionState transactionState) {
        TransactionRequest txRequest = BinaryProtocol.decode(command);

        TxResponseMessage resp = new TxResponseMessage(txRequest.getTransactionContent().getHash());

        resp.setExecutionState(transactionState);

        return BinaryProtocol.encode(resp, TransactionResponse.class);
    }

    public List<byte[]> updateAppResponses(List<byte[]> asyncResponseLinkedList, byte[] commonHash, boolean isConsistent) {
        List<byte[]> updatedResponses = new ArrayList<>();
        TxResponseMessage resp = null;

        for(int i = 0; i < asyncResponseLinkedList.size(); i++) {
            TransactionResponse txResponse = BinaryProtocol.decode(asyncResponseLinkedList.get(i));
            if (isConsistent) {
                resp = new TxResponseMessage(txResponse.getContentHash());
            }
            else {
                resp = new TxResponseMessage(new HashDigest(commonHash));
            }
            resp.setExecutionState(TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK);
            updatedResponses.add(BinaryProtocol.encode(resp, TransactionResponse.class));
    }
    return updatedResponses;
}
    /**
     *
     *  Decision has been made at the consensus stage， commit block
     *
     */
    public void preComputeAppCommit(String batchId) {
        try {
            messageHandle.commitBatch(realmName, batchId);
        } catch (BlockRollbackException e) {
            LOGGER.error("Error occurred while pre compute commit --" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     *
     *  Consensus write phase will terminate, new block hash values are inconsistent, rollback block
     *
     */
    public void preComputeAppRollback(String batchId) {
        messageHandle.rollbackBatch(realmName, batchId, TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK.CODE);
        LOGGER.debug("Rollback of operations that cause inconsistencies in the ledger");
    }

    //notice
    public byte[] getSnapshot() {
        LOGGER.debug("------- GetSnapshot...[replica.id=" + this.getId() + "]");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BytesUtils.writeInt(stateHandles.size(), out);
        for (StateHandle stateHandle : stateHandles) {
            // TODO: 测试代码；
            return stateHandle.takeSnapshot();
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

    enum Status {

        STARTING,

        RUNNING,

        STOPPING,

        STOPPED

    }

}
