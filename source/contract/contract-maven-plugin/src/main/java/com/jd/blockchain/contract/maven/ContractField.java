package com.jd.blockchain.contract.maven;

public class ContractField extends AbstractContract {

    private String fieldName;

    private String fieldType;

    private boolean isStatic;

    public ContractField(String className, String fieldName, String fieldType) {
        this(className, fieldName, fieldType, false);
    }

    public ContractField(String className, String fieldName, String fieldType, boolean isStatic) {
        this.className = format(className);
        this.fieldName = fieldName;
        this.fieldType = format(fieldType);
        this.isStatic = isStatic;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public String toString() {
        return "ContractField{" +
                "className='" + className + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", isStatic=" + isStatic +
                '}';
    }
}
