package com.jd.blockchain.ledger.core.impl.handles;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;
import com.jd.blockchain.utils.Bytes;

public class ParticipantRegisterOperationHandle implements OperationHandle {
    @Override
    public BytesValue process(Operation op, LedgerDataSet dataset, TransactionRequestContext requestContext,
                              LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {

        ParticipantRegisterOperation participantRegOp = (ParticipantRegisterOperation) op;

        LedgerAdminAccount adminAccount = dataset.getAdminAccount();

        ParticipantInfo participantInfo = participantRegOp.getParticipantInfo();

//        ConsensusProvider provider = ConsensusProviders.getProvider(adminAccount.getSetting().getConsensusProvider());

        ParticipantNode participantNode = new PartNode((int)(adminAccount.getParticipantCount()), participantInfo.getName(), participantInfo.getPubKey(), ParticipantNodeState.REGISTED);

//        LedgerAdminAccount.LedgerMetadataImpl metadata = (LedgerAdminAccount.LedgerMetadataImpl) adminAccount.getMetadata();


        PubKey pubKey = participantNode.getPubKey();

        BlockchainIdentityData identityData = new BlockchainIdentityData(pubKey);

        //update consensus setting
//        Bytes newConsensusSettings =  provider.getSettingsFactory().getConsensusSettingsBuilder().updateSettings(metadata.getSetting().getConsensusSetting(), participantInfo);

//        LedgerSetting ledgerSetting = new LedgerConfiguration(adminAccount.getSetting().getConsensusProvider(),
//                newConsensusSettings, metadata.getSetting().getCryptoSetting());

//        metadata.setSetting(ledgerSetting);
//        metadata.setViewId(metadata.getViewId() + 1);

        //reg participant as user
        dataset.getUserAccountSet().register(identityData.getAddress(), pubKey);

        //add new participant as consensus node
        adminAccount.addParticipant(participantNode);

        return null;
    }

    @Override
    public boolean support(Class<?> operationType) {
        return ParticipantRegisterOperation.class.isAssignableFrom(operationType);
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
