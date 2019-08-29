package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.TX_OP_PARTICIPANT_STATE_UPDATE)
public interface ParticipantStateUpdateOperation extends Operation {
    @DataField(order=1, refContract = true)
    ParticipantStateUpdateInfo getStateUpdateInfo();
}
