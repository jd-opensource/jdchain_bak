/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.config.MsgQueueSettingsFactory
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 上午11:49
 * Description:
 */
package com.jd.blockchain.consensus.mq.config;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.ClientIncomingSettings;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consensus.SettingsFactory;
import com.jd.blockchain.consensus.mq.MsgQueueConsensusSettingsBuilder;
import com.jd.blockchain.consensus.mq.settings.MsgQueueBlockSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueClientIncomingSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueConsensusSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueNetworkSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueNodeSettings;
import com.jd.blockchain.utils.io.BytesEncoder;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueSettingsFactory implements SettingsFactory {

    static {
        DataContractRegistry.register(NodeSettings.class);

        DataContractRegistry.register(MsgQueueNodeSettings.class);

        DataContractRegistry.register(ConsensusSettings.class);

        DataContractRegistry.register(MsgQueueConsensusSettings.class);

        DataContractRegistry.register(MsgQueueNetworkSettings.class);

        DataContractRegistry.register(MsgQueueBlockSettings.class);

        DataContractRegistry.register(MsgQueueClientIncomingSettings.class);

        DataContractRegistry.register(ClientIncomingSettings.class);
    }

    private static final MsgQueueConsensusSettingsEncoder MQCS_ENCODER = new MsgQueueConsensusSettingsEncoder();

    private static final MsgQueueClientIncomingSettingsEncoder MQCIS_ENCODER = new MsgQueueClientIncomingSettingsEncoder();

    private static final MsgQueueConsensusSettingsBuilder BUILDER = new MsgQueueConsensusSettingsBuilder();

    @Override
    public MsgQueueConsensusSettingsBuilder getConsensusSettingsBuilder() {
        return BUILDER;
    }

    @Override
    public BytesEncoder<ConsensusSettings> getConsensusSettingsEncoder() {
        return MQCS_ENCODER;
    }

    @Override
    public BytesEncoder<ClientIncomingSettings> getIncomingSettingsEncoder() {
        return MQCIS_ENCODER;
    }

    private static class MsgQueueConsensusSettingsEncoder implements BytesEncoder<ConsensusSettings>{

        @Override
        public byte[] encode(ConsensusSettings data) {
            if (data instanceof MsgQueueConsensusSettings) {
                return BinaryProtocol.encode(data, MsgQueueConsensusSettings.class);
            }
            throw new IllegalArgumentException("Settings data isn't supported! Accept MsgQueueConsensusSettings only!");
        }

        @Override
        public MsgQueueConsensusSettings decode(byte[] bytes) {
            return BinaryProtocol.decodeAs(bytes, MsgQueueConsensusSettings.class);
        }
    }

    private static class MsgQueueClientIncomingSettingsEncoder implements BytesEncoder<ClientIncomingSettings>{

        @Override
        public byte[] encode(ClientIncomingSettings data) {
            if (data instanceof MsgQueueClientIncomingSettings) {
                return BinaryProtocol.encode(data, MsgQueueClientIncomingSettings.class);
            }
            throw new IllegalArgumentException("Settings data isn't supported! Accept MsgQueueClientIncomingSettings only!");
        }

        @Override
        public MsgQueueClientIncomingSettings decode(byte[] bytes) {
            return BinaryProtocol.decodeAs(bytes, MsgQueueClientIncomingSettings.class);
        }

    }
}