package com.jd.blockchain.consensus.bftsmart.service;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bftsmart.consensus.app.BatchAppResultImpl;
import bftsmart.reconfiguration.views.View;
import bftsmart.tom.*;
import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.consensus.service.*;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.transaction.TxResponseMessage;
import com.jd.blockchain.utils.StringUtils;
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
import org.springframework.util.NumberUtils;

public class BftsmartNodeServer extends DefaultRecoverable implements NodeServer {

    private static Logger LOGGER = LoggerFactory.getLogger(BftsmartNodeServer.class);

    private final Lock cmdHandleLock = new ReentrantLock();

    private List<StateHandle> stateHandles = new CopyOnWriteArrayList<>();

    private volatile Status status = Status.STOPPED;

    private final Object mutex = new Object();

    private volatile ServiceReplica replica;

    private StateMachineReplicate stateMachineReplicate;

    private ServerSettings serverSettings;

    private BftsmartConsensusManageService manageService;

    private volatile BftsmartTopology topology;

    private volatile BftsmartTopology outerTopology;

    private volatile BftsmartConsensusSettings setting;

    private volatile InnerStateHolder stateHolder;

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
        this.stateHolder = new InnerStateHolder(this.stateMachineReplicate.getLatestStateID(realmName));
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
    }

    protected void initConfig(int id, Properties systemsConfig, HostsConfig hostConfig) {
        byte[] serialHostConf = BinarySerializeUtils.serialize(hostConfig);
        Properties sysConfClone = (Properties)systemsConfig.clone();
        int port = hostConfig.getPort(id);
//        hostConfig.add(id, DEFAULT_BINDING_HOST, port);

        //if ledger-init.sh set up the -DhostIp=xxx, then get it;
        String preHostPort = System.getProperty("hostPort");
        if(!StringUtils.isEmpty(preHostPort)){
            port = NumberUtils.parseNumber(preHostPort, Integer.class);
            LOGGER.info("###peer-startup.sh###,set up the -DhostPort="+port);
        }

        String preHostIp = System.getProperty("hostIp");
        if(!StringUtils.isEmpty(preHostIp)){
            hostConfig.add(id, preHostIp, port);
            LOGGER.info("###peer-startup.sh###,set up the -DhostIp="+preHostIp);
        }

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
        if (!isRunning()) {
            return null;
        }
        if (outerTopology != null) {
            return outerTopology;
        }
        return topology;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean isRunning() {
        return status == Status.RUNNING;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] bytes, MessageContext messageContext) {
        return messageHandle.processUnordered(bytes).get();
    }

    /**
     *
     *  Only block, no reply， used by state transfer when peer start
     *
     */
    private void block(byte[][] manageConsensusCmds) {
        String batchId = messageHandle.beginBatch(realmName);
        try {
            int msgId = 0;
            for (byte[] txContent : manageConsensusCmds) {
                messageHandle.processOrdered(msgId++, txContent, realmName, batchId);
            }
            messageHandle.completeBatch(realmName, batchId);
            messageHandle.commitBatch(realmName, batchId);
        } catch (Exception e) {
            LOGGER.error("Error occurred while processing ordered messages! --" + e.getMessage(), e);
            messageHandle.rollbackBatch(realmName, batchId, TransactionState.CONSENSUS_ERROR.CODE);
        }
    }

    /**
     *  Local peer has cid diff with remote peer, used by state transfer when peer start
     *  每个传入的commands都是一个确定的batch，不会出现跨batch或多batch的情况
     *
     */
    private byte[][] stateTransferMsgExecute(byte[][] commands, MessageContext[] msgCtxs) {
        cmdHandleLock.lock();
        try {
            int currHandleCid = msgCtxs[0].getConsensusId(); // 同一批次，获取其中之一即可
            long lastCid = stateHolder.lastCid, currentCid = stateHolder.currentCid;
            if (currHandleCid <= lastCid) {
                // 表示该CID已经执行过，不再处理
                // 状态传输消息返回null即可
                LOGGER.warn("State transfer message is repeated, ledger's lastCID = {}, protocol's cid = {}", lastCid, currHandleCid);
                return null;
            } else if (currHandleCid == lastCid + 1) {
                // 有可能处理正在执行中的状态，需要判断是否新的开始执行
                if (currHandleCid == currentCid) {
                    // 表示在执行，那么将执行的结果回滚并重新执行
                    String batchingID = stateHolder.batchingID;
                    messageHandle.rollbackBatch(realmName, batchingID, TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK.CODE);
                }
                // 执行即可
                block(commands);
                // 重置上轮ID
                stateHolder.setLastCid(currHandleCid);
                stateHolder.reset();
            } else {
                // 因为跨checkpoint导致进行消息重放时丢失了部分数据
                // 此时打印日志，等待数据copy，不能再往后执行，否则会污染整个账本的数据
                LOGGER.error("State transfer message execute error, ledger's lastCID = {}, protocol's cid = {}", lastCid, currHandleCid);
                return null;
            }
        } finally {
            cmdHandleLock.unlock();
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
            // 表示从状态传输接入
            return stateTransferMsgExecute(commands, msgCtxs);
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
    @Override
    public BatchAppResultImpl preComputeAppHash(int cid, byte[][] commands) {

        List<AsyncFuture<byte[]>> asyncFutureLinkedList = new ArrayList<>(commands.length);
        List<byte[]> responseLinkedList = new ArrayList<>();
        StateSnapshot newStateSnapshot = null;
        StateSnapshot preStateSnapshot = null;
        StateSnapshot genisStateSnapshot = null;
        BatchAppResultImpl result = null;
        String batchId = null;
        int msgId = 0;
        cmdHandleLock.lock();
        try {
            long lastCid = stateHolder.lastCid, currentCid = stateHolder.currentCid;
            if (cid <= lastCid) {
                // 表示该CID已经执行过，返回错误，需要协议层处理
                LOGGER.warn("Prepare compute phase message is repeated, ledger's lastCID = {}, protocol's cid = {}", lastCid, cid);
                return unExecuteMsgResult(cid, commands);
            } else if (cid == lastCid + 1) {
                // 需要判断之前二阶段是否执行过
                if (cid == currentCid) {
                    // 表示二阶段已执行,回滚，重新执行
                    String batchingID = stateHolder.batchingID;
                    messageHandle.rollbackBatch(realmName, batchingID, TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK.CODE);
                }
            } else {
                // 协议层没有控制住，导致比当前cid要大的值进入，此时打印日志，不可执行，防止对现有数据造成污染
                LOGGER.error("Prepare compute phase execute error, ledger's lastCID = {}, protocol's cid = {}", lastCid, cid);
                return unExecuteMsgResult(cid, commands);
            }
            stateHolder.currentCid = cid;
            batchId = messageHandle.beginBatch(realmName);
            stateHolder.batchingID = batchId;
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

            for (AsyncFuture<byte[]> asyncFuture : asyncFutureLinkedList) {
                responseLinkedList.add(asyncFuture.get());
            }

            result = new BatchAppResultImpl(responseLinkedList, newStateSnapshot.getSnapshot(), batchId, genisStateSnapshot.getSnapshot());
            result.setErrorCode((byte) 0);
        } catch (BlockRollbackException e) {
            LOGGER.error("Error occurred while pre compute app! --" + e.getMessage(), e);
            for (byte[] command : commands) {
                responseLinkedList.add(createAppResponse(command, e.getState()));
            }

            result = new BatchAppResultImpl(responseLinkedList, preStateSnapshot.getSnapshot(), batchId, genisStateSnapshot.getSnapshot());
            result.setErrorCode((byte) 1);
        }catch (Exception e) {
            LOGGER.error("Error occurred while pre compute app! --" + e.getMessage(), e);
            for (byte[] command : commands) {
                responseLinkedList.add(createAppResponse(command, TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK));
            }

            result = new BatchAppResultImpl(responseLinkedList, preStateSnapshot.getSnapshot(), batchId, genisStateSnapshot.getSnapshot());
            result.setErrorCode((byte) 1);
        } finally {
            cmdHandleLock.unlock();
        }

        return result;
    }

    /**
     * 未执行共识消息的情况下统一的返回结果
     *           该入口只会在正常执行二阶段，但该二阶段的消息已经执行过或因为lastCID不合理导致无法执行的情况
     *
     * @param cid
     *           共识ID
     *
     * @param commands
     *           消息列表
     * @return
     */
    private BatchAppResultImpl unExecuteMsgResult(int cid, byte[][] commands) {
        List<byte[]> responseLinkedList = new ArrayList<>(commands.length);
        for (byte[] command : commands) {
            responseLinkedList.add(createAppResponse(command, TransactionState.SYSTEM_ERROR));
        }
        byte[] cidBytes = BytesUtils.toBytes(cid);
        BatchAppResultImpl result = new BatchAppResultImpl(responseLinkedList, cidBytes, "", cidBytes);
        result.setErrorCode((byte) 1);
        return result;
    }

    // Block full rollback responses, generated in pre compute phase, due to tx exception
    private byte[] createAppResponse(byte[] command, TransactionState transactionState) {
        TransactionRequest txRequest = BinaryProtocol.decode(command);
        TxResponseMessage resp = new TxResponseMessage(txRequest.getTransactionContent().getHash());
        resp.setExecutionState(transactionState);
        return BinaryProtocol.encode(resp, TransactionResponse.class);
    }

    @Override
    public List<byte[]> updateAppResponses(List<byte[]> asyncResponseLinkedList, byte[] commonHash, boolean isConsistent) {
        List<byte[]> updatedResponses = new ArrayList<>();
        TxResponseMessage resp = null;

        for(int i = 0; i < asyncResponseLinkedList.size(); i++) {
            TransactionResponse txResponse = BinaryProtocol.decode(asyncResponseLinkedList.get(i));
            if (isConsistent) {
                resp = new TxResponseMessage(txResponse.getContentHash());
            } else {
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
    @Override
    public void preComputeAppCommit(int cid, String batchId) {
        cmdHandleLock.lock();
        try {
            long lastCid = stateHolder.lastCid;
            if (cid <= lastCid) {
                // 表示该CID已经执行过，不再处理
                LOGGER.warn("Prepare compute commit phase is repeat, ledger's lastCid = {}, protocol's cid = {}", lastCid, cid);
                return;
            } else if (cid > lastCid + 1) {
                LOGGER.error("Prepare compute commit phase is error, ledger's lastCid = {}, protocol's cid = {}", lastCid, cid);
                return;
            }
            stateHolder.setLastCid(cid);
            stateHolder.reset();
            messageHandle.commitBatch(realmName, batchId);
        } catch (BlockRollbackException e) {
            LOGGER.error("Error occurred while pre compute commit --" + e.getMessage(), e);
            throw e;
        } finally {
            cmdHandleLock.unlock();
        }
    }

    /**
     *
     *  Consensus write phase will terminate, new block hash values are inconsistent, rollback block
     *
     */
    @Override
    public void preComputeAppRollback(int cid, String batchId) {
        cmdHandleLock.lock();
        try {
            long lastCid = stateHolder.lastCid;
            if (cid <= lastCid) {
                // 表示该CID已经执行过，不再处理
                LOGGER.warn("Prepare compute rollback phase is repeat, ledger's lastCid = {}, protocol's cid = {}", lastCid, cid);
                return;
            } else if (cid > lastCid + 1) {
                LOGGER.error("Prepare compute rollback phase is error, ledger's lastCid = {}, protocol's cid = {}", lastCid, cid);
                return;
            }
            stateHolder.setLastCid(cid);
            stateHolder.reset();
            messageHandle.rollbackBatch(realmName, batchId, TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK.CODE);
            LOGGER.debug("Rollback of operations that cause inconsistencies in the ledger");
        } finally {
            cmdHandleLock.unlock();
        }
    }

    //notice
    @Override
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

    @Override
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
                this.replica = new ServiceReplica(tomConfig, this, this, (int)(this.stateHolder.lastCid));
                this.topology = new BftsmartTopology(replica.getReplicaContext().getCurrentView());
                initOutTopology();
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

    private void initOutTopology() {
        View currView = this.topology.getView();
        int id = currView.getId();
        int curProcessId = tomConfig.getProcessId();
        int f = currView.getF();
        int[] processes = currView.getProcesses();
        InetSocketAddress[] addresses = new InetSocketAddress[processes.length];
        for (int i = 0; i < processes.length; i++) {
            int pid = processes[i];
            if (curProcessId == pid) {
                LOGGER.debug("outerTomConfig in current node, my viewId = {} , my process id = {}, host = {}, port = {} \r\n", id, pid, this.outerTomConfig.getHost(pid), this.outerTomConfig.getPort(pid));
                addresses[i] = new InetSocketAddress(this.outerTomConfig.getHost(pid), this.outerTomConfig.getPort(pid));
            } else {
                addresses[i] = currView.getAddress(pid);
            }
            LOGGER.debug("list tomConfig, viewId = {},  process id = {}, address = {} \r\n", id, pid, addresses[i]);
        }
        View returnView = new View(id, processes, f, addresses);
        this.outerTopology = new BftsmartTopology(returnView);
    }

    enum Status {

        STARTING,

        RUNNING,

        STOPPING,

        STOPPED

    }

    public static class InnerStateHolder {

        private long lastCid;

        private long currentCid = -1L;

        private String batchingID = "";

        public InnerStateHolder(long lastCid) {
            this.lastCid = lastCid;
        }

        public InnerStateHolder(long lastCid, long currentCid) {
            this.lastCid = lastCid;
            this.currentCid = currentCid;
        }

        public long getLastCid() {
            return lastCid;
        }

        public void setLastCid(long lastCid) {
            this.lastCid = lastCid;
        }

        public long getCurrentCid() {
            return currentCid;
        }

        public void setCurrentCid(long currentCid) {
            this.currentCid = currentCid;
        }

        public String getBatchingID() {
            return batchingID;
        }

        public void setBatchingID(String batchingID) {
            this.batchingID = batchingID;
        }

        public void reset() {
            currentCid = -1;
            batchingID = "";
        }
    }

}
