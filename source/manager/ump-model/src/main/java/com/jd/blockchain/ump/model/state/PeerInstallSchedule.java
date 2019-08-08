package com.jd.blockchain.ump.model.state;

public class PeerInstallSchedule {

    private InstallProcess process;

    private ScheduleState state;

    public PeerInstallSchedule() {
    }

    public PeerInstallSchedule(InstallProcess process, ScheduleState state) {
        this.process = process;
        this.state = state;
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
