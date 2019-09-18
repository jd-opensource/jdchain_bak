package com.jd.blockchain.ump.model.state;

import com.jd.blockchain.ump.model.config.LedgerIdentification;

import java.util.ArrayList;
import java.util.List;

public class PeerInstallSchedules {

    private String ledgerHash;

    private LedgerIdentification identification;

    private List<PeerInstallSchedule> installSchedules = new ArrayList<>();

    public PeerInstallSchedules() {
    }

    public PeerInstallSchedules(LedgerIdentification identification) {
        this.identification = identification;
    }

    public PeerInstallSchedules(LedgerIdentification identification, String ledgerHash) {
        this.identification = identification;
        this.ledgerHash = ledgerHash;
    }

    public PeerInstallSchedules addInstallSchedule(PeerInstallSchedule installSchedule) {
        this.installSchedules.add(installSchedule);
        return this;
    }

    public PeerInstallSchedules initLedgerHash(String ledgerHash) {
        setLedgerHash(ledgerHash);
        return this;
    }

    public String getLedgerHash() {
        return ledgerHash;
    }

    public void setLedgerHash(String ledgerHash) {
        this.ledgerHash = ledgerHash;
    }

    public LedgerIdentification getIdentification() {
        return identification;
    }

    public void setIdentification(LedgerIdentification identification) {
        this.identification = identification;
    }

    public List<PeerInstallSchedule> getInstallSchedules() {
        return installSchedules;
    }

    public void setInstallSchedules(List<PeerInstallSchedule> installSchedules) {
        this.installSchedules = installSchedules;
    }
}
