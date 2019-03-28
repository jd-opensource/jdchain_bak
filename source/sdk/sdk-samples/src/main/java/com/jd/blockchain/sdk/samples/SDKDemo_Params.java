/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.samples.SDKDemo_Params
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/10/18 下午2:16
 * Description:
 */
package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.crypto.asymmetric.PrivKey;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.tools.keygen.KeyGenCommand;

/**
 *
 * @author shaozhuguang
 * @create 2018/10/18
 * @since 1.0.0
 */

public class SDKDemo_Params {
    public static final String PASSWORD = "abc";

    public static final String[] PUB_KEYS = { "endPsK36koyFr1D245Sa9j83vt6pZUdFBJoJRB3xAsWM6cwhRbna",
            "endPsK36sC5JdPCDPDAXUwZtS3sxEmqEhFcC4whayAsTTh8Z6eoZ",
            "endPsK36jEG281HMHeh6oSqzqLkT95DTnCM6REDURjdb2c67uR3R",
            "endPsK36nse1dck4uF19zPvAMijCV336Y3zWdgb4rQG8QoRj5ktR" };

    public static final String[] PRIV_KEYS = {
            "177gjsj5PHeCpbAtJE7qnbmhuZMHAEKuMsd45zHkv8F8AWBvTBbff8yRKdCyT3kwrmAjSnY",
            "177gjw9u84WtuCsK8u2WeH4nWqzgEoJWY7jJF9AU6XwLHSosrcNX3H6SSBsfvR53HgX7KR2",
            "177gk2FpjufgEon92mf2oRRFXDBZkRy8SkFci7Jxc5pApZEJz3oeCoxieWatDD3Xg7i1QEN",
            "177gjvv7qvfCAXroFezSn23UFXLVLFofKS3y6DXkJ2DwVWS4LcRNtxRgiqWmQEeWNz4KQ3J" };

    public static PrivKey privkey0 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD);
    public static PrivKey privkey1 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[1], PASSWORD);
    public static PrivKey privkey2 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[2], PASSWORD);
    public static PrivKey privkey3 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[3], PASSWORD);

    public static PubKey pubKey0 = KeyGenCommand.decodePubKey(PUB_KEYS[0]);
    public static PubKey pubKey1 = KeyGenCommand.decodePubKey(PUB_KEYS[1]);
    public static PubKey pubKey2 = KeyGenCommand.decodePubKey(PUB_KEYS[2]);
    public static PubKey pubKey3 = KeyGenCommand.decodePubKey(PUB_KEYS[3]);
}