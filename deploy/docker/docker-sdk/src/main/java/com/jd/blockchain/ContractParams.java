package com.jd.blockchain;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainKeypair;

public class ContractParams {
    String contractZipName;
    BlockchainKeypair signAdminKey;
    BlockchainIdentity contractIdentity;
    boolean isDeploy;
    boolean isExecute;
    boolean hasVersion; //contract's version;
    long version;
    BlockchainIdentity dataAccount;
    String key;
    String value;

    public String getContractZipName() {
        return contractZipName;
    }

    public ContractParams setContractZipName(String contractZipName) {
        this.contractZipName = contractZipName;
        return this;
    }

    public BlockchainKeypair getSignAdminKey() {
        return signAdminKey;
    }

    public ContractParams setSignAdminKey(BlockchainKeypair signAdminKey) {
        this.signAdminKey = signAdminKey;
        return this;
    }

    public BlockchainIdentity getContractIdentity() {
        return contractIdentity;
    }

    public ContractParams setContractIdentity(BlockchainIdentity contractIdentity) {
        this.contractIdentity = contractIdentity;
        return this;
    }

    public boolean isDeploy() {
        return isDeploy;
    }

    public ContractParams setDeploy(boolean deploy) {
        isDeploy = deploy;
        return this;
    }

    public boolean isExecute() {
        return isExecute;
    }

    public ContractParams setExecute(boolean execute) {
        isExecute = execute;
        return this;
    }

    public boolean isHasVersion() {
        return hasVersion;
    }

    public ContractParams setHasVersion(boolean hasVersion) {
        this.hasVersion = hasVersion;
        return this;
    }

    public long getVersion() {
        return version;
    }

    public ContractParams setVersion(long version) {
        this.version = version;
        return this;
    }

    public BlockchainIdentity getDataAccount() {
        return dataAccount;
    }

    public ContractParams setDataAccount(BlockchainIdentity dataAccount) {
        this.dataAccount = dataAccount;
        return this;
    }

    public String getKey() {
        return key;
    }

    public ContractParams setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ContractParams setValue(String value) {
        this.value = value;
        return this;
    }
}
