package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.ledger.LedgerMetadata;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jd.blockchain.ledger.core.LedgerAdminDataset.*;

/**
 * @author shaozhuguang
 * @date 2019-09-16
 *
 * LedgerAdminInfo的独立实现类，主要用于页面展示，区分 {@link LedgerAdminDataset}
 */
public class LedgerAdminInfoData implements LedgerAdminInfo {

    static {
        DataContractRegistry.register(LedgerMetadata.class);
        DataContractRegistry.register(LedgerMetadata_V2.class);
    }

    private static Logger LOGGER = LoggerFactory.getLogger(LedgerAdminInfoData.class);

    private final Bytes metaPrefix;

    private final Bytes settingPrefix;

    private LedgerMetadata_V2 origMetadata;

    private LedgerAdminDataset.LedgerMetadataInfo metadata;

    /**
     * 原来的账本设置；
     *
     * <br>
     * 对 LedgerMetadata 修改的新配置不能立即生效，需要达成共识后，在下一次区块计算中才生效；
     */
    private LedgerSettings previousSettings;

    /**
     * 账本的参与节点；
     */
    private ParticipantDataset participants;

    /**
     * 账本参数配置；
     */
    private LedgerSettings settings;

    private ExPolicyKVStorage storage;

    public LedgerAdminInfoData(HashDigest adminAccountHash, String keyPrefix, ExPolicyKVStorage kvStorage,
                              VersioningKVStorage versioningKVStorage, boolean readonly) {

        this.metaPrefix = Bytes.fromString(keyPrefix + LEDGER_META_PREFIX);
        this.settingPrefix = Bytes.fromString(keyPrefix + LEDGER_SETTING_PREFIX);
        this.storage = kvStorage;
        this.origMetadata = loadAndVerifyMetadata(adminAccountHash);
        this.metadata = new LedgerMetadataInfo(origMetadata);
        this.settings = loadAndVerifySettings(metadata.getSettingsHash());
        // 复制记录一份配置作为上一个区块的原始配置，该实例仅供读取，不做修改，也不会回写到存储；
        this.previousSettings = new LedgerConfiguration(settings);

        String partiPrefix = keyPrefix + LEDGER_PARTICIPANT_PREFIX;
        this.participants = new ParticipantDataset(metadata.getParticipantsHash(), previousSettings.getCryptoSetting(),
                partiPrefix, kvStorage, versioningKVStorage, readonly);
    }

    private LedgerMetadata_V2 loadAndVerifyMetadata(HashDigest adminAccountHash) {
        Bytes key = encodeMetadataKey(adminAccountHash);
        byte[] bytes = storage.get(key);
        HashFunction hashFunc = Crypto.getHashFunction(adminAccountHash.getAlgorithm());
        if (!hashFunc.verify(adminAccountHash, bytes)) {
            String errorMsg = "Verification of the hash for ledger metadata failed! --[HASH=" + key + "]";
            LOGGER.error(errorMsg);
            throw new LedgerException(errorMsg);
        }
        return deserializeMetadata(bytes);
    }


    private LedgerSettings loadAndVerifySettings(HashDigest settingsHash) {
        if (settingsHash == null) {
            return null;
        }
        Bytes key = encodeSettingsKey(settingsHash);
        byte[] bytes = storage.get(key);
        HashFunction hashFunc = Crypto.getHashFunction(settingsHash.getAlgorithm());
        if (!hashFunc.verify(settingsHash, bytes)) {
            String errorMsg = "Verification of the hash for ledger setting failed! --[HASH=" + key + "]";
            LOGGER.error(errorMsg);
            throw new LedgerException(errorMsg);
        }
        return deserializeSettings(bytes);
    }

    private Bytes encodeMetadataKey(HashDigest metadataHash) {
        return metaPrefix.concat(metadataHash);
    }

    private LedgerMetadata_V2 deserializeMetadata(byte[] bytes) {
        return BinaryProtocol.decode(bytes);
    }

    private LedgerSettings deserializeSettings(byte[] bytes) {
        return BinaryProtocol.decode(bytes);
    }

    private Bytes encodeSettingsKey(HashDigest settingsHash) {
        return settingPrefix.concat(settingsHash);
    }

    /**
     * 返回元数据配置信息
     *
     * @return
     */
    @Override
    public LedgerMetadata_V2 getMetadata() {
        return metadata;
    }

    /**
     * 返回当前设置的账本配置；
     *
     * @return
     */
    @Override
    public LedgerSettings getSettings() {
        return settings;
    }

    /**
     * 返回当前参与方的数量
     *
     * @return
     */
    @Override
    public long getParticipantCount() {
        return participants.getParticipantCount();
    }

    /**
     * 返回当前参与方列表
     *
     * @return
     */
    @Override
    public ParticipantNode[] getParticipants() {
        return participants.getParticipants();
    }
}
