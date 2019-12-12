package com.jd.blockchain.ump.model.penetrate;

import java.util.List;

/**
 * 数据账户信息
 * @author zhaogw
 * date 2019/7/26 14:49
 */
public class DataAccountSchema {
    private String ledgerHash;
    private String dataAccount;
    private String memo;
    private List<FieldSchema> fieldSchemaList;

    public String getLedgerHash() {
        return ledgerHash;
    }

    public void setLedgerHash(String ledgerHash) {
        this.ledgerHash = ledgerHash;
    }

    public String getDataAccount() {
        return dataAccount;
    }

    public void setDataAccount(String dataAccount) {
        this.dataAccount = dataAccount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<FieldSchema> getFieldSchemaList() {
        return fieldSchemaList;
    }

    public void setFieldSchemaList(List<FieldSchema> fieldSchemaList) {
        this.fieldSchemaList = fieldSchemaList;
    }
}
