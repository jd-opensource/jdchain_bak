/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.capability.CapabilitySettings
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/26 下午5:38
 * Description:
 */
package com.jd.blockchain.capability.settings;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/26
 * @since 1.0.0
 */

public class CapabilitySettings {

    public static final int TX_TOTAL_SIZE = 100 * 1000 * 1000;
//    public static final int TX_TOTAL_SIZE = 100 * 1000;

    public static final int TX_HALF_SIZE = 50 * 1000 * 1000;

    public static final int DR_SIZE = 10000;

    public static final int KV_SIZE = 10000;

    public static final int KV_TOTAL_SIZE = 100 * 1000 * 1000;

//    public static String MSG_QUEUE_URL = "nats://127.0.0.1:4222";
    public static String MSG_QUEUE_URL = "rabbit://127.0.0.1:5672";

//    public static String TX_TOPIC = "tx-topic";
    public static String TX_TOPIC = "tx-two-topic";

    public static final int TX_SIZE_PER_SEND = 10000;

    public static HashDigest ledgerHash;

    public static AsymmetricKeypair adminKey;

    public static final String settingsConf = "settings.conf";
}