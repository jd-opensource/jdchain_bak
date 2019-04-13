/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.peer.web.ScheduledTasks
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/7 上午11:12
 * Description:
 */
package com.jd.blockchain.peer.web;

import com.jd.blockchain.consensus.service.NodeServer;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.core.LedgerManage;
import com.jd.blockchain.peer.ConsensusManage;
import com.jd.blockchain.peer.LedgerBindingConfigAware;
import com.jd.blockchain.peer.PeerServerBooter;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.utils.ArgumentSet;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/7
 * @since 1.0.0
 */
@Component
//@EnableScheduling
public class PeerTimeTasks implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private LedgerManage ledgerManager;

    private String ledgerBindConfigFile;

    //每1分钟执行一次
    @Scheduled(cron = "0 */5 * * * * ")
    public void updateLedger(){
        System.out.println ("Update Ledger Tasks Start " + new Date());
        try {
            LedgerBindingConfig ledgerBindingConfig = loadLedgerBindingConfig();

            HashDigest[] totalLedgerHashs = ledgerBindingConfig.getLedgerHashs();

            HashDigest[] existingLedgerHashs = ledgerManager.getLedgerHashs();

            Set<HashDigest> newAddHashs = new HashSet<>();

            for (HashDigest ledgerHash : totalLedgerHashs) {
                boolean isExist = false;
                for (HashDigest exist : existingLedgerHashs) {
                    if (ledgerHash.equals(exist)) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    newAddHashs.add(ledgerHash);
                }
            }
            if (!newAddHashs.isEmpty()) {
                // 建立共识网络；
                Map<String, LedgerBindingConfigAware> bindingConfigAwares = applicationContext.getBeansOfType(LedgerBindingConfigAware.class);
                List<NodeServer> nodeServers = new ArrayList<>();
                for (HashDigest ledgerHash : newAddHashs) {
                    System.out.printf("newLedger[%s] \r\n", ledgerHash.toBase58());
                    for (LedgerBindingConfigAware aware : bindingConfigAwares.values()) {
                        nodeServers.add(aware.setConfig(ledgerBindingConfig, ledgerHash));
                    }
                }
                // 启动指定NodeServer节点
                ConsensusManage consensusManage = applicationContext.getBean(ConsensusManage.class);
                for (NodeServer nodeServer : nodeServers) {
                    consensusManage.runRealm(nodeServer);
                }
            } else {
                System.out.println("All Ledgers is newest!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private LedgerBindingConfig loadLedgerBindingConfig() throws Exception {
        LedgerBindingConfig ledgerBindingConfig;
        ledgerBindConfigFile = PeerServerBooter.ledgerBindConfigFile;
        System.out.printf("load ledgerBindConfigFile = %s \r\n", ledgerBindConfigFile);
        if (ledgerBindConfigFile == null) {
            ClassPathResource configResource = new ClassPathResource("ledger-binding.conf");
            InputStream in = configResource.getInputStream();
            ledgerBindingConfig = LedgerBindingConfig.resolve(in);
        } else {
            File file = new File(ledgerBindConfigFile);
            ledgerBindingConfig = LedgerBindingConfig.resolve(file);
        }
        return ledgerBindingConfig;
    }
}