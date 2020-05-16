/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.samples.SDKDemo_RegisterUser
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/10/18 下午2:00
 * Description: 注册用户
 */
package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.ConsoleUtils;

/**
 * 注册用户
 * @author shaozhuguang
 * @create 2018/10/18
 * @since 1.0.0
 */

public class SDKDemo_RegisterTest {
    public static void main(String[] args) {

        if (args != null) {
            if (args[0].equals("user")) {
                SDKDemo_RegisterUser.main(null);
            } else if (args[0].equals("dataaccount")) {
                SDKDemo_RegisterAccount.main(null);
            }
        }
    }
}