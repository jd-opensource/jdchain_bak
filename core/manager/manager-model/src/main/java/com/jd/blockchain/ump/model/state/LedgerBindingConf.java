package com.jd.blockchain.ump.model.state;

import java.util.Set;

public class LedgerBindingConf {

    private Set<String> ledgerHashs;

    private long lastTime;

    public LedgerBindingConf() {
    }

    public LedgerBindingConf(long lastTime) {
        this.lastTime = lastTime;
    }

    public Set<String> getLedgerHashs() {
        return ledgerHashs;
    }

    public void setLedgerHashs(Set<String> ledgerHashs) {
        this.ledgerHashs = ledgerHashs;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}
