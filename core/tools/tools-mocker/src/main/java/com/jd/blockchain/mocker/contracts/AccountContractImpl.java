package com.jd.blockchain.mocker.contracts;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.KVDataEntry;

public class AccountContractImpl implements EventProcessingAware, AccountContract {

    private ContractEventContext eventContext;

    private HashDigest ledgerHash;

    @Override
    public void create(String address, String account, long money) {
        // 暂不处理该账户已经存在的问题
        eventContext.getLedger().dataAccount(address).setInt64(account, money, -1);
    }

    @Override
    public void transfer(String address, String from, String to, long money) {
        // 首先分别查询from与to的结果
        KVDataEntry[] dataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, from, to);
        long currentFromMoney = 0L, currentToMoney = 0L, currentFromVer = -1L, currentToVer = -1L;
        if (dataEntries != null && dataEntries.length > 0) {
            for (KVDataEntry dataEntry : dataEntries) {
                String key = dataEntry.getKey();
                Object value = dataEntry.getValue();
                long version = dataEntry.getVersion();
                if (key.equals(from)) {
                    currentFromMoney = (long) value;
                    currentFromVer = version;
                }
                if (key.equals(to)) {
                    currentToMoney = (long) value;
                    currentToVer = version;
                }
            }
        }
        currentFromMoney -= money;
        currentToMoney += money;
        // 重新设置结果
        eventContext.getLedger().dataAccount(address).setInt64(from, currentFromMoney, currentFromVer)
                .setInt64(to, currentToMoney, currentToVer);
    }

    @Override
    public void print(String address, String from, String to) {
        KVDataEntry[] dataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, from, to);
        if (dataEntries != null && dataEntries.length > 0) {
            for (KVDataEntry dataEntry : dataEntries) {
                String key = dataEntry.getKey();
                Object value = dataEntry.getValue();
                long version = dataEntry.getVersion();
                System.out.printf("Key = %s Value = %s Version = %s \r\n", key, value, version);
            }
        }
    }

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
        this.ledgerHash = this.eventContext.getCurrentLedgerHash();
    }

    @Override
    public void postEvent(ContractEventContext eventContext, Exception error) {

    }

}
