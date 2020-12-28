package com.jdchain.samples.contract;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.Event;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.utils.Bytes;

/**
 * 合约样例实现
 */
public class SampleContractImpl implements EventProcessingAware, SampleContract {

    private ContractEventContext eventContext;

    @Override
    public void setKVWithVersion(String address, String key, String value, long version) {
        eventContext.getLedger().dataAccount(Bytes.fromBase58(address)).setText(key, value, version);
    }

    @Override
    public void setKV(String address, String key, String value) {
        // 查询最新版本，初始为-1
        TypedKVEntry[] entries = eventContext.getLedger().getDataEntries(eventContext.getCurrentLedgerHash(), address, key);
        long version = -1;
        if (null != entries && entries.length > 0) {
            version = entries[0].getVersion();
        }
        eventContext.getLedger().dataAccount(Bytes.fromBase58(address)).setText(key, value, version);
    }

    @Override
    public String registerUser(String seed) {
        CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
        SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);
        AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());
        BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
        eventContext.getLedger().users().register(keypair.getIdentity());

        return keypair.getAddress().toBase58();
    }

    @Override
    public String registerDataAccount(String seed) {
        CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
        SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);
        AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());
        BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
        eventContext.getLedger().dataAccounts().register(keypair.getIdentity());

        return keypair.getAddress().toBase58();
    }

    @Override
    public String registerEventAccount(String seed) {
        CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
        SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);
        AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());
        BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
        eventContext.getLedger().eventAccounts().register(keypair.getIdentity());

        return keypair.getAddress().toBase58();
    }

    @Override
    public void publishEventWithSequence(String address, String topic, String content, long sequence) {
        eventContext.getLedger().eventAccount(Bytes.fromBase58(address)).publish(topic, content, sequence);
    }

    @Override
    public void publishEvent(String address, String topic, String content) {
        // 查询最新序号，初始为-1
        Event event = eventContext.getLedger().getLatestEvent(eventContext.getCurrentLedgerHash(), address, topic);
        long sequence = -1;
        if (null != event) {
            sequence = event.getSequence();
        }
        eventContext.getLedger().eventAccount(Bytes.fromBase58(address)).publish(topic, content, sequence);
    }

    /**
     * 合约方法调用前操作
     *
     * @param eventContext
     */
    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
    }

    /**
     * 合约方法调用后操作
     *
     * @param eventContext
     * @param error
     */
    @Override
    public void postEvent(ContractEventContext eventContext, Exception error) {

    }
}
