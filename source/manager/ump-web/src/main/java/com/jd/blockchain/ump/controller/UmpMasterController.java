package com.jd.blockchain.ump.controller;

import com.jd.blockchain.ump.model.PeerSharedConfigs;
import com.jd.blockchain.ump.model.config.LedgerConfig;
import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.model.state.InstallSchedule;
import com.jd.blockchain.ump.model.state.LedgerMasterInstall;
import com.jd.blockchain.ump.service.UmpService;
import com.jd.blockchain.ump.service.UmpStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/master/")
public class UmpMasterController {

    @Autowired
    private UmpService umpService;

    @Autowired
    private UmpStateService umpStateService;

    /**
     * 需要支持的接口
     * 1、接收节点Share的信息
     * 2、接收节点发送来的状态信息
     * 3、接收前端查看某些节点状态的请求
     */
    @RequestMapping(method = RequestMethod.POST, path = "share")
    public LedgerConfig share(@RequestBody final PeerLocalConfig sharedConfig) {

        PeerSharedConfigs sharedConfigs = umpService.loadPeerSharedConfigs(sharedConfig);

        if (sharedConfigs == null) {
            throw new IllegalStateException("PeerSharedConfig may be exits Conflict !!!");
        }

        return umpService.response(sharedConfigs, sharedConfig);
    }

    /**
     * 接收其他Peer节点发送的安装信息
     *
     * @param installSchedule
     *         安装信息
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "receive")
    public String receive(@RequestBody final InstallSchedule installSchedule) {

        try {
            umpStateService.save(installSchedule, null);
        } catch (Exception e) {
            return "FAIL";
        }

        return "SUCCESS";
    }

    @RequestMapping(method = RequestMethod.GET, path = "read/{ledgerKey}")
    public Map<String, List<InstallSchedule>> readState(@PathVariable(name = "ledgerKey") String ledgerKey) {

        return umpStateService.readStates(ledgerKey);
    }

    @RequestMapping(method = RequestMethod.GET, path = "list")
    public List<LedgerMasterInstall> ledgerInstallList() {

        // 返回当前Master收到的所有节点所有的安装信息
        return umpStateService.readLedgerMasterInstalls();
    }
}
