///**
// * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
// * FileName: com.jd.blockchain.sdk.client.ClientResolveUtil
// * Author: shaozhuguang
// * Department: Y事业部
// * Date: 2019/3/27 下午4:12
// * Description:
// */
//package com.jd.blockchain.sdk.client;
//
//import java.lang.reflect.Field;
//
//import com.jd.blockchain.ledger.*;
//import com.jd.blockchain.utils.io.ByteArray;
//import org.apache.commons.codec.binary.Base64;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.jd.blockchain.crypto.CryptoProvider;
//import com.jd.blockchain.crypto.PubKey;
//import com.jd.blockchain.transaction.ContractCodeDeployOpTemplate;
//import com.jd.blockchain.transaction.ContractEventSendOpTemplate;
//import com.jd.blockchain.transaction.DataAccountKVSetOpTemplate;
//import com.jd.blockchain.transaction.DataAccountRegisterOpTemplate;
//import com.jd.blockchain.transaction.KVData;
//import com.jd.blockchain.transaction.LedgerInitOpTemplate;
//import com.jd.blockchain.transaction.LedgerInitSettingData;
//import com.jd.blockchain.transaction.UserRegisterOpTemplate;
//import com.jd.blockchain.utils.Bytes;
//import com.jd.blockchain.utils.codec.Base58Utils;
//import com.jd.blockchain.utils.codec.HexUtils;
//import com.jd.blockchain.utils.io.BytesUtils;
//
///**
// *
// * @author shaozhuguang
// * @create 2019/3/27
// * @since 1.0.0
// */
//
//public class ClientResolveUtil {
//
//    public static KVDataEntry[] read(KVDataEntry[] kvDataEntries) {
//        if (kvDataEntries == null || kvDataEntries.length == 0) {
//            return kvDataEntries;
//        }
//        KVDataEntry[] resolveKvDataEntries = new KVDataEntry[kvDataEntries.length];
//        // kvDataEntries是代理对象，需要处理
//        for (int i = 0; i < kvDataEntries.length; i++) {
//            KVDataEntry kvDataEntry = kvDataEntries[i];
//            String key = kvDataEntry.getKey();
//            long version = kvDataEntry.getVersion();
//            DataType dataType = kvDataEntry.getType();
//            KvData innerKvData = new KvData(key, version, dataType);
//            Object valueObj = kvDataEntry.getValue();
//            switch (dataType) {
//                case NIL:
//                    break;
//                case BYTES:
//                case TEXT:
//                case JSON:
//                    innerKvData.setValue(valueObj.toString());
//                    break;
//                case INT32:
//                    innerKvData.setValue(Integer.parseInt(valueObj.toString()));
//                    break;
//                case INT64:
//                    innerKvData.setValue(Long.parseLong(valueObj.toString()));
//                    break;
//                default:
//                    throw new IllegalStateException("Unsupported value type[" + dataType + "] to resolve!");
//            }
//            resolveKvDataEntries[i] = innerKvData;
//        }
//        return resolveKvDataEntries;
//    }
//
//    public static Operation read(Operation operation) {
//
//        try {
//            // Class
//            Class<?> clazz = operation.getClass();
//            Field field = clazz.getSuperclass().getDeclaredField("h");
//            field.setAccessible(true);
//            Object object = field.get(operation);
//            if (object instanceof JSONObject) {
//                JSONObject jsonObject = (JSONObject) object;
//                if (jsonObject.containsKey("accountID")) {
//                    return convertDataAccountRegisterOperation(jsonObject);
//                } else if (jsonObject.containsKey("userID")) {
//                    return convertUserRegisterOperation(jsonObject);
//                } else if (jsonObject.containsKey("contractID")) {
//                    return convertContractCodeDeployOperation(jsonObject);
//                } else if (jsonObject.containsKey("writeSet")) {
//                    return convertDataAccountKVSetOperation(jsonObject);
//                } else if (jsonObject.containsKey("initSetting")) {
//                    return convertLedgerInitOperation(jsonObject);
//                } else if (jsonObject.containsKey("contractAddress")) {
//                    return convertContractEventSendOperation(jsonObject);
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return null;
//    }
//
//    public static Object readValueByBytesValue(BytesValue bytesValue) {
//        DataType dataType = bytesValue.getType();
//        Bytes saveVal = bytesValue.getValue();
//        Object showVal;
//        switch (dataType) {
//            case BYTES:
//                // return hex
//                showVal = HexUtils.encode(saveVal.toBytes());
//                break;
//            case TEXT:
//            case JSON:
//                showVal = saveVal.toUTF8String();
//                break;
//            case INT64:
//                showVal = BytesUtils.toLong(saveVal.toBytes());
//                break;
//            default:
//                showVal = HexUtils.encode(saveVal.toBytes());
//                break;
//        }
//        return showVal;
//    }
//
//    public static DataAccountRegisterOperation convertDataAccountRegisterOperation(JSONObject jsonObject) {
//        JSONObject account = jsonObject.getJSONObject("accountID");
//        return new DataAccountRegisterOpTemplate(blockchainIdentity(account));
//    }
//
//    public static DataAccountKVSetOperation convertDataAccountKVSetOperation(JSONObject jsonObject) {
//        // 写入集合处理
//        JSONArray writeSetObj = jsonObject.getJSONArray("writeSet");
//        JSONObject accountAddrObj = jsonObject.getJSONObject("accountAddress");
//        String addressBase58 = accountAddrObj.getString("value");
//        Bytes address = Bytes.fromBase58(addressBase58);
//
//        DataAccountKVSetOpTemplate kvOperation = new DataAccountKVSetOpTemplate(address);
//        for (int i = 0; i <writeSetObj.size(); i++) {
//            JSONObject currWriteSetObj = writeSetObj.getJSONObject(i);
//            long expectedVersion = currWriteSetObj.getLong("expectedVersion");
//            JSONObject valueObj = currWriteSetObj.getJSONObject("value");
//            String typeStr = valueObj.getString("type");
//            String realValBase58 = valueObj.getString("value");
//            String key = currWriteSetObj.getString("key");
//            DataType dataType = DataType.valueOf(typeStr);
//            BytesValue bytesValue =BytesValueEntry.fromType(dataType, Base58Utils.decode(realValBase58));
//            KVData kvData = new KVData(key, bytesValue, expectedVersion);
//            kvOperation.set(kvData);
//        }
//
//        return kvOperation;
//    }
//
//    public static LedgerInitOperation convertLedgerInitOperation(JSONObject jsonObject) {
//        JSONObject legerInitObj = jsonObject.getJSONObject("initSetting");
//        LedgerInitSettingData ledgerInitSettingData = new LedgerInitSettingData();
//        String ledgerSeedStr = legerInitObj.getString("ledgerSeed");
//
//        // 种子需要做Base64转换
//        ledgerInitSettingData.setLedgerSeed(Base64.decodeBase64(BytesUtils.toBytes(ledgerSeedStr)));
//
//        String consensusProvider = legerInitObj.getString("consensusProvider");
//
//        ledgerInitSettingData.setConsensusProvider(consensusProvider);
//
//        JSONObject cryptoSettingObj = legerInitObj.getJSONObject("cryptoSetting");
//        boolean autoVerifyHash = cryptoSettingObj.getBoolean("autoVerifyHash");
//        short hashAlgorithm = cryptoSettingObj.getShort("hashAlgorithm");
//
//        CryptoConfig cryptoConfig = new CryptoConfig();
//
//        cryptoConfig.setAutoVerifyHash(autoVerifyHash);
//
//        cryptoConfig.setHashAlgorithm(hashAlgorithm);
//
//        ledgerInitSettingData.setCryptoSetting(cryptoConfig);
//
//
//        JSONObject consensusSettingsObj = legerInitObj.getJSONObject("consensusSettings");
//        Bytes consensusSettings = Bytes.fromBase58(consensusSettingsObj.getString("value"));
//
//        ledgerInitSettingData.setConsensusSettings(consensusSettings);
//
//        JSONArray consensusParticipantsArray = legerInitObj.getJSONArray("consensusParticipants");
//
//        if (!consensusParticipantsArray.isEmpty()) {
//            ParticipantNode[] participantNodes = new ParticipantNode[consensusParticipantsArray.size()];
//            for (int i = 0; i < consensusParticipantsArray.size(); i++) {
//                JSONObject currConsensusParticipant = consensusParticipantsArray.getJSONObject(i);
//                String addressBase58 = currConsensusParticipant.getString("address");
//                String name = currConsensusParticipant.getString("name");
//                int id = currConsensusParticipant.getInteger("id");
//                JSONObject pubKeyObj = currConsensusParticipant.getJSONObject("pubKey");
//                String pubKeyBase58 = pubKeyObj.getString("value");
//                // 生成ParticipantNode对象
//                ParticipantCertData participantCertData = new ParticipantCertData(id, addressBase58, name, new PubKey(Bytes.fromBase58(pubKeyBase58).toBytes()));
//                participantNodes[i] = participantCertData;
//            }
//            ledgerInitSettingData.setConsensusParticipants(participantNodes);
//        }
//
//        return new LedgerInitOpTemplate(ledgerInitSettingData);
//    }
//
//    public static UserRegisterOperation convertUserRegisterOperation(JSONObject jsonObject) {
//        JSONObject user = jsonObject.getJSONObject("userID");
//        return new UserRegisterOpTemplate(blockchainIdentity(user));
//    }
//
//    public static ContractCodeDeployOperation convertContractCodeDeployOperation(JSONObject jsonObject) {
//        JSONObject contract = jsonObject.getJSONObject("contractID");
//        BlockchainIdentityData blockchainIdentity = blockchainIdentity(contract);
//
//        String chainCodeStr = jsonObject.getString("chainCode");
//        ContractCodeDeployOpTemplate contractCodeDeployOpTemplate = new ContractCodeDeployOpTemplate(blockchainIdentity, BytesUtils.toBytes(chainCodeStr));
//        return contractCodeDeployOpTemplate;
//    }
//
//    public static ContractEventSendOperation convertContractEventSendOperation(JSONObject jsonObject) {
//        JSONObject contractAddressObj = jsonObject.getJSONObject("contractAddress");
//        String contractAddress = contractAddressObj.getString("value");
//        String argsStr = jsonObject.getString("args");
//        String event = jsonObject.getString("event");
//        return new ContractEventSendOpTemplate(Bytes.fromBase58(contractAddress), event, BytesUtils.toBytes(argsStr));
//    }
//
//    private static BlockchainIdentityData blockchainIdentity(JSONObject jsonObject) {
//        JSONObject addressObj = jsonObject.getJSONObject("address");
//        // base58值
//        String addressBase58 = addressObj.getString("value");
//        Bytes address = Bytes.fromBase58(addressBase58);
//
//        JSONObject pubKeyObj = jsonObject.getJSONObject("pubKey");
//        // base58值
//        String pubKeyBase58 = pubKeyObj.getString("value");
//        PubKey pubKey = new PubKey(Bytes.fromBase58(pubKeyBase58).toBytes());
//
//        // 生成对应的对象
//        return new BlockchainIdentityData(address, pubKey);
//    }
//
//    public static class CryptoConfig implements CryptoSetting {
//
//        private short hashAlgorithm;
//
//        private boolean autoVerifyHash;
//
//        @Override
//        public CryptoProvider[] getSupportedProviders() {
//            return new CryptoProvider[0];
//        }
//
//        @Override
//        public short getHashAlgorithm() {
//            return hashAlgorithm;
//        }
//
//        @Override
//        public boolean getAutoVerifyHash() {
//            return autoVerifyHash;
//        }
//
//        public void setHashAlgorithm(short hashAlgorithm) {
//            this.hashAlgorithm = hashAlgorithm;
//        }
//
//        public void setAutoVerifyHash(boolean autoVerifyHash) {
//            this.autoVerifyHash = autoVerifyHash;
//        }
//    }
//
//    public static class ParticipantCertData implements ParticipantNode{
//        private int id;
//        private String address;
//        private String name;
//        private PubKey pubKey;
//
//        public ParticipantCertData() {
//        }
//
//        public ParticipantCertData(ParticipantNode participantNode) {
//            this.address = participantNode.getAddress();
//            this.name = participantNode.getName();
//            this.pubKey = participantNode.getPubKey();
//        }
//
//        public ParticipantCertData(int id, String address, String name, PubKey pubKey) {
//            this.id = id;
//            this.address = address;
//            this.name = name;
//            this.pubKey = pubKey;
//        }
//
//        @Override
//        public String getAddress() {
//            return address;
//        }
//
//        @Override
//        public String getName() {
//            return name;
//        }
//
//        @Override
//        public PubKey getPubKey() {
//            return pubKey;
//        }
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//    }
//
//    public static class KvData implements KVDataEntry {
//
//        private String key;
//
//        private long version;
//
//        private DataType dataType;
//
//        private Object value;
//
//        public KvData() {
//        }
//
//        public KvData(String key, long version, DataType dataType) {
//            this(key, version, dataType, null);
//        }
//
//        public KvData(String key, long version, DataType dataType, Object value) {
//            this.key = key;
//            this.version = version;
//            this.dataType = dataType;
//            this.value = value;
//        }
//
//        public void setKey(String key) {
//            this.key = key;
//        }
//
//        public void setVersion(long version) {
//            this.version = version;
//        }
//
//        public void setDataType(DataType dataType) {
//            this.dataType = dataType;
//        }
//
//        public void setValue(Object value) {
//            this.value = value;
//        }
//
//        @Override
//        public String getKey() {
//            return key;
//        }
//
//        @Override
//        public long getVersion() {
//            return version;
//        }
//
//        @Override
//        public DataType getType() {
//            return dataType;
//        }
//
//        @Override
//        public Object getValue() {
//            return value;
//        }
//    }
//}