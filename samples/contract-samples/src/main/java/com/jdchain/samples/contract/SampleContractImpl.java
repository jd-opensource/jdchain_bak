package com.jdchain.samples.contract;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.base.DefaultCryptoEncoding;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.ledger.*;

import com.jd.blockchain.transaction.SimpleSecurityOperationBuilder;
import utils.Bytes;

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
        // 查询已提交区块数据，不包括此操作所在未提交区块的所有数据
        // TypedKVEntry[] entries = eventContext.getLedger().getDataEntries(eventContext.getCurrentLedgerHash(), address, key);
        // 可查询包括此操作所在未提交区块的所有数据
        TypedKVEntry[] entries = eventContext.getUncommittedLedger().getDataEntries(address, key);
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
        // 查询已提交区块数据，不包括此操作所在未提交区块的所有数据
        // Event event = eventContext.getLedger().getRuntimeLedger().getLatestEvent(address, topic);
        // 可查询包括此操作所在未提交区块的所有数据
        Event event = eventContext.getUncommittedLedger().getLatestEvent(address, topic);
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

    @Override
    public void createRoleAndPermissions(String role, String ledgerPermissionSemicolonStr, String txPermissionSemicolonStr) {

        SimpleSecurityOperationBuilder.SimpleRoleConfigurer roleConfigurer = eventContext.getLedger().security().role(role);

        if(ledgerPermissionSemicolonStr != null){
            for(String perm : ledgerPermissionSemicolonStr.split(";")){
                LedgerPermission permission = LedgerPermission.valueOf(perm.trim().toUpperCase());
                roleConfigurer.enable(permission);
            }
        }

        if(txPermissionSemicolonStr != null){
            for(String perm : txPermissionSemicolonStr.split(";")){
                TransactionPermission permission = TransactionPermission.valueOf(perm.trim().toUpperCase());
                roleConfigurer.enable(permission);
            }
        }

    }

    @Override
    public void registerUserByPubKey(String pubkey) {
        PubKey pubKey = DefaultCryptoEncoding.createPubKey(ClassicAlgorithm.ED25519.code(), Bytes.fromBase58(pubkey).toBytes());
        BlockchainIdentityData identityData = new BlockchainIdentityData(pubKey);
        eventContext.getLedger().users().register(identityData);
    }

    @Override
    public void modifyUserRole(String address, String role) {
        eventContext.getLedger()
                .security()
                .authorziation(Bytes.fromBase58(address))
                .authorize(role);

    }

    @Override
    public void modifyUserState(String userAddress, String state) {
        AccountState accountState = AccountState.valueOf(state.trim().toUpperCase());
        eventContext.getLedger().user(userAddress)
                .state(accountState);
    }

    @Override
    public void modifyDataAccountRoleAndMode(String dataAccountAddress, String role, String mode) {
        eventContext.getLedger().dataAccount(dataAccountAddress)
                .permission()
                .role(role)
                .mode(Integer.parseInt(mode));
    }

    @Override
    public void setKV(String dataAccountAddress, String key, String value, String version) {
        eventContext.getLedger().dataAccount(dataAccountAddress)
                .setText(key, value, Integer.parseInt(version));
    }

    @Override
    public void modifyEventAccountRoleAndMode(String eventAccountAddress, String role, String mode) {
        eventContext.getLedger().eventAccount(eventAccountAddress)
                .permission()
                .role(role)
                .mode(Integer.parseInt(mode));
    }

    @Override
    public void publishEventAccount(String eventAccountAddress, String eventName, String value, String sequence) {
        eventContext.getLedger().eventAccount(eventAccountAddress)
                .publish(eventName, value, Integer.parseInt(sequence));
    }

    @Override
    public void invokeContract(String contractAddress, String method, String argSemicolonStr) {
        String[] args = argSemicolonStr.split(";");
        BytesValue[] bytesValues = new BytesValue[args.length];

        for(int i = 0; i < args.length; i++){
            bytesValues[i] = TypedValue.fromText(args[i]);
        }

        eventContext.getLedger()
                .contract(contractAddress)
                .invoke(method, new BytesValueList() {
                    @Override
                    public BytesValue[] getValues() {
                        return bytesValues;
                    }
                });
    }

    @Override
    public String deployContract(String pubkey, byte[] carBytes) {

        PubKey pubKey = KeyGenUtils.decodePubKey(pubkey);

        ContractCodeDeployOperation deployOperation = eventContext.getLedger().contracts()
                .deploy(new BlockchainIdentityData(pubKey), carBytes);

        return deployOperation.getContractID().getAddress().toString();
    }

    @Override
    public void modifyContractRoleAndMode(String contractAddress, String role, String mode) {
        eventContext.getLedger().contract(contractAddress)
                .permission()
                .role(role)
                .mode(Integer.parseInt(mode));
    }

    @Override
    public void modifyContractState(String contractAddress, String state) {
        AccountState accountState = AccountState.valueOf(state.trim().toUpperCase());
        eventContext.getLedger().contract(contractAddress)
                .state(accountState);
    }



}
