/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.server.MsgQueueNodeServer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/13 上午11:20
 * Description:
 */
package com.jd.blockchain.consensus.mq.server;

import com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider;
import com.jd.blockchain.consensus.mq.consumer.MsgQueueConsumer;
import com.jd.blockchain.consensus.mq.factory.MsgQueueFactory;
import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;
import com.jd.blockchain.consensus.mq.settings.MsgQueueBlockSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueConsensusSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueNetworkSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueServerSettings;
import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.consensus.service.NodeServer;
import com.jd.blockchain.consensus.service.StateMachineReplicate;

import java.util.concurrent.Executors;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/13
 * @since 1.0.0
 */

public class MsgQueueNodeServer implements NodeServer {

    private DefaultMsgQueueMessageDispatcher dispatcher;

    private ExtendMsgQueueMessageExecutor extendExecutor;

    private MessageHandle messageHandle;

    private StateMachineReplicate stateMachineReplicator;

    private MsgQueueMessageExecutor messageExecutor;

    private MsgQueueNetworkSettings networkSettings;

    private MsgQueueConsensusManageService manageService;

    private int txSizePerBlock = 1000;

    private long maxDelayMilliSecondsPerBlock = 1000;

    private MsgQueueServerSettings serverSettings;

    private boolean isRunning;

    public MsgQueueNodeServer setMessageHandle(MessageHandle messageHandle) {
        this.messageHandle = messageHandle;
        return this;
    }

    public MsgQueueNodeServer setStateMachineReplicator(StateMachineReplicate stateMachineReplicator) {
        this.stateMachineReplicator = stateMachineReplicator;
        return this;
    }

    public MsgQueueNodeServer setTxSizePerBlock(int txSizePerBlock) {
        this.txSizePerBlock = txSizePerBlock;
        return this;
    }

    public MsgQueueNodeServer setMaxDelayMilliSecondsPerBlock(long maxDelayMilliSecondsPerBlock) {
        this.maxDelayMilliSecondsPerBlock = maxDelayMilliSecondsPerBlock;
        return this;
    }

    public MsgQueueNodeServer setMsgQueueNetworkSettings(MsgQueueNetworkSettings networkSettings) {
        this.networkSettings = networkSettings;
        return this;
    }

    public MsgQueueNodeServer setServerSettings(MsgQueueServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        this.manageService = new MsgQueueConsensusManageService()
                                .setConsensusSettings(serverSettings.getConsensusSettings());
        return this;
    }

    public MsgQueueNodeServer init() {
        String realmName = this.serverSettings.getRealmName();
        MsgQueueBlockSettings blockSettings = this.serverSettings.getBlockSettings();
        MsgQueueConsensusSettings consensusSettings = this.serverSettings.getConsensusSettings();

        this.setTxSizePerBlock(blockSettings.getTxSizePerBlock())
            .setMaxDelayMilliSecondsPerBlock(blockSettings.getMaxDelayMilliSecondsPerBlock())
            .setMsgQueueNetworkSettings(consensusSettings.getNetworkSettings())
            ;

        String  server = networkSettings.getServer(),
               txTopic = networkSettings.getTxTopic(),
               blTopic = networkSettings.getBlTopic(),
              msgTopic = networkSettings.getMsgTopic();

        MsgQueueProducer blProducer = MsgQueueFactory.newProducer(server, blTopic),
                         txProducer = MsgQueueFactory.newProducer(server, txTopic),
                        msgProducer = MsgQueueFactory.newProducer(server, msgTopic);

        MsgQueueConsumer txConsumer = MsgQueueFactory.newConsumer(server, txTopic),
                        msgConsumer = MsgQueueFactory.newConsumer(server, msgTopic);

        initMessageExecutor(blProducer, realmName);

        initDispatcher(txProducer, txConsumer);

        initExtendExecutor(msgProducer, msgConsumer);

        return this;
    }

    @Override
    public String getProviderName() {
        return MsgQueueConsensusProvider.NAME;
    }

    @Override
    public MsgQueueConsensusManageService getManageService() {
        return this.manageService;
    }

    @Override
    public MsgQueueServerSettings getSettings() {
        return serverSettings;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized void start() {
        if (!isRunning) {
            try {
                dispatcher.connect();
                Executors.newSingleThreadExecutor().execute(dispatcher);
                extendExecutor.connect();
                Executors.newSingleThreadExecutor().execute(extendExecutor);
                isRunning = true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void stop() {
        if (isRunning) {
            try {
                dispatcher.stop();
                extendExecutor.stop();
                isRunning = false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initMessageExecutor(MsgQueueProducer blProducer, final String realmName) {
        messageExecutor = new MsgQueueMessageExecutor()
                .setRealmName(realmName)
                .setMessageHandle(messageHandle)
                .setBlProducer(blProducer)
                .setStateMachineReplicator(stateMachineReplicator)
                .setTxSizePerBlock(txSizePerBlock)
                .init()
        ;
    }

    private void initDispatcher(MsgQueueProducer txProducer, MsgQueueConsumer txConsumer) {
        dispatcher = new DefaultMsgQueueMessageDispatcher(txSizePerBlock, maxDelayMilliSecondsPerBlock)
                .setTxProducer(txProducer)
                .setTxConsumer(txConsumer)
                .setEventHandler(messageExecutor)
        ;
        dispatcher.init();
    }


    private void initExtendExecutor(MsgQueueProducer msgProducer, MsgQueueConsumer msgConsumer) {
        extendExecutor = new ExtendMsgQueueMessageExecutor()
                .setMessageHandle(messageHandle)
                .setMsgConsumer(msgConsumer)
                .setMsgProducer(msgProducer)
        ;
        extendExecutor.init();
    }
}