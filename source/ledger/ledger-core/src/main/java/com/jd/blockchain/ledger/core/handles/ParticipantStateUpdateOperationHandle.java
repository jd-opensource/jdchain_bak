package com.jd.blockchain.ledger.core.handles;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.utils.Bytes;


public class ParticipantStateUpdateOperationHandle extends AbstractLedgerOperationHandle<ParticipantStateUpdateOperation> {
    public ParticipantStateUpdateOperationHandle() {
        super(ParticipantStateUpdateOperation.class);
    }

    @Override
    protected void doProcess(ParticipantStateUpdateOperation op, LedgerDataset newBlockDataset,
                             TransactionRequestExtension requestContext, LedgerDataQuery previousBlockDataset,
                             OperationHandleContext handleContext, LedgerService ledgerService) {

        // 权限校验；
        SecurityPolicy securityPolicy = SecurityContext.getContextUsersPolicy();
        securityPolicy.checkEndpointPermission(LedgerPermission.REGISTER_PARTICIPANT, MultiIDsPolicy.AT_LEAST_ONE);

        ParticipantStateUpdateOperation stateUpdateOperation = (ParticipantStateUpdateOperation) op;

        LedgerAdminDataset adminAccountDataSet = newBlockDataset.getAdminDataset();

        ConsensusProvider provider = ConsensusProviders.getProvider(adminAccountDataSet.getSettings().getConsensusProvider());

        ParticipantNode[] participants = adminAccountDataSet.getParticipants();

        ParticipantNode participantNode = null;

        for(int i = 0; i < participants.length; i++) {
            if (stateUpdateOperation.getParticipantIdentity().getPubKey().equals(participants[i].getPubKey())) {
               participantNode = new PartNode(participants[i].getId(), participants[i].getName(), participants[i].getPubKey(), ParticipantNodeState.CONSENSUSED);
               break;
            }
        }

        //update consensus setting
        ParticipantInfo participantInfo = new ParticipantInfoData(participantNode.getName(), participantNode.getPubKey(), stateUpdateOperation.getNetworkAddress());

        Bytes newConsensusSettings =  provider.getSettingsFactory().getConsensusSettingsBuilder().updateSettings(adminAccountDataSet.getSettings().getConsensusSetting(), participantInfo);

        LedgerSettings ledgerSetting = new LedgerConfiguration(adminAccountDataSet.getSettings().getConsensusProvider(),
                newConsensusSettings, adminAccountDataSet.getPreviousSetting().getCryptoSetting());

        adminAccountDataSet.setLedgerSetting(ledgerSetting);

        adminAccountDataSet.updateParticipant(participantNode);

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
