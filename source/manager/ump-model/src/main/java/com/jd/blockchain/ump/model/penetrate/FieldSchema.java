package com.jd.blockchain.ump.model.penetrate;

/**
 * ump中记录的字段信息;
 * @author zhaogw
 * date 2019/7/26 14:50
 */
public class FieldSchema {
    private String code;
    private String fieldType;
    private boolean isPrimary;
    //备注;
    private String memo;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
