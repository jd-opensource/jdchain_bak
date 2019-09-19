package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.net.NetworkAddress;

@DataContract(code= DataCodes.TX_OP_PARTICIPANT_STATE_UPDATE)
public interface ParticipantStateUpdateOperation extends Operation {

    @DataField(order = 0, refContract = true)
    BlockchainIdentity getStateUpdateIdentity();

    @DataField(order = 1, primitiveType = PrimitiveType.BYTES)
    NetworkAddress getNetworkAddress();

    @DataField(order = 2, refEnum = true)
    ParticipantNodeState getState();

}
