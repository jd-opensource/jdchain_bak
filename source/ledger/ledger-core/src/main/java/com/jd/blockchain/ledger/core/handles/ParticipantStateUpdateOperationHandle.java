package com.jd.blockchain.ledger.core.handles;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerSettings;
import com.jd.blockchain.ledger.ParticipantInfo;
import com.jd.blockchain.ledger.ParticipantInfoData;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;
import com.jd.blockchain.ledger.core.LedgerAdminDataset;
import com.jd.blockchain.ledger.core.LedgerConfiguration;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.MultiIDsPolicy;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.SecurityContext;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionRequestExtension;
import com.jd.blockchain.utils.Bytes;


public class ParticipantStateUpdateOperationHandle extends AbstractLedgerOperationHandle<ParticipantStateUpdateOperation> {
    public ParticipantStateUpdateOperationHandle() {
        super(ParticipantStateUpdateOperation.class);
    }

    @Override
    protected void doProcess(ParticipantStateUpdateOperation op, LedgerDataset newBlockDataset,
                             TransactionRequestExtension requestContext, LedgerQuery previousBlockDataset,
                             OperationHandleContext handleContext) {

        // 权限校验；
        SecurityPolicy securityPolicy = SecurityContext.getContextUsersPolicy();
        securityPolicy.checkEndpointPermission(LedgerPermission.REGISTER_PARTICIPANT, MultiIDsPolicy.AT_LEAST_ONE);

        ParticipantStateUpdateOperation stateUpdateOperation = (ParticipantStateUpdateOperation) op;

        LedgerAdminDataset adminAccountDataSet = newBlockDataset.getAdminDataset();

        ConsensusProvider provider = ConsensusProviders.getProvider(adminAccountDataSet.getSettings().getConsensusProvider());

        ParticipantNode[] participants = adminAccountDataSet.getParticipants();

        ParticipantNode participantNode = null;

        for(int i = 0; i < participants.length; i++) {
            if (stateUpdateOperation.getStateUpdateIdentity().getPubKey().equals(participants[i].getPubKey())) {
               participantNode = new PartNode(participants[i].getId(), participants[i].getName(), participants[i].getPubKey(), ParticipantNodeState.ACTIVED);
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
