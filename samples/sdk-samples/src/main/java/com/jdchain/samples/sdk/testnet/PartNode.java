package com.jdchain.samples.sdk.testnet;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.ParticipantNodeState;

import utils.Bytes;

public class PartNode implements ParticipantNode {

    private int id;

    private Bytes address;

    private String name;

    private PubKey pubKey;

    private ParticipantNodeState participantNodeState;

    public PartNode(int id, PubKey pubKey, ParticipantNodeState participantNodeState) {
        this(id, id + "", pubKey, participantNodeState);
    }

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