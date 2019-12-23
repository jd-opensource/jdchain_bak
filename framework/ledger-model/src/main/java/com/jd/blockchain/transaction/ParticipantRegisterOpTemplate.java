package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ParticipantInfo;
import com.jd.blockchain.ledger.ParticipantRegisterOperation;
import com.jd.blockchain.utils.net.NetworkAddress;

public class ParticipantRegisterOpTemplate implements ParticipantRegisterOperation {

    static {
        DataContractRegistry.register(ParticipantRegisterOperation.class);
    }

    private String participantName;
    private BlockchainIdentity participantRegisterIdentity;
    private NetworkAddress networkAddress;

    public ParticipantRegisterOpTemplate(String participantName, BlockchainIdentity participantRegisterIdentity, NetworkAddress networkAddress) {
        this.participantName = participantName;
        this.participantRegisterIdentity = participantRegisterIdentity;
        this.networkAddress = networkAddress;

    }

    @Override
    public String getParticipantName() {
        return participantName;
    }

    @Override
    public BlockchainIdentity getParticipantRegisterIdentity() {
        return participantRegisterIdentity;
    }

    @Override
    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }
}
