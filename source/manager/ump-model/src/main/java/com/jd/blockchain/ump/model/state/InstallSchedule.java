package com.jd.blockchain.ump.model.state;

public class InstallSchedule {

    private String ledgerKey;

    private String ledgerAndNodeKey;

    private InstallProcess process;

    private ScheduleState state;

    public InstallSchedule() {
    }

    public InstallSchedule(String ledgerKey, String ledgerAndNodeKey, InstallProcess process, ScheduleState state) {
        this.ledgerKey = ledgerKey;
        this.ledgerAndNodeKey = ledgerAndNodeKey;
        this.process = process;
        this.state = state;
    }

    public String getLedgerKey() {
        return ledgerKey;
    }

    public void setLedgerKey(String ledgerKey) {
        this.ledgerKey = ledgerKey;
    }

    public String getLedgerAndNodeKey() {
        return ledgerAndNodeKey;
    }

    public void setLedgerAndNodeKey(String ledgerAndNodeKey) {
        this.ledgerAndNodeKey = ledgerAndNodeKey;
    }

    public InstallProcess getProcess() {
        return process;
    }

    public void setProcess(InstallProcess process) {
        this.process = process;
    }

    public ScheduleState getState() {
        return state;
    }

    public void setState(ScheduleState state) {
        this.state = state;
    }
}
