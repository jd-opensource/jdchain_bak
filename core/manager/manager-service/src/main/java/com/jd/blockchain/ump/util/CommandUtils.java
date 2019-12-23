package com.jd.blockchain.ump.util;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import java.util.*;

public class CommandUtils {

    public static void killVm(String processName) throws Exception {

        MonitoredVm activeVm = activeVm(processName);

        if (activeVm != null) {
            killVm(activeVm);
        }
    }

    public static void killVm(MonitoredVm vm) throws Exception {
        if (vm != null) {
            int vmId = vm.getVmIdentifier().getLocalVmId();
            List<String> killCmd = killCommand(vmId);
            execute(killCmd);
        }
    }

    public static List<String> toCommandList(String cmd) {
        // 要求使用空格
        String[] cmdArray = cmd.split(" ");

        if (cmdArray.length > 0) {
            return Arrays.asList(cmdArray);
        }

        return null;

    }

    public static Process execute(List<String> cmds) throws Exception {

        if (cmds == null || cmds.isEmpty()) {
            throw new IllegalStateException("Command's List is NULL !!!");
        }

        ProcessBuilder pBuilder = new ProcessBuilder(cmds);

        Process process = pBuilder.start();

        return process;

    }

    public static boolean executeAndVerify(List<String> cmds, String verify) throws Exception {

        if (cmds == null || cmds.isEmpty()) {
            throw new IllegalStateException("Command's List is NULL !!!");
        }

        ProcessBuilder pBuilder = new ProcessBuilder(cmds);

        pBuilder.start();

        // 时延5s，再进行判断
        Thread.sleep(5000);

        return isActive(verify);

    }

    public static MonitoredVm activeVm(String processName) throws Exception {

        MonitoredHost localMonitored = MonitoredHost.getMonitoredHost("localhost");

        Set<Integer> activeVms = new HashSet<>(localMonitored.activeVms());

        for (Integer vmId : activeVms) {

            try {
                MonitoredVm vm = localMonitored.getMonitoredVm(new VmIdentifier("//" + vmId));

                String vmProcessName = MonitoredVmUtil.mainClass(vm, true);

                if (vmProcessName.contains(processName)) {
                    return vm;
                }
            } catch (Exception e) {
                // 此处异常打印即可，不需要处理
                System.err.println(e);
            }
        }

        return null;
    }

    public static boolean isActive(String processName) throws Exception {

        MonitoredVm activeVm = activeVm(processName);

        return activeVm != null;
    }

    public static String mainArgs(MonitoredVm vm) {
        if (vm != null) {
            try {
                return MonitoredVmUtil.mainArgs(vm);
            } catch (Exception e) {
                // 打印日志即可
                System.err.println(e);
            }
        }
        return null;
    }

    public static String mainArgs(String processName) throws Exception {

        return mainArgs(activeVm(processName));
    }

    public static List<String> killCommand(int vmId) {
        if (vmId > 1) {
            List<String> killCmd = new ArrayList<>();
            killCmd.add("kill");
            killCmd.add("-9");
            killCmd.add(String.valueOf(vmId));

            return killCmd;
        }

        throw new IllegalStateException(String.format("Can not kill Process ID = [%s]", vmId));
    }
}
