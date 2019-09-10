package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;
import com.jd.blockchain.utils.net.NetworkAddress;

public class ParticipantStateUpdateOpTemplate implements ParticipantStateUpdateOperation {

    static {
        DataContractRegistry.register(ParticipantStateUpdateOperation.class);
    }

    private BlockchainIdentity blockchainIdentity;
    private NetworkAddress networkAddress;
    private ParticipantNodeState participantNodeState;

    public ParticipantStateUpdateOpTemplate(BlockchainIdentity blockchainIdentity, NetworkAddress networkAddress, ParticipantNodeState participantNodeState) {

        this.blockchainIdentity = blockchainIdentity;
        this.networkAddress = networkAddress;
        this.participantNodeState = participantNodeState;
    }


    @Override
    public BlockchainIdentity getParticipantIdentity() {
        return blockchainIdentity;
    }

    @Override
    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    @Override
    public ParticipantNodeState getState() {
        return participantNodeState;
    }
}
