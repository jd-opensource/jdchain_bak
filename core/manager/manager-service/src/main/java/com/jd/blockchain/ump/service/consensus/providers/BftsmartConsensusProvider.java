package com.jd.blockchain.ump.service.consensus.providers;


import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.model.config.PeerSharedConfig;
import com.jd.blockchain.ump.service.consensus.ConsensusProvider;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class BftsmartConsensusProvider implements ConsensusProvider {

    public static final String BFTSMART_PROVIDER = "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider";

    public static final String BFTSMART_CONFIG_FILE = "bftsmart.default.config";

    private static final int MIN_PARTI_SIZE = 4;

    private Properties bftsmartProps;

    @Override
    public String provider() {
        return BFTSMART_PROVIDER;
    }

    @Override
    public String configFilePath() {
        return BFTSMART_CONFIG_FILE;
    }

    @Override
    public void setConfig(Properties properties) {
        bftsmartProps = properties;
    }

    @Override
    public Properties getConfig() {
        return bftsmartProps;
    }

    @Override
    public byte[] handleSharedConfigs(List<PeerLocalConfig> sharedConfigs) {

        // 首先校验其中的ConsensusNode是否完全一致，若完全一致则不可以
        verify(sharedConfigs);

        StringBuilder sBuilder = new StringBuilder();

        // 先加入当前节点信息
        List<String> nodeConfigs = nodeConfigs(sharedConfigs);

        for (String nodeConfig : nodeConfigs) {
            sBuilder.append(nodeConfig).append(NEXT_LINE);
        }

        int nodeNum = sharedConfigs.size();

        // 写入之前配置文件中的内容
        for (Map.Entry<Object, Object> entry : bftsmartProps.entrySet()) {

            // 获取Key-Value
            String key = (String) entry.getKey(), value = (String) entry.getValue();

            // 对特殊的Key和Value单独处理
            /**
             * system.servers.num = 4
             *
             * system.servers.f = 1
             *
             * system.initial.view = 0,1,2,3
             */
            if (key.startsWith(BftsmartConstant.SERVERS_NUM_PREFIX)) {

                sBuilder.append(BftsmartConstant.SERVERS_NUM_PREFIX + " = " + nodeNum).append(NEXT_LINE);
            } else if (key.startsWith(BftsmartConstant.SERVERS_F_PREFIX)) {

                sBuilder.append(BftsmartConstant.SERVERS_F_PREFIX + " = " + nodeFNum(nodeNum)).append(NEXT_LINE);
            } else if (key.startsWith(BftsmartConstant.INIT_VIEW_PREFIX)) {

                sBuilder.append(BftsmartConstant.INIT_VIEW_PREFIX + " = " + initView(nodeNum)).append(NEXT_LINE);
            } else {

                sBuilder.append(key + " = " + value).append(NEXT_LINE);
            }
        }

        return sBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String initView(int nodeNum) {

        StringBuilder views = new StringBuilder();

        for (int i = 0; i < nodeNum; i++) {
            if (views.length() > 0) {
                views.append(",");
            }
            views.append(i);
        }
        return views.toString();
    }

    private void verify(List<PeerLocalConfig> sharedConfigs) {

        Set<String> consensusInfos = new HashSet<>();

        if (sharedConfigs == null) {
            throw new IllegalStateException("Shared Configs is NULL !!!");
        }

        if (sharedConfigs.size() < MIN_PARTI_SIZE) {
            throw new IllegalStateException(
                    String.format("Shared Configs's size = %s, can not meet minimum %s !!!",
                            sharedConfigs.size(), MIN_PARTI_SIZE));
        }

        for (PeerLocalConfig sharedConfig : sharedConfigs) {
            String consensusInfo = sharedConfig.getConsensusNode();
            if (consensusInfos.contains(consensusInfo)) {
                throw new IllegalStateException("Shared Configs's Consensus may be conflict !!!");
            }
            consensusInfos.add(consensusInfo);
        }
    }

    private List<String> nodeConfigs(List<PeerLocalConfig> sharedConfigs) {

        List<String> nodeConfigs = new ArrayList<>();

        if (sharedConfigs != null && !sharedConfigs.isEmpty()) {
            for (int i = 0; i < sharedConfigs.size(); i++) {

                PeerSharedConfig sharedConfig = sharedConfigs.get(i);

                String consensusNode = sharedConfig.getConsensusNode();

                String[] hostAndPort = consensusNode.split(":");

                nodeConfigs.add(String.format(BftsmartConstant.HOST_FORMAT, i, hostAndPort[0]));

                nodeConfigs.add(String.format(BftsmartConstant.PORT_FORMAT, i, hostAndPort[1]));

                nodeConfigs.add(String.format(BftsmartConstant.SECURE_FORMAT, i, false));

            }
        }

        return nodeConfigs;
    }

    private int nodeFNum(int nodeNum) {
        /**
         * 3F+1
         *
         * 假设有4个节点，则可有一个，若有N个，则N-1/3
         */
        if (nodeNum < 4) {
            return 0;
        }
        return (nodeNum - 1) / 3;
    }
}
