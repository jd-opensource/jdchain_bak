package com.jd.blockchain.ledger.core.impl.handles;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;

public class ParticipantStateUpdateOperationHandle implements OperationHandle {

    @Override
    public boolean support(Class<?> operationType) {
        return ParticipantStateUpdateOperation.class.isAssignableFrom(operationType);
    }

    @Override
    public BytesValue process(Operation op, LedgerDataSet newBlockDataset, TransactionRequestContext requestContext, LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {

        ParticipantStateUpdateOperation stateUpdateOperation = (ParticipantStateUpdateOperation) op;

        LedgerAdminAccount adminAccount = newBlockDataset.getAdminAccount();

        ParticipantNode[] participants = adminAccount.getParticipants();

        ParticipantNode participantNode = null;

        for(int i = 0; i < participants.length; i++) {
            if (stateUpdateOperation.getStateUpdateInfo().getPubKey().equals(participants[i].getPubKey())) {
               participantNode = new PartNode(participants[i].getId(), participants[i].getName(), participants[i].getPubKey(), ParticipantNodeState.CONSENSUSED);
            }
        }

        adminAccount.updateParticipant(participantNode);

        return null;
    }

    private static class PartNode implements ParticipantNode {

        private int id;

        private String address;

        private String name;

        private PubKey pubKey;

        private ParticipantNodeState participantNodeState;

        public PartNode(int id, String name, PubKey pubKey, ParticipantNodeState participantNodeState) {
            this.id = id;
            this.name = name;
            this.pubKey = pubKey;
            this.address = AddressEncoding.generateAddress(pubKey).toBase58();
            this.participantNodeState = participantNodeState;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getAddress() {
            return address;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public PubKey getPubKey() {
            return pubKey;
        }

        @Override
        public ParticipantNodeState getParticipantNodeState() {
            return participantNodeState;
        }
    }

}
