/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.intgr.LedgerInitConsensusConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/21 下午5:52
 * Description:
 */
package test.com.jd.blockchain.intgr;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.utils.io.FileUtils;

import test.com.jd.blockchain.intgr.perf.LedgerPerformanceTest;

import java.io.File;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/21
 * @since 1.0.0
 */

public class LedgerInitConsensusConfig {

    public static ConsensusConfig mqConfig = new ConsensusConfig();

    public static ConsensusConfig bftsmartConfig = new ConsensusConfig();

    public static String[] redisConnectionStrings = new String[4];

    public static String[] memConnectionStrings = new String[4];

    public static String[] rocksdbConnectionStrings = new String[4];

    public static String[] rocksdbDirStrings = new String[4];

    public static String[] mqProvider = new String[1];

    public static String[] bftsmartProvider = new String[1];

    public static String[] mqAndbftsmartProvider = new String[2];

    static {
        mqConfig.provider = "com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider";
        mqConfig.configPath = "mq.config";

        bftsmartConfig.provider = "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider";
        bftsmartConfig.configPath = "bftsmart.config";

//        for (int i = 0; i < redisConnectionStrings.length; i++) {
//            redisConnectionStrings[i] = "redis://127.0.0.1:6379/" + i;
//        }
        redisConnectionStrings[0] = "redis://192.168.54.112:6379";
        redisConnectionStrings[1] = "redis://192.168.54.112:6379/1";
        redisConnectionStrings[2] = "redis://192.168.54.112:6379/2";
        redisConnectionStrings[3] = "redis://192.168.54.112:6379/3";

        for (int i = 0; i < memConnectionStrings.length; i++) {
            memConnectionStrings[i] = "memory://local/" + i;
        }

        mqProvider[0] = mqConfig.provider;
        bftsmartProvider[0] = bftsmartConfig.provider;

        mqAndbftsmartProvider[0] = mqConfig.provider;
        mqAndbftsmartProvider[1] = bftsmartConfig.provider;

        for (int i = 0; i < rocksdbConnectionStrings.length; i++) {
            String currDir = FileUtils.getCurrentDir() + File.separator + "rocks.db";
            String dbDir = new File(currDir, "rocksdb" + i + ".db").getAbsolutePath();
            rocksdbDirStrings[i] = dbDir;
            rocksdbConnectionStrings[i] = "rocksdb://" + dbDir;
        }
    }

    public static ConsensusProvider getConsensusProvider(String providerName) {
        return ConsensusProviders.getProvider(providerName);
    }

    public static class ConsensusConfig {

        String provider;

        String configPath;

        public String getProvider() {
            return provider;
        }

        public String getConfigPath() {
            return configPath;
        }
    }
}