package com.jd.blockchain.ump.model.state;

public class LedgerPeerInited {

    private String ledgerHash;

    private LedgerPeerInstall peerInstall;

    private StartupState startupState;

    public LedgerPeerInited() {
    }

    public LedgerPeerInited(String ledgerHash, LedgerPeerInstall peerInstall) {
        this.ledgerHash = ledgerHash;
        this.peerInstall = peerInstall;
    }

    public String getLedgerHash() {
        return ledgerHash;
    }

    public void setLedgerHash(String ledgerHash) {
        this.ledgerHash = ledgerHash;
    }

    public LedgerPeerInstall getPeerInstall() {
        return peerInstall;
    }

    public void setPeerInstall(LedgerPeerInstall peerInstall) {
        this.peerInstall = peerInstall;
    }

    public StartupState getStartupState() {
        return startupState;
    }

    public void setStartupState(StartupState startupState) {
        this.startupState = startupState;
    }
}
