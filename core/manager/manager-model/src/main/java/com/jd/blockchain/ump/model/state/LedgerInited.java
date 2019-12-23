package com.jd.blockchain.ump.model.state;

public class LedgerInited {

    private String ledgerHash;

    private String ledgerName;

    private String partiName;

    private String partiAddress;

    private String dbUri;

    private StartupState startupState = StartupState.UNKNOWN;

    public LedgerInited() {
    }

    public LedgerInited(String ledgerHash) {
        this.ledgerHash = ledgerHash;
    }

    public String getLedgerHash() {
        return ledgerHash;
    }

    public void setLedgerHash(String ledgerHash) {
        this.ledgerHash = ledgerHash;
    }

    public String getLedgerName() {
        return ledgerName;
    }

    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    public String getPartiName() {
        return partiName;
    }

    public void setPartiName(String partiName) {
        this.partiName = partiName;
    }

    public String getPartiAddress() {
        return partiAddress;
    }

    public void setPartiAddress(String partiAddress) {
        this.partiAddress = partiAddress;
    }

    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri(String dbUri) {
        this.dbUri = dbUri;
    }

    public StartupState getStartupState() {
        return startupState;
    }

    public void setStartupState(StartupState startupState) {
        this.startupState = startupState;
    }

    public LedgerInited buildLedgerHash(String ledgerHash) {
        setLedgerHash(ledgerHash);
        return this;
    }

    public LedgerInited buildLedgerName(String ledgerName) {
        setLedgerName(ledgerName);
        return this;
    }

    public LedgerInited buildPartiName(String partiName) {
        setPartiName(partiName);
        return this;
    }

    public LedgerInited buildPartiAddress(String partiAddress) {
        setPartiAddress(partiAddress);
        return this;
    }

    public LedgerInited buildDbUri(String dbUri) {
        setDbUri(dbUri);
        return this;
    }

    public LedgerInited buildStartupState(StartupState startupState) {
        setStartupState(startupState);
        return this;
    }
}
