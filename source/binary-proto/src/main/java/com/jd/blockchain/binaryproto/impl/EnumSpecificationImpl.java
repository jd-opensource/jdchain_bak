//package com.jd.blockchain.binaryproto.impl;
//
//import java.lang.reflect.Method;
//import java.util.*;
//
//import com.jd.blockchain.binaryproto.EnumSpecification;
//
//import my.utils.ValueType;
//
///**
// * Created by zhangshuang3 on 2018/6/21.
// */
//public class EnumSpecificationImpl implements  EnumSpecification {
//    private int code;
//    private long version;
//    private String name;
//    private String description;
//    private ValueType item;
//    private Set<Integer> intSet = new LinkedHashSet<>();
//    private Set<String> stringSet = new LinkedHashSet<>();
//    private Map<Object, Integer> readEnumConstants = new HashMap<Object, Integer>();
//
//    public EnumSpecificationImpl(int code, String name, String description) {
//        this.code = code;
//        this.name = name;
//        this.description = description;
//    }
//    @Override
//    public int getCode() {
//        return this.code;
//    }
//    public void setCode(int code) {
//        this.code = code;
//    }
//    @Override
//    public long getVersion(){
//        return this.version;
//    }
//    public void setVersion(long version) {
//        this.version = version;
//    }
//    @Override
//    public String getName() {
//        return this.name;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//    @Override
//    public String getDescription() {
//        return this.description;
//    }
//    public void setDescription(String description) {
//        this.description = description;
//    }
//    @Override
//    public ValueType getValueType() {
//        return this.item;
//    }
//    public void setItemType(ValueType item) {
//        this.item = item;
//    }
//    @Override
//    public Set<Integer> getItemValues() {
//        return this.intSet;
//    }
//    public void setItemValues(Integer item) {
//        this.intSet.add(item);
//    }
//    @Override
//    public Set<String> getItemNames() {
//        return this.stringSet;
//    }
//    public void setItemNames(String item) {
//        this.stringSet.add(item);
//    }
//
//    public  Map<Object, Integer> getEnumConstants() {
//        return this.readEnumConstants;
//    }
//    public void setEnumConstants(Object enumConstant , Integer code) {
//        this.readEnumConstants.put(enumConstant, code);
//    }
//}
