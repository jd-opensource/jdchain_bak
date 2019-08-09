package com.jd.blockchain.contract;

import com.jd.blockchain.crypto.HashDigest;

import java.util.Random;

public class RandomContractImpl implements RandomContract, EventProcessingAware {

    private static final Random RANDOM_TIME = new Random();

    private ContractEventContext eventContext;

    private HashDigest ledgerHash;

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
        this.ledgerHash = eventContext.getCurrentLedgerHash();
    }

    @Override
    public void postEvent(ContractEventContext eventContext, Exception error) {

    }

    @Override
    public void put(String address, String key, String value) {

        String saveVal = value + "-" + RANDOM_TIME.nextInt(1024);

        eventContext.getLedger().dataAccount(address).setText(key, saveVal, -1L);
    }

    @Override
    public String putAndGet(String address, String key, String value) {

        String saveVal = value + "-" + RANDOM_TIME.nextInt(1024);

        eventContext.getLedger().dataAccount(address).setText(key, saveVal, -1L);

        return address;
    }
}
