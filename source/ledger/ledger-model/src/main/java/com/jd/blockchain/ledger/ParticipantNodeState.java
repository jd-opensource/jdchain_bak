package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;


/**
 * 参与方节点状态
 * @author zhangshuang
 * @create 2019/7/8
 * @since 1.0.0
 */
@EnumContract(code= DataCodes.ENUM_TYPE_PARTICIPANT_NODE_STATE)
public enum ParticipantNodeState {

    /**
     * 已注册；
     */
    REGISTERED((byte) 0),

    /**
     * 已激活；
     */
    ACTIVED((byte) 1);

    @EnumField(type= PrimitiveType.INT8)
    public final byte CODE;

    private ParticipantNodeState(byte code) {
        this.CODE = code;
    }

    public static ParticipantNodeState valueOf(byte code) {
        for (ParticipantNodeState tr : values()) {
            if (tr.CODE == code) {
                return tr;
            }
        }
        throw new IllegalArgumentException("Unsupported participant node state code!");
    }
}
