/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.capability.CapabilityEngine
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/27 上午10:16
 * Description:
 */
package com.jd.blockchain.capability;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jd.blockchain.capability.service.RemoteTransactionService;
import com.jd.blockchain.capability.service.SettingsInit;
import com.jd.blockchain.capability.settings.CapabilitySettings;
import com.jd.blockchain.utils.ArgumentSet;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/27
 * @since 1.0.0
 */
@Component
public class CapabilityEngine implements CommandLineRunner {

    public static final String MQURL_ARG = "-s";

    public static final String CONF_ARG = "-c";

    public static final String TOPIC_ARG = "-t";

    public static final String UR_AEG = "-ur";

    public static final String DR_AEG = "-dr";

    public static final String UR_DR_AEG = "-udr";

    public static final String DR_KV_AEG = "-drw";

    public static final String KV_AEG = "-kv";

    public static void engine(String[] args) {
        ArgumentSet arguments = ArgumentSet.resolve(args,
                ArgumentSet.setting()
                        .prefix(MQURL_ARG)
                        .prefix(CONF_ARG)
                        .prefix(TOPIC_ARG)
                        .option(UR_AEG)
                        .option(DR_AEG)
                        .option(UR_DR_AEG)
                        .option(KV_AEG)
                        .option(DR_KV_AEG));
        try {
            ArgumentSet.ArgEntry mqArg = arguments.getArg(MQURL_ARG);
            if (mqArg != null) {
                String mqUrl = mqArg.getValue();
                CapabilitySettings.MSG_QUEUE_URL = mqUrl;
            }

            ArgumentSet.ArgEntry confArg = arguments.getArg(CONF_ARG);
            if (confArg != null) {
                String conf = confArg.getValue();
                SettingsInit.init(conf);
            } else {
                SettingsInit.init(CapabilitySettings.settingsConf);
            }

            ArgumentSet.ArgEntry topicArg = arguments.getArg(TOPIC_ARG);
            if (topicArg != null) {
                String topic = topicArg.getValue();
                CapabilitySettings.TX_TOPIC = topic;
            }

            RemoteTransactionService service = new RemoteTransactionService();

            if (arguments.hasOption(UR_AEG)) {
                // 单纯注册1亿个用户
                service.userRegister(CapabilitySettings.TX_TOTAL_SIZE);
            } else if (arguments.hasOption(DR_AEG)) {
                // 单纯注册1亿数据账户
                service.dataAccountRegister(CapabilitySettings.TX_TOTAL_SIZE);
            } else if (arguments.hasOption(UR_DR_AEG)) {
                // 先注册5千万用户再注册5千万数据账户
                service.userAndDataAccountRegister(CapabilitySettings.TX_HALF_SIZE,
                        CapabilitySettings.TX_HALF_SIZE);
            } else if (arguments.hasOption(DR_KV_AEG)) {
                // 先注册1万数据账户，再对每个数据账户写入1万个kv
                service.dataAccountRegisterAndKvStorage(CapabilitySettings.DR_SIZE,
                        CapabilitySettings.KV_SIZE);
            } else if (arguments.hasOption(KV_AEG)) {
                // 单纯向其中某个数据账户写入KV
                service.kvStorage(CapabilitySettings.KV_TOTAL_SIZE);
            } else {
                // 单纯向其中某个数据账户写入KV
                service.kvStorage(CapabilitySettings.KV_TOTAL_SIZE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        engine(args);
    }
}