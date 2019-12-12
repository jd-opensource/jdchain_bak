package com.jd.blockchain.ledger.core.handles;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.ParticipantInfo;
import com.jd.blockchain.ledger.ParticipantInfoData;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.ledger.ParticipantRegisterOperation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.LedgerAdminDataset;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.MultiIDsPolicy;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.SecurityContext;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionRequestExtension;
import com.jd.blockchain.transaction.UserRegisterOpTemplate;
import com.jd.blockchain.utils.Bytes;


public class ParticipantRegisterOperationHandle extends AbstractLedgerOperationHandle<ParticipantRegisterOperation> {
    public ParticipantRegisterOperationHandle() {
        super(ParticipantRegisterOperation.class);
    }

    @Override
    protected void doProcess(ParticipantRegisterOperation op, LedgerDataset newBlockDataset,
                             TransactionRequestExtension requestContext, LedgerQuery previousBlockDataset,
                             OperationHandleContext handleContext) {

        // 权限校验；
        SecurityPolicy securityPolicy = SecurityContext.getContextUsersPolicy();
        securityPolicy.checkEndpointPermission(LedgerPermission.REGISTER_PARTICIPANT, MultiIDsPolicy.AT_LEAST_ONE);

        ParticipantRegisterOperation participantRegOp = (ParticipantRegisterOperation) op;

        LedgerAdminDataset adminAccountDataSet = newBlockDataset.getAdminDataset();

        ParticipantInfo participantInfo = new ParticipantInfoData(participantRegOp.getParticipantName(), participantRegOp.getParticipantRegisterIdentity().getPubKey(), participantRegOp.getNetworkAddress());

        ParticipantNode participantNode = new PartNode((int)(adminAccountDataSet.getParticipantCount()), participantInfo.getName(), participantInfo.getPubKey(), ParticipantNodeState.REGISTERED);

        //add new participant as consensus node
        adminAccountDataSet.addParticipant(participantNode);

        // Build UserRegisterOperation, reg participant as user
        UserRegisterOperation userRegOp = new UserRegisterOpTemplate(participantRegOp.getParticipantRegisterIdentity());
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
