package com.jd.blockchain.ledger.core.handles;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.utils.Bytes;


public class ParticipantRegisterOperationHandle extends AbstractLedgerOperationHandle<ParticipantRegisterOperation> {
    public ParticipantRegisterOperationHandle() {
        super(ParticipantRegisterOperation.class);
    }

    @Override
    protected void doProcess(ParticipantRegisterOperation op, LedgerDataset newBlockDataset,
                             TransactionRequestExtension requestContext, LedgerDataQuery previousBlockDataset,
                             OperationHandleContext handleContext, LedgerService ledgerService) {

        // 权限校验；
        SecurityPolicy securityPolicy = SecurityContext.getContextUsersPolicy();
        securityPolicy.checkEndpointPermission(LedgerPermission.REGISTER_PARTICIPANT, MultiIDsPolicy.AT_LEAST_ONE);

        ParticipantRegisterOperation participantRegOp = (ParticipantRegisterOperation) op;

        LedgerAdminDataset adminAccountDataSet = newBlockDataset.getAdminDataset();

        ParticipantInfo participantInfo = participantRegOp.getParticipantInfo();

        ParticipantNode participantNode = new PartNode((int)(adminAccountDataSet.getParticipantCount()), participantInfo.getName(), participantInfo.getPubKey(), ParticipantNodeState.REGISTED);

        PubKey pubKey = participantNode.getPubKey();

        BlockchainIdentityData identityData = new BlockchainIdentityData(pubKey);

//        //reg participant as user
//        dataset.getUserAccountSet().register(identityData.getAddress(), pubKey);

        //add new participant as consensus node
        adminAccountDataSet.addParticipant(participantNode);

        // Build UserRegisterOperation;
        UserRegisterOperation userRegOp = null;//
        handleContext.handle(userRegOp);
    }

    private static class PartNode implements ParticipantNode {

        private int id;

        private Bytes address;

        private String name;

        private PubKey pubKey;

        private ParticipantNodeState participantNodeState;

        public PartNode(int id, String name, PubKey pubKey, ParticipantNodeState participantNodeState) {
            this.id = id;
            this.name = name;
            this.pubKey = pubKey;
            this.address = AddressEncoding.generateAddress(pubKey);
            this.participantNodeState = participantNodeState;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public Bytes getAddress() {
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
