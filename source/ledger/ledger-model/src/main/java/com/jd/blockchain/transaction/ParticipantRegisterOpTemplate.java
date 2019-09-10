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
    private BlockchainIdentity participantPubKey;
    private NetworkAddress networkAddress;

    public ParticipantRegisterOpTemplate(String participantName, BlockchainIdentity participantPubKey, NetworkAddress networkAddress) {
        this.participantName = participantName;
        this.participantPubKey = participantPubKey;
        this.networkAddress = networkAddress;

    }

    @Override
    public String getParticipantName() {
        return participantName;
    }

    @Override
    public BlockchainIdentity getParticipantIdentity() {
        return participantPubKey;
    }

    @Override
    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }
}
