package com.jd.blockchain.ump.model.penetrate;

import com.alibaba.fastjson.annotation.JSONField;
import com.jd.blockchain.ump.model.UmpConstant;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zhaogw
 * date 2019/7/19 11:33
 */
public class SchemaDomain {

    @JSONField(serialize = false)
    private String schemaId;

    @JSONField(serialize = false)
    private String schemaAllId;

    @JSONField(name="ledger")
    private String ledgerHash;

    @JSONField(name="associate_account")
    private String dataAccount;

    @JSONField(serialize = false)
    private List<FieldSchema> fieldSchemaList;

    private String content;

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

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

    public String getContent() {
        if(CollectionUtils.isEmpty(fieldSchemaList)){
            throw new IllegalStateException("content is empty! you must choose the field first!");
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("type "+this.schemaId).append("{").append(" ");
        for(FieldSchema fieldSchema : fieldSchemaList){
            if(fieldSchema.isPrimary()){
                stringBuffer.append(fieldSchema.getCode()+"(isPrimaryKey: Boolean = true):"+fieldSchema.getFieldType()).append(" ");
            }else {
                stringBuffer.append(fieldSchema.getCode()+":"+fieldSchema.getFieldType()).append(" ");
            }
        }
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<FieldSchema> getFieldSchemaList() {
        return fieldSchemaList;
    }

    public void setFieldSchemaList(List<FieldSchema> fieldSchemaList) {
        this.fieldSchemaList = fieldSchemaList;
    }

    public String getSchemaAllId() {
        this.schemaAllId = schemaId+ UmpConstant.DELIMETER_MINUS+ledgerHash.substring(0,6)+
                UmpConstant.DELIMETER_MINUS+ dataAccount.substring(0,6);
        return schemaAllId;
    }
}
