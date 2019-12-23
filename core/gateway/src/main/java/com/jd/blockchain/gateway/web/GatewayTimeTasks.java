/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.gateway.web.GatewayTimeTasks
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/16 下午6:17
 * Description:
 */
package com.jd.blockchain.gateway.web;

import com.jd.blockchain.consensus.service.NodeServer;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.gateway.PeerConnector;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/16
 * @since 1.0.0
 */
@Component
@EnableScheduling
public class GatewayTimeTasks {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GatewayTimeTasks.class);

    @Autowired
    private PeerConnector peerConnector;

    //每30分钟执行一次
    @Scheduled(cron = "0 */8 * * * * ")
    public void updateLedger(){
        try {
            peerConnector.reconnect();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}