package com.jd.blockchain.contract.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContractMethod extends AbstractContract {

    private String methodName;

    private String[] paramTypes;

    private String[] returnTypes;

    private List<ContractField> fieldList = new ArrayList<>();

    private List<ContractMethod> methodList = new ArrayList<>();

    public ContractMethod(String className, String methodName) {
        this(className, methodName, null, null);
    }

    public ContractMethod(String className, String methodName, String[] paramTypes, String[] returnTypes) {
        this.className = format(className);
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.returnTypes = returnTypes;
    }

    public void addMethod(String className, String methodName, String[] paramTypes, String[] returnTypes) {
        methodList.add(new ContractMethod(className, methodName, paramTypes, returnTypes));
    }

    public void addField(String className, String fieldName, String fieldType) {
        this.fieldList.add(new ContractField(className, fieldName, fieldType));
    }

    public void addStaticField(String className, String fieldName, String fieldType) {
        this.fieldList.add(new ContractField(className, fieldName, fieldType, true));
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getParamTypes() {
        return paramTypes;
    }

    public List<ContractField> getAllFieldList() {
        return fieldList;
    }

    public List<ContractField> getClassFieldList(String cName) {
        List<ContractField> classFieldList = new ArrayList<>();
        if (!fieldList.isEmpty()) {
            for (ContractField field : fieldList) {
                if (field.getClassName().equals(cName)) {
                    classFieldList.add(field);
                }
            }
        }
        return classFieldList;
    }

    public List<ContractMethod> getMethodList() {
        return methodList;
    }

    @Override
    public String toString() {
        return "ContractMethod{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", returnTypes=" + Arrays.toString(returnTypes) +
                ", fieldList=" + fieldList +
                ", methodList=" + methodList +
                '}';
    }
}
