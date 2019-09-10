package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.net.NetworkAddress;


/**
 * 参与方信息；
 *
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.METADATA_PARTICIPANT_INFO)
public interface ParticipantInfo {

    /**
     * regist or unregist, temporarily not available to users
     *
     * @return
     */
//    @DataField(order = 0, primitiveType = PrimitiveType.TEXT)
//    String getFlag();

    /**
     * 参与者名称；
     *
     * @return
     */
    @DataField(order = 1, primitiveType = PrimitiveType.TEXT)
    String getName();

    /**
     * 公钥；
     *
     * @return
     */
    @DataField(order = 2, primitiveType = PrimitiveType.BYTES)
    PubKey getPubKey();

    /**
     * 共识协议的网络地址；
     *
     * @return
     */
    @DataField(order = 3, primitiveType = PrimitiveType.BYTES)
    NetworkAddress getNetworkAddress();

}