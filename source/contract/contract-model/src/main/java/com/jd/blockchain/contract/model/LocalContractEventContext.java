package com.jd.blockchain.contract.model;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.utils.io.ByteArray;

import java.util.Set;

/**
 * @Author zhaogw
 * @Date 2018/9/5 17:43
 */
public class LocalContractEventContext implements ContractEventContext,Cloneable {
    private HashDigest ledgeHash;
    private String event;
    private byte[] chainCode;
    private byte[] args;
    private TransactionRequest transactionRequest;
    private Set<BlockchainIdentity> txSigners;
    private Set<BlockchainIdentity> contractOwners;
    private LedgerContext ledgerContext;

    public LocalContractEventContext(HashDigest ledgeHash, byte[] chainCode, String event){
        this.ledgeHash = ledgeHash;
        this.event = event;
        this.chainCode = chainCode;
    }

    @Override
    public HashDigest getCurrentLedgerHash() {
        return ledgeHash;
    }

    @Override
    public TransactionRequest getTransactionRequest() {
        return transactionRequest;
    }

    @Override
    public Set<BlockchainIdentity> getTxSigners() {
        return txSigners;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public byte[] getArgs() {
        return args;
    }

    @Override
    public LedgerContext getLedger() {
        return ledgerContext;
    }

    @Override
    public Set<BlockchainIdentity> getContracOwners() {
        return null;
    }

    public LocalContractEventContext setLedgeHash(HashDigest ledgeHash) {
        this.ledgeHash = ledgeHash;
        return this;
    }

    public LocalContractEventContext setEvent(String event) {
        this.event = event;
        return this;
    }

    public LocalContractEventContext setTransactionRequest(TransactionRequest transactionRequest) {
        this.transactionRequest = transactionRequest;
        return this;
    }

    public LocalContractEventContext setTxSigners(Set<BlockchainIdentity> txSigners) {
        this.txSigners = txSigners;
        return this;
    }

    public LocalContractEventContext setContractOwners(Set<BlockchainIdentity> contractOwners) {
        this.contractOwners = contractOwners;
        return this;
    }

    public LocalContractEventContext setLedgerContext(LedgerContext ledgerContext) {
        this.ledgerContext = ledgerContext;
        return this;
    }

    public byte[] getChainCode() {
        return chainCode;
    }

    public LocalContractEventContext setChainCode(byte[] chainCode) {
        this.chainCode = chainCode;
        return this;
    }

    public LocalContractEventContext setArgs(byte[] args) {
        this.args = args;
        return this;
    }
}
