package com.jd.blockchain.ump.controller;

import com.jd.blockchain.ump.model.config.LedgerIdentification;
import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.model.state.PeerInstallSchedules;
import com.jd.blockchain.ump.service.UmpService;
import com.jd.blockchain.ump.service.UmpSimulateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(path = "/peer/")
public class UmpPeerSimulateController {

    private final Map<Integer, String> ledgerAndNodeKeys = new ConcurrentHashMap<>();

    @Autowired
    private UmpService umpService;

    @Autowired
    private UmpSimulateService simulateService;

    @Autowired
    private UmpPeerController peerController;

    @RequestMapping(method = RequestMethod.GET, path = "share/simulate/{node}")
    public LedgerIdentification share(@PathVariable(name = "node") int nodeId) {

        boolean isMaster = false;
        if (nodeId == 0) {
            isMaster = true;
        }

        PeerLocalConfig localConfig = simulateService.nodePeerLocalConfig(nodeId, isMaster);

        LedgerIdentification identification = peerController.share(localConfig);

        // 作为缓存使用
        ledgerAndNodeKeys.put(nodeId, identification.getLedgerAndNodeKey());

        return identification;
    }


    @RequestMapping(method = RequestMethod.GET, path = "install/simulate/{node}")
    public PeerInstallSchedules install(@PathVariable(name = "node") int nodeId) {

        String ledgerAndNodeKey = ledgerAndNodeKeys.get(nodeId);

        return umpService.install(ledgerAndNodeKey);
    }

    @RequestMapping(method = RequestMethod.GET, path = "init/simulate/{node}")
    public PeerInstallSchedules init(@PathVariable(name = "node") int nodeId) {

        return umpService.init(ledgerAndNodeKeys.get(nodeId));
    }
}
