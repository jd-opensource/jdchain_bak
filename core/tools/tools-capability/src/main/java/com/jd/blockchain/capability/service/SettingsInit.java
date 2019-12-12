/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.capability.service.SettingsInit
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/27 下午2:26
 * Description:
 */
package com.jd.blockchain.capability.service;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.capability.settings.CapabilitySettings;
import com.jd.blockchain.consensus.action.ActionResponse;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
import com.jd.blockchain.consensus.bftsmart.BftsmartNodeSettings;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.codec.Base58Utils;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/27
 * @since 1.0.0
 */

public class SettingsInit {

    static {
        DataContractRegistry.register(LedgerBlock.class);
        DataContractRegistry.register(TransactionContent.class);
        DataContractRegistry.register(TransactionContentBody.class);
        DataContractRegistry.register(TransactionRequest.class);
        DataContractRegistry.register(NodeRequest.class);
        DataContractRegistry.register(EndpointRequest.class);
        DataContractRegistry.register(TransactionResponse.class);
        DataContractRegistry.register(DataAccountKVSetOperation.class);
        DataContractRegistry.register(DataAccountKVSetOperation.KVWriteEntry.class);

        DataContractRegistry.register(Operation.class);
        DataContractRegistry.register(ContractCodeDeployOperation.class);
        DataContractRegistry.register(ContractEventSendOperation.class);
        DataContractRegistry.register(DataAccountRegisterOperation.class);
        DataContractRegistry.register(UserRegisterOperation.class);
        DataContractRegistry.register(ParticipantRegisterOperation.class);
        DataContractRegistry.register(ParticipantStateUpdateOperation.class);

        DataContractRegistry.register(ActionResponse.class);

        DataContractRegistry.register(BftsmartConsensusSettings.class);
        DataContractRegistry.register(BftsmartNodeSettings.class);

    }

    public static final void init(String settingsFile) throws Exception {

        Settings settings = new Settings();
//        settings.ledgerHash = "6B3aa543AkotypMaLCeuWDTXFLuG9UKyZCSdJBPStJzEe";
        settings.ledgerHash = "6CB4tTkKyfshafJB1xk8deeZ8FxnjJsSnQLExi5Bq6Mum";
        settings.privKey = "177gjsj5PHeCpbAtJE7qnbmhuZMHAEKuMsd45zHkv8F8AWBvTBbff8yRKdCyT3kwrmAjSnY";
        settings.pubKey = "endPsK36koyFr1D245Sa9j83vt6pZUdFBJoJRB3xAsWM6cwhRbna";
        settings.pwd = "abc";
        init(settings);

//        File confFile = new File(settingsFile);
//        if (settingsFile == null || settingsFile.length() == 0 || !confFile.exists()) {
//            ClassPathResource resource = new ClassPathResource(CapabilitySettings.settingsConf);
//            confFile = resource.getFile();
//        }
//        if (!confFile.exists()) {
//            Settings settings = new Settings();
//            settings.ledgerHash = "6B3aa543AkotypMaLCeuWDTXFLuG9UKyZCSdJBPStJzEe";
//            settings.privKey = "177gjsj5PHeCpbAtJE7qnbmhuZMHAEKuMsd45zHkv8F8AWBvTBbff8yRKdCyT3kwrmAjSnY";
//            settings.pubKey = "endPsK36koyFr1D245Sa9j83vt6pZUdFBJoJRB3xAsWM6cwhRbna";
//            settings.pwd = "abc";
//            init(settings);
//        } else  {
//            List<String> readLines = FileUtils.readLines(confFile);
//            if (readLines != null && !readLines.isEmpty()) {
//                Settings settings = new Settings();
//                for (String readLine : readLines) {
//                    if (readLine.startsWith("ledgerHash")) {
//                        settings.ledgerHash = readLine.split("=")[1];
//                    } else if (readLine.startsWith("privKey")) {
//                        settings.privKey = readLine.split("=")[1];
//                    } else if (readLine.startsWith("pubKey")) {
//                        settings.pubKey = readLine.split("=")[1];
//                    } else if (readLine.startsWith("pwd")) {
//                        settings.pwd = readLine.split("=")[1];
//                    }
//                }
//                init(settings);
//            } else {
//                throw new IllegalArgumentException("file is not exist !!!");
//            }
//        }
    }

    private static void init(Settings settings) {
        // 处理ledgerHash
        HashDigest hash = new HashDigest(Base58Utils.decode(settings.getLedgerHash()));
        CapabilitySettings.ledgerHash = hash;

        // 处理用户
        PrivKey privKey = KeyGenUtils.decodePrivKeyWithRawPassword(settings.getPrivKey(), settings.getPwd());
        PubKey pubKey = KeyGenUtils.decodePubKey(settings.getPubKey());
        CapabilitySettings.adminKey = new AsymmetricKeypair(pubKey, privKey);
    }

    private static class Settings {
        String ledgerHash;

        String privKey;

        String pubKey;

        String pwd;

        public String getLedgerHash() {
            return ledgerHash;
        }

        public String getPrivKey() {
            return privKey;
        }

        public String getPubKey() {
            return pubKey;
        }

        public String getPwd() {
            return pwd;
        }
    }
}