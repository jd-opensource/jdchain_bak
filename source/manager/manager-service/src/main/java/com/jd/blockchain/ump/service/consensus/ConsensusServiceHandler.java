package com.jd.blockchain.ump.service.consensus;

import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.util.Base58Utils;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConsensusServiceHandler implements ConsensusService {

    private static final String PATH_INNER = "/";

    private static final Map<String, ConsensusProvider> CONSENSUS_PROVIDERS = new ConcurrentHashMap<>();

    static {
        try {
            initProviders();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String initConsensusConf(String consensusProvider, List<PeerLocalConfig> sharedConfigs) {
        // 首先根据provider获取对应的配置信息
        ConsensusProvider provider = CONSENSUS_PROVIDERS.get(consensusProvider);

        if (provider == null) {
            throw new IllegalStateException(
                    String.format("ConsensusProvider[%s] can not find Manage-Class !!!", consensusProvider));
        }

        byte[] result = provider.handleSharedConfigs(sharedConfigs);

        return Base58Utils.encode(result);
    }

    private static void initProviders() {
        // 初始化所有实现类
        Reflections reflections = new Reflections("com.jd.blockchain.ump.service.consensus");

        Set<Class<? extends ConsensusProvider>> providerSet =
                reflections.getSubTypesOf(ConsensusProvider.class);

        for (Class<? extends ConsensusProvider> clazz : providerSet) {

            if (!clazz.isInterface()) {
                try {
                    // 根据class生成对象
                    ConsensusProvider provider = clazz.newInstance();
                    String providerKey = provider.provider();
                    if (providerKey != null && providerKey.length() > 0 &&
                            !CONSENSUS_PROVIDERS.containsKey(providerKey)) {

                        // 根据value读取配置文件中的内容
                        InputStream currentFileInputStream = ConsensusServiceHandler.class.getResourceAsStream(
                                PATH_INNER + provider.configFilePath());

                        Properties currentProps = new Properties();

                        currentProps.load(currentFileInputStream);

                        provider.setConfig(currentProps);

                        CONSENSUS_PROVIDERS.put(providerKey, provider);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }
}
