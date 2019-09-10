package com.jd.blockchain.ump.model.state;

import java.util.ArrayList;
import java.util.List;

public class PeerStartupSchedules {

    private String peerPath;

    private List<PeerInstallSchedule> installSchedules = new ArrayList<>();

    public PeerStartupSchedules() {
    }

    public PeerStartupSchedules(String peerPath) {
        this.peerPath = peerPath;
    }


    public PeerStartupSchedules addInstallSchedule(PeerInstallSchedule installSchedule) {
        this.installSchedules.add(installSchedule);
        return this;
    }

    public List<PeerInstallSchedule> getInstallSchedules() {
        return installSchedules;
    }

    public void setInstallSchedules(List<PeerInstallSchedule> installSchedules) {
        this.installSchedules = installSchedules;
    }

    public String getPeerPath() {
        return peerPath;
    }

    public void setPeerPath(String peerPath) {
        this.peerPath = peerPath;
    }
}
