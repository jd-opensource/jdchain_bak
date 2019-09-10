package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * 参与方状态更新信息；
 *
 *
 */
@DataContract(code = DataCodes.METADATA_PARTICIPANT_STATE_INFO)
public interface ParticipantStateUpdateInfo {

    /**
     * 公钥；
     *
     * @return
     */
    @DataField(order = 1, primitiveType = PrimitiveType.BYTES)
    PubKey getPubKey();

    /**
     * 共识协议的网络地址；
     *
     * @return
     */
    @DataField(order = 2, primitiveType = PrimitiveType.BYTES)
    NetworkAddress getNetworkAddress();

    /**
     * 参与方状态；
     *
     * @return
     */
    @DataField(order = 3, refEnum = true)
    ParticipantNodeState getState();
}
