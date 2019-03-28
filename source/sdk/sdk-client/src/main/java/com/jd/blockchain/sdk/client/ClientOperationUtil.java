/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.client.ClientOperationUtil
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/3/27 下午4:12
 * Description:
 */
package com.jd.blockchain.sdk.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.data.*;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.codec.HexUtils;
import com.jd.blockchain.utils.io.BytesSlice;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.Field;

/**
 *
 * @author shaozhuguang
 * @create 2019/3/27
 * @since 1.0.0
 */

public class ClientOperationUtil {

    public static Operation read(Operation operation) {

        try {
            // Class
            Class<?> clazz = operation.getClass();
            Field field = clazz.getSuperclass().getDeclaredField("h");
            field.setAccessible(true);
            Object object = field.get(operation);
            if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                if (jsonObject.containsKey("accountID")) {
                    return convertDataAccountRegisterOperation(jsonObject);
                } else if (jsonObject.containsKey("userID")) {
                    return convertUserRegisterOperation(jsonObject);
                } else if (jsonObject.containsKey("contractID")) {
                    return convertContractCodeDeployOperation(jsonObject);
                } else if (jsonObject.containsKey("writeSet")) {
                    return convertDataAccountKVSetOperation(jsonObject);
                } else if (jsonObject.containsKey("initSetting")) {
                    return convertLedgerInitOperation(jsonObject);
                } else if (jsonObject.containsKey("contractAddress")) {
                    return convertContractEventSendOperation(jsonObject);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static Object readValueByBytesValue(BytesValue bytesValue) {
        DataType dataType = bytesValue.getType();
        BytesSlice saveVal = bytesValue.getValue();
        Object showVal;
        switch (dataType) {
            case BYTES:
                // return hex
                showVal = HexUtils.encode(saveVal.getBytesCopy());
                break;
            case TEXT:
            case JSON:
                showVal = saveVal.getString();
                break;
            case INT64:
                showVal = saveVal.getLong();
                break;
            default:
                showVal = HexUtils.encode(saveVal.getBytesCopy());
                break;
        }
        return showVal;
    }

    public static DataAccountRegisterOperation convertDataAccountRegisterOperation(JSONObject jsonObject) {
        JSONObject account = jsonObject.getJSONObject("accountID");
        return new DataAccountRegisterOpTemplate(blockchainIdentity(account));
    }

    public static DataAccountKVSetOperation convertDataAccountKVSetOperation(JSONObject jsonObject) {
        // 写入集合处理
        JSONArray writeSetObj = jsonObject.getJSONArray("writeSet");
        JSONObject accountAddrObj = jsonObject.getJSONObject("accountAddress");
        String addressBase58 = accountAddrObj.getString("value");
        Bytes address = Bytes.fromBase58(addressBase58);

        DataAccountKVSetOpTemplate kvOperation = new DataAccountKVSetOpTemplate(address);
        for (int i = 0; i <writeSetObj.size(); i++) {
            JSONObject currWriteSetObj = writeSetObj.getJSONObject(i);
            long expectedVersion = currWriteSetObj.getLong("expectedVersion");
            JSONObject valueObj = currWriteSetObj.getJSONObject("value");
            String typeStr = valueObj.getString("type");
            String realValBase58 = valueObj.getString("value");
            String key = currWriteSetObj.getString("key");
            DataType dataType = DataType.valueOf(typeStr);
            BytesValue bytesValue = new BytesValueImpl(dataType, Base58Utils.decode(realValBase58));
            KVData kvData = new KVData(key, bytesValue, expectedVersion);
            kvOperation.set(kvData);
        }

        return kvOperation;
    }

    public static LedgerInitOperation convertLedgerInitOperation(JSONObject jsonObject) {
        JSONObject legerInitObj = jsonObject.getJSONObject("initSetting");
        LedgerInitSettingData ledgerInitSettingData = new LedgerInitSettingData();
        String ledgerSeedStr = legerInitObj.getString("ledgerSeed");

        // 种子需要做Base64转换
        ledgerInitSettingData.setLedgerSeed(Base64.decodeBase64(ledgerSeedStr.getBytes()));

        String consensusProvider = legerInitObj.getString("consensusProvider");

        ledgerInitSettingData.setConsensusProvider(consensusProvider);

        JSONObject cryptoSettingObj = legerInitObj.getJSONObject("cryptoSetting");
        boolean autoVerifyHash = cryptoSettingObj.getBoolean("autoVerifyHash");
        String hashAlgorithmStr = cryptoSettingObj.getString("hashAlgorithm");

        CryptoConfig cryptoConfig = new CryptoConfig();
        cryptoConfig.setAutoVerifyHash(autoVerifyHash);
        cryptoConfig.setHashAlgorithm(CryptoAlgorithm.valueOf(hashAlgorithmStr));

        ledgerInitSettingData.setCryptoSetting(cryptoConfig);


        JSONObject consensusSettingsObj = legerInitObj.getJSONObject("consensusSettings");
        Bytes consensusSettings = Bytes.fromBase58(consensusSettingsObj.getString("value"));

        ledgerInitSettingData.setConsensusSettings(consensusSettings);

        JSONArray consensusParticipantsArray = legerInitObj.getJSONArray("consensusParticipants");

        if (!consensusParticipantsArray.isEmpty()) {
            ParticipantNode[] participantNodes = new ParticipantNode[consensusParticipantsArray.size()];
            for (int i = 0; i < consensusParticipantsArray.size(); i++) {
                JSONObject currConsensusParticipant = consensusParticipantsArray.getJSONObject(i);
                String addressBase58 = currConsensusParticipant.getString("address");
                String name = currConsensusParticipant.getString("name");
                int id = currConsensusParticipant.getInteger("id");
                JSONObject pubKeyObj = currConsensusParticipant.getJSONObject("pubKey");
                String pubKeyBase58 = pubKeyObj.getString("value");
                // 生成ParticipantNode对象
                ParticipantCertData participantCertData = new ParticipantCertData(id, addressBase58, name, new PubKey(Bytes.fromBase58(pubKeyBase58).toBytes()));
                participantNodes[i] = participantCertData;
            }
            ledgerInitSettingData.setConsensusParticipants(participantNodes);
        }

        return new LedgerInitOpTemplate(ledgerInitSettingData);
    }

    public static UserRegisterOperation convertUserRegisterOperation(JSONObject jsonObject) {
        JSONObject user = jsonObject.getJSONObject("userID");
        return new UserRegisterOpTemplate(blockchainIdentity(user));
    }

    public static ContractCodeDeployOperation convertContractCodeDeployOperation(JSONObject jsonObject) {
        JSONObject contract = jsonObject.getJSONObject("contractID");
        BlockchainIdentityData blockchainIdentity = blockchainIdentity(contract);

        String chainCodeStr = jsonObject.getString("chainCode");
        ContractCodeDeployOpTemplate contractCodeDeployOpTemplate = new ContractCodeDeployOpTemplate(blockchainIdentity, chainCodeStr.getBytes());
        return contractCodeDeployOpTemplate;
    }

    public static ContractEventSendOperation convertContractEventSendOperation(JSONObject jsonObject) {
        JSONObject contractAddressObj = jsonObject.getJSONObject("contractAddress");
        String contractAddress = contractAddressObj.getString("value");
        String argsStr = jsonObject.getString("args");
        String event = jsonObject.getString("event");
        return new ContractEventSendOpTemplate(Bytes.fromBase58(contractAddress), event, argsStr.getBytes());
    }

    private static BlockchainIdentityData blockchainIdentity(JSONObject jsonObject) {
        JSONObject addressObj = jsonObject.getJSONObject("address");
        // base58值
        String addressBase58 = addressObj.getString("value");
        Bytes address = Bytes.fromBase58(addressBase58);

        JSONObject pubKeyObj = jsonObject.getJSONObject("pubKey");
        // base58值
        String pubKeyBase58 = pubKeyObj.getString("value");
        PubKey pubKey = new PubKey(Bytes.fromBase58(pubKeyBase58).toBytes());

        // 生成对应的对象
        return new BlockchainIdentityData(address, pubKey);
    }

    public static class CryptoConfig implements CryptoSetting {

        private CryptoAlgorithm hashAlgorithm;

        private boolean autoVerifyHash;

        @Override
        public CryptoAlgorithm getHashAlgorithm() {
            return hashAlgorithm;
        }

        @Override
        public boolean getAutoVerifyHash() {
            return autoVerifyHash;
        }

        public void setHashAlgorithm(CryptoAlgorithm hashAlgorithm) {
            this.hashAlgorithm = hashAlgorithm;
        }

        public void setAutoVerifyHash(boolean autoVerifyHash) {
            this.autoVerifyHash = autoVerifyHash;
        }
    }

    public static class ParticipantCertData implements ParticipantNode{
        private int id;
        private String address;
        private String name;
        private PubKey pubKey;

        public ParticipantCertData() {
        }

        public ParticipantCertData(ParticipantNode participantNode) {
            this.address = participantNode.getAddress();
            this.name = participantNode.getName();
            this.pubKey = participantNode.getPubKey();
        }

        public ParticipantCertData(int id, String address, String name, PubKey pubKey) {
            this.id = id;
            this.address = address;
            this.name = name;
            this.pubKey = pubKey;
        }

        @Override
        public String getAddress() {
            return address;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public PubKey getPubKey() {
            return pubKey;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}