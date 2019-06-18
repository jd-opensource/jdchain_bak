package com.jd.blockchain.contract;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.KVDataVO;
import com.jd.blockchain.ledger.KVInfoVO;

public class TransferContractImpl implements EventProcessingAware, TransferContract {

    private ContractEventContext eventContext;

    private HashDigest ledgerHash;

    @Override
    public String create(String address, String account, long money) {
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, account);
        // 肯定有返回值，但若不存在则返回version=-1
        if (kvDataEntries != null && kvDataEntries.length > 0) {
            long currVersion = kvDataEntries[0].getVersion();
            if (currVersion > -1) {
                throw new IllegalStateException(String.format("%s -> %s already have created !!!", address, account));
            }
            eventContext.getLedger().dataAccount(address).setInt64(account, money, -1L);
        } else {
            throw new IllegalStateException(String.format("Ledger[%s] inner Error !!!", ledgerHash.toBase58()));
        }
        return String.format("DataAccountAddress[%s] -> Create(By Contract Operation) Account = %s and Money = %s Success!!! \r\n",
                address, account, money);
    }

    @Override
    public String transfer(String address, String from, String to, long money) {
        // 首先查询余额
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, from, to);
        if (kvDataEntries == null || kvDataEntries.length != 2) {
            throw new IllegalStateException(String.format("%s -> %s - %s may be not created !!!", address, from, to));
        } else {
            // 判断from账号中钱数量是否足够
            long fromMoney = 0L, toMoney = 0L, fromVersion = 0L, toVersion = 0L;
            for (KVDataEntry kvDataEntry : kvDataEntries) {
                if (kvDataEntry.getKey().equals(from)) {
                    fromMoney = (long) kvDataEntry.getValue();
                    fromVersion = kvDataEntry.getVersion();
                } else {
                    toMoney = (long) kvDataEntry.getValue();
                    toVersion = kvDataEntry.getVersion();
                }
            }
            if (fromMoney < money) {
                throw new IllegalStateException(String.format("%s -> %s not have enough money !!!", address, from));
            }
            long fromNewMoney = fromMoney - money;
            long toNewMoney = toMoney + money;
            // 重新设置
            eventContext.getLedger().dataAccount(address).setInt64(from, fromNewMoney, fromVersion);
            eventContext.getLedger().dataAccount(address).setInt64(to, toNewMoney, toVersion);
        }

        return String.format("DataAccountAddress[%s] transfer from [%s] to [%s] and [money = %s] Success !!!", address, from, to, money);
    }

    @Override
    public long read(String address, String account) {
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, account);
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            return -1;
        }
        return (long)kvDataEntries[0].getValue();
    }

    @Override
    public String readAll(String address, String account) {
        KVDataEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, account);
        // 获取最新的版本号
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            return "";
        }
        long newestVersion = kvDataEntries[0].getVersion();
        if (newestVersion == -1) {
            return "";
        }
        KVDataVO[] kvDataVOS = new KVDataVO[1];
        long[] versions = new long[(int)newestVersion + 1];
        for (int i = 0; i < versions.length; i++) {
            versions[i] = i;
        }
        KVDataVO kvDataVO = new KVDataVO(account, versions);

        kvDataVOS[0] = kvDataVO;

        KVInfoVO kvInfoVO = new KVInfoVO(kvDataVOS);

        KVDataEntry[] allEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, kvInfoVO);

        return JSON.toJSONString(allEntries);
    }

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
        this.ledgerHash = eventContext.getCurrentLedgerHash();
    }

    @Override
    public void postEvent(ContractEventContext eventContext, Exception error) {

    }
}
