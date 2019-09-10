/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.MsgQueueConsensusSettingsBuilder
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 下午1:46
 * Description:
 */
package com.jd.blockchain.consensus.mq;

import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.ConsensusSettingsBuilder;
import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.mq.config.MsgQueueBlockConfig;
import com.jd.blockchain.consensus.mq.config.MsgQueueConsensusConfig;
import com.jd.blockchain.consensus.mq.config.MsgQueueNetworkConfig;
import com.jd.blockchain.consensus.mq.config.MsgQueueNodeConfig;
import com.jd.blockchain.consensus.mq.settings.MsgQueueBlockSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueConsensusSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueNetworkSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueNodeSettings;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.ParticipantInfo;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.PropertiesUtils;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesEncoder;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueConsensusSettingsBuilder implements ConsensusSettingsBuilder {

    private static final String DEFAULT_TOPIC_TX = "tx-topic";

    private static final String DEFAULT_TOPIC_BL = "bl-topic";

    private static final String DEFAULT_TOPIC_MSG = "msg-topic";

    private static final int DEFAULT_TXSIZE = 1000;

    private static final int DEFAULT_MAXDELAY = 1000;

    /**
     *
     */
    private static final String CONFIG_TEMPLATE_FILE = "mq.config";

    /**
     * 参数键：节点数量；
     */
    public static final String SERVER_NUM_KEY = "system.servers.num";

    /**
     * 参数键格式：节点公钥；
     */
    public static final String PUBKEY_PATTERN = "system.server.%s.pubkey";

    public static final String MSG_QUEUE_SERVER = "system.msg.queue.server";

    public static final String MSG_QUEUE_TOPIC_TX = "system.msg.queue.topic.tx";

    public static final String MSG_QUEUE_TOPIC_BL = "system.msg.queue.topic.bl";

    public static final String MSG_QUEUE_TOPIC_MSG = "system.msg.queue.topic.msg";

    public static final String MSG_QUEUE_BLOCK_TXSIZE = "system.msg.queue.block.txsize";

    public static final String MSG_QUEUE_BLOCK_MAXDELAY = "system.msg.queue.block.maxdelay";

    public static final  String  MSG_QUEUE_PROVIDER = "com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider";

    private static Properties CONFIG_TEMPLATE;

    static {
        if (FileUtils.existFile(CONFIG_TEMPLATE_FILE)) {
            ClassPathResource configResource = new ClassPathResource(CONFIG_TEMPLATE_FILE);
            try {
                try (InputStream in = configResource.getInputStream()) {
                    CONFIG_TEMPLATE = PropertiesUtils.load(in, BytesUtils.DEFAULT_CHARSET);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Override
    public MsgQueueConsensusSettings createSettings(Properties props, ParticipantNode[] participantNodes) {
        MsgQueueNetworkConfig networkConfig = new MsgQueueNetworkConfig();
        Properties resolvingProps = PropertiesUtils.cloneFrom(props);

        String server = PropertiesUtils.getProperty(resolvingProps, MSG_QUEUE_SERVER, true);
        if (server == null || server.length()<= 0) {
            throw new IllegalArgumentException(String.format("Property[%s] is empty!", MSG_QUEUE_SERVER));
        }
        networkConfig.setServer(server)
                .setTxTopic(initProp(resolvingProps, MSG_QUEUE_TOPIC_TX, DEFAULT_TOPIC_TX))
                .setBlTopic(initProp(resolvingProps, MSG_QUEUE_TOPIC_BL, DEFAULT_TOPIC_BL))
                .setMsgTopic(initProp(resolvingProps, MSG_QUEUE_TOPIC_MSG, DEFAULT_TOPIC_MSG))
                ;

        MsgQueueBlockConfig blockConfig = new MsgQueueBlockConfig()
                .setTxSizePerBlock(initProp(resolvingProps, MSG_QUEUE_BLOCK_TXSIZE, DEFAULT_TXSIZE))
                .setMaxDelayMilliSecondsPerBlock(initProp(resolvingProps, MSG_QUEUE_BLOCK_MAXDELAY, DEFAULT_MAXDELAY))
                ;

        MsgQueueConsensusConfig consensusConfig = new MsgQueueConsensusConfig()
                .setBlockSettings(blockConfig)
                .setNetworkSettings(networkConfig)
                ;
        // load node settings
        int serversNum = PropertiesUtils.getInt(resolvingProps, SERVER_NUM_KEY);
        for (int i = 0; i < serversNum; i++) {
            int id = i;

            String keyOfPubkey = nodeKey(PUBKEY_PATTERN, id);

            String base58PubKey = PropertiesUtils.getRequiredProperty(resolvingProps, keyOfPubkey);
            PubKey pubKey = KeyGenUtils.decodePubKey(base58PubKey);

//            PubKey pubKey = new PubKey(Base58Utils.decode(base58PubKey));
            resolvingProps.remove(keyOfPubkey);
            Bytes address = AddressEncoding.generateAddress(pubKey);

            String networkAddress = address.toBase58();
            MsgQueueNodeConfig nodeConfig = new MsgQueueNodeConfig()
                    .setAddress(networkAddress)
                    .setPubKey(pubKey)
                    ;
            consensusConfig.addNodeSettings(nodeConfig);
        }
        return consensusConfig;
    }

    private MsgQueueNodeSettings[] nodeSettings(NodeSettings[] nodeSettings, ParticipantInfo participantInfo) {

        MsgQueueNodeSettings msgQueueNodeSettings = new MsgQueueNodeConfig();
        ((MsgQueueNodeConfig) msgQueueNodeSettings).setAddress(AddressEncoding.generateAddress(participantInfo.getPubKey()).toBase58());
        ((MsgQueueNodeConfig) msgQueueNodeSettings).setPubKey(participantInfo.getPubKey());

        MsgQueueNodeSettings[] msgQueuetNodeSettings = new MsgQueueNodeSettings[nodeSettings.length + 1];
        for (int i = 0; i < nodeSettings.length; i++) {
            msgQueuetNodeSettings[i] = (MsgQueueNodeSettings)nodeSettings[i];
        }
        msgQueuetNodeSettings[nodeSettings.length] = msgQueueNodeSettings;

        return msgQueuetNodeSettings;
    }

    @Override
    public Bytes updateSettings(Bytes oldConsensusSettings, ParticipantInfo participantInfo) {

        BytesEncoder<ConsensusSettings> consensusEncoder =  ConsensusProviders.getProvider(MSG_QUEUE_PROVIDER).getSettingsFactory().getConsensusSettingsEncoder();

        MsgQueueConsensusSettings consensusSettings = (MsgQueueConsensusSettings) consensusEncoder.decode(oldConsensusSettings.toBytes());

        MsgQueueNodeSettings[] nodeSettings = nodeSettings(consensusSettings.getNodes(), participantInfo);

        MsgQueueConsensusConfig msgQueueConsensusConfig = new MsgQueueConsensusConfig();
        for (int i = 0; i < nodeSettings.length; i++) {
            msgQueueConsensusConfig.addNodeSettings(nodeSettings[i]);
        }

        msgQueueConsensusConfig.setBlockSettings(consensusSettings.getBlockSettings());

        msgQueueConsensusConfig.setNetworkSettings(consensusSettings.getNetworkSettings());


//        for(int i = 0 ;i < msgQueueConsensusConfig.getNodes().length; i++) {
//            System.out.printf("node addr = %s\r\n", msgQueueConsensusConfig.getNodes()[i].getAddress());
//        }

        return new Bytes(consensusEncoder.encode(msgQueueConsensusConfig));

    }

    @Override
    public Properties createPropertiesTemplate() {
        return PropertiesUtils.cloneFrom(CONFIG_TEMPLATE);
    }

    @Override
    public void writeSettings(ConsensusSettings settings, Properties props) {

        if (!(settings instanceof MsgQueueConsensusSettings)) {
            throw new IllegalArgumentException("ConsensusSettings data isn't supported! Accept MsgQueueConsensusSettings only!");
        }

        MsgQueueConsensusSettings consensusSettings = (MsgQueueConsensusSettings) settings;

        MsgQueueNetworkSettings networkSettings = consensusSettings.getNetworkSettings();
        if (networkSettings == null || networkSettings.getServer() == null || networkSettings.getServer().length() <= 0) {
            throw new IllegalArgumentException("MsgQueue Consensus server is empty!");
        }

        String server = networkSettings.getServer();
        props.setProperty(MSG_QUEUE_SERVER, server);

        String txTopic = networkSettings.getTxTopic();
        if (txTopic == null || txTopic.length() <= 0) {
            txTopic = DEFAULT_TOPIC_TX;
        }
        props.setProperty(MSG_QUEUE_TOPIC_TX, txTopic);

        String blTopic = networkSettings.getBlTopic();
        if (blTopic == null || blTopic.length() <= 0) {
            blTopic = DEFAULT_TOPIC_BL;
        }
        props.setProperty(MSG_QUEUE_TOPIC_BL, blTopic);

        String msgTopic = networkSettings.getMsgTopic();
        if (msgTopic == null || msgTopic.length() <= 0) {
            msgTopic = DEFAULT_TOPIC_MSG;
        }
        props.setProperty(MSG_QUEUE_TOPIC_MSG, msgTopic);

        MsgQueueBlockSettings blockSettings = consensusSettings.getBlockSettings();
        if (blockSettings == null) {
            props.setProperty(MSG_QUEUE_BLOCK_TXSIZE, DEFAULT_TXSIZE + "");
            props.setProperty(MSG_QUEUE_BLOCK_MAXDELAY, DEFAULT_MAXDELAY + "");
        } else {
            int txSize = blockSettings.getTxSizePerBlock();
            long maxDelay = blockSettings.getMaxDelayMilliSecondsPerBlock();
            props.setProperty(MSG_QUEUE_BLOCK_TXSIZE, txSize + "");
            props.setProperty(MSG_QUEUE_BLOCK_MAXDELAY, maxDelay + "");
        }


//        int serversNum = PropertiesUtils.getInt(props, SERVER_NUM_KEY);
//        if (serversNum > 0) {
//            for (int i = 0; i < serversNum; i++) {
//                int id = i;
//                String keyOfPubkey = nodeKey(PUBKEY_PATTERN, id);
//                props.remove(keyOfPubkey);
//
//                String keyOfHost = nodeKey(CONSENSUS_HOST_PATTERN, id);
//                props.remove(keyOfHost);
//            }
//        }
//
//        NodeSettings[] nodesSettings = consensusSettings.getNodes();
//        serversNum = nodesSettings.length;
//        props.setProperty(SERVER_NUM_KEY, serversNum + "");
//
//        for (int i = 0; i < serversNum; i++) {
//            MsgQueueNodeSettings mqns = (MsgQueueNodeSettings) nodesSettings[i];
//            int id = i;
//            String keyOfPubkey = nodeKey(PUBKEY_PATTERN, id);
//            props.setProperty(keyOfPubkey, mqns.getPubKey().toBase58());
//
//            String keyOfHost = nodeKey(CONSENSUS_HOST_PATTERN, id);
//            props.setProperty(keyOfHost, mqns.getAddress() == null ? "" : mqns.getAddress());
//        }
    }

    private String initProp(Properties resolvingProps, String key, String defaultVal) {
        try {
            String value = PropertiesUtils.getProperty(resolvingProps, key, true);
            if (value == null || value.length() <= 0) {
                value = defaultVal;
            }
            return value;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private int initProp(Properties resolvingProps, String key, int defaultVal) {
        try {
            int value = PropertiesUtils.getInt(resolvingProps, key);
            if (value <= 0) {
                value = defaultVal;
            }
            return value;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private static String nodeKey(String pattern, int id) {
        return String.format(pattern, id);
    }
}