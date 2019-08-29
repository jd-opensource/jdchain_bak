package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.PubKey;

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
     * 参与方状态；
     *
     * @return
     */
    @DataField(order = 2, refEnum = true)
    ParticipantNodeState getState();
}
