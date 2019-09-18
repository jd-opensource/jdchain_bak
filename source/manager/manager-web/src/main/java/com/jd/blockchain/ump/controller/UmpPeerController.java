package com.jd.blockchain.ump.controller;

import com.jd.blockchain.ump.model.MasterAddr;
import com.jd.blockchain.ump.model.UmpConstant;
import com.jd.blockchain.ump.model.config.LedgerConfig;
import com.jd.blockchain.ump.model.config.LedgerIdentification;
import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.model.config.PeerSharedConfigVv;
import com.jd.blockchain.ump.model.state.*;
import com.jd.blockchain.ump.model.user.UserKeys;
import com.jd.blockchain.ump.service.UmpService;
import com.jd.blockchain.ump.service.UmpStateService;
import com.jd.blockchain.ump.service.UtilService;
import com.jd.blockchain.ump.util.HttpJsonClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/peer/")
public class UmpPeerController {

    @Autowired
    private UmpService umpService;

    @Autowired
    private UmpStateService umpStateService;

    @Autowired
    private UmpMasterController masterController;

    @Autowired
    private UtilService utilService;

//    @RequestMapping(method = RequestMethod.POST, path = "share")
    public LedgerIdentification share(@RequestBody PeerLocalConfig localConfig) {

        //首先校验配置信息
        localConfig.verify();

        MasterAddr masterAddr = localConfig.masterAddr();

        LedgerConfig ledgerConfig;

        if (localConfig.master()) {
            // 当前节点本身是master，直接调用Controller方法
            ledgerConfig = masterController.share(localConfig);
        } else {
            ledgerConfig = HttpJsonClientUtils.httpPost(masterAddr, UmpConstant.REQUEST_SHARED_URL, localConfig, LedgerConfig.class, false);
        }

        if (ledgerConfig == null) {
            // 未加载成功
            throw new IllegalStateException("Can not load Ledger-Config's Data from Master Node !!!");
        }

        String ledgerAndNodeKey = umpService.save(masterAddr, ledgerConfig, localConfig);

        int nodeId = ledgerConfig.getInitConfig().nodeId(localConfig.getPubKey());

        LedgerIdentification identification = new LedgerIdentification(nodeId, localConfig,
                masterAddr, ledgerAndNodeKey, ledgerConfig.getInitConfig());

        // 将数据写入数据库
        umpStateService.save(identification);

        return identification;
    }

    @RequestMapping(method = RequestMethod.POST, path = "share")
    public LedgerIdentification share(@RequestBody PeerSharedConfigVv sharedConfigVv) {

        String pubKey = sharedConfigVv.getPubKey();

        if (pubKey == null || pubKey.length() == 0) {
            throw new IllegalStateException("Public Key can not be empty !!!");
        }

        // 获取对应的UsersKey，转换为LocalConfig
        UserKeys userKeys = utilService.read(sharedConfigVv.getUserId());

        if (userKeys == null || !pubKey.equals(userKeys.getPubKey())) {
            throw new IllegalStateException(String.format("Can not find UserKeys by %s", pubKey));
        }

        PeerLocalConfig localConfig = sharedConfigVv.toPeerLocalConfig(userKeys);

        return share(localConfig);
    }

    @RequestMapping(method = RequestMethod.POST, path = "install/{ledgerAndNodeKey}")
    public PeerInstallSchedules install(@PathVariable(name = "ledgerAndNodeKey") String ledgerAndNodeKey) {

        return umpService.install(ledgerAndNodeKey);
    }

    @RequestMapping(method = RequestMethod.POST, path = "init/{ledgerAndNodeKey}")
    public PeerInstallSchedules init(@PathVariable(name = "ledgerAndNodeKey") String ledgerAndNodeKey) {

        return umpService.init(ledgerAndNodeKey);
    }

    @RequestMapping(method = RequestMethod.POST, path = "startup")
    public PeerStartupSchedules startup() {

        return umpService.startup();
    }

//    @RequestMapping(method = RequestMethod.POST, path = "stop/{ledgerAndNodeKey}")
    public boolean stop(@PathVariable(name = "ledgerAndNodeKey") String ledgerAndNodeKey) {

        return umpService.stop(ledgerAndNodeKey);
    }

    @RequestMapping(method = RequestMethod.POST, path = "stop")
    public boolean stop() {

        return umpService.stop();
    }

    @RequestMapping(method = RequestMethod.GET, path = "init/read/{ledgerAndNodeKey}")
    public PeerInstallSchedules readInitState(@PathVariable(name = "ledgerAndNodeKey") String ledgerAndNodeKey) {

        return umpStateService.readInitState(ledgerAndNodeKey);
    }

    @RequestMapping(method = RequestMethod.GET, path = "list")
    public List<LedgerPeerInstall> ledgerInstallList() {

        // 返回当前Peer节点所有的安装信息
        return umpStateService.readLedgerPeerInstalls();
    }

    public List<LedgerPeerInited> ledgerInitedList(@RequestParam(name = "search", required = false) String search) {

        // 返回当前Peer节点所有的初始化后信息
        return umpStateService.readLedgerPeerIniteds(search);
    }

    @RequestMapping(method = RequestMethod.GET, path = "initeds")
    public List<LedgerInited> ledgerIniteds(@RequestParam(name = "search", required = false) String search) {

        // 返回当前Peer节点所有的初始化后信息
        return umpStateService.readLedgerIniteds(search);
    }
}
