package com.jd.blockchain.ump.model;

import com.jd.blockchain.ump.model.state.InstallSchedule;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UmpQueue {

    private final BlockingQueue<InstallScheduleRequest> QUEUE_INSTALL_SCHEDULE = new LinkedBlockingQueue<>();

    public void put(InstallSchedule installSchedule, MasterAddr masterAddr) throws InterruptedException {
        QUEUE_INSTALL_SCHEDULE.put(new InstallScheduleRequest(installSchedule, masterAddr));
    }

    public InstallScheduleRequest take() throws InterruptedException {
        return QUEUE_INSTALL_SCHEDULE.take();
    }

    public static class InstallScheduleRequest {

        private InstallSchedule installSchedule;

        private MasterAddr masterAddr;

        public InstallScheduleRequest() {
        }

        public InstallScheduleRequest(InstallSchedule installSchedule, MasterAddr masterAddr) {
            this.installSchedule = installSchedule;
            this.masterAddr = masterAddr;
        }

        public InstallSchedule getInstallSchedule() {
            return installSchedule;
        }

        public void setInstallSchedule(InstallSchedule installSchedule) {
            this.installSchedule = installSchedule;
        }

        public MasterAddr getMasterAddr() {
            return masterAddr;
        }

        public void setMasterAddr(MasterAddr masterAddr) {
            this.masterAddr = masterAddr;
        }
    }
}
