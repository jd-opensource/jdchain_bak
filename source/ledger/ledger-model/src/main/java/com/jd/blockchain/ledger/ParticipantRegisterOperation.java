package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.net.NetworkAddress;

@DataContract(code= DataCodes.TX_OP_PARTICIPANT_REG)
public interface ParticipantRegisterOperation extends Operation {

    @DataField(order = 0, primitiveType=PrimitiveType.TEXT)
    String getParticipantName();

    @DataField(order = 1, refContract = true)
    BlockchainIdentity getParticipantRegisterIdentity();

    @DataField(order = 2, primitiveType = PrimitiveType.BYTES)
    NetworkAddress getNetworkAddress();
}
