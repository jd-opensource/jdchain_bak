//package com.jd.blockchain.binaryproto.impl;
//
//
//import org.omg.CORBA.PUBLIC_MEMBER;
//
//import com.jd.blockchain.binaryproto.DataSpecification;
//import com.jd.blockchain.binaryproto.EnumSpecification;
//import com.jd.blockchain.binaryproto.FieldSpec;
//
//import my.utils.ValueType;
//
//import java.lang.reflect.Method;
//
///**
// * Created by zhangshuang3 on 2018/6/21.
// */
//public class FieldSpecImpl implements FieldSpec {
//    private int typeCode;
//    private ValueType primitiveType;
//    private EnumSpecification enumSpec;
//    private DataSpecification dataSpec;
//    private boolean isRefPubKey;
//    private boolean isRefPrivKey;
//    private boolean isRefHashDigest;
//    private boolean isRefSignatureDigest;
//    private boolean isRefIdentity;
//    private boolean isRefNetworkAddr;
//    private String name;
//    private String description;
//    private boolean isList;
//    private int maxLength;
//    private Class<?> contractTypeResolver;
//    private Method readMethod;
//
//    public FieldSpecImpl() {
//
//    }
//    public Method getReadMethod() {
//        return readMethod;
//    }
//    public void setReadMethod(Method readMethod) {
//        this.readMethod = readMethod;
//        readMethod.setAccessible(true);
//    }
//    @Override
//    public int getTypeCode() {return typeCode;}
//    public void setTypeCode(int typeCode) {this.typeCode = typeCode;}
//
//    @Override
//    public ValueType getPrimitiveType() {return primitiveType;}
//    public void setPrimitiveType(ValueType primitiveType) {
//        this.primitiveType = primitiveType;
//    }
//    @Override
//    public EnumSpecification getRefEnum() {return enumSpec;}
//    public void setRefEnum(EnumSpecification enumSpec) {
//        this.enumSpec = enumSpec;
//    }
//
//    @Override
//    public DataSpecification getRefContract() {return dataSpec;}
//    public void setRefContract(DataSpecification dataSpec) {
//        this.dataSpec = dataSpec;
//    }
//
//    @Override
//    public boolean isRepeatable() {return isList;}
//    public void setIsList(boolean isList) {this.isList = isList;}
//
//    @Override
//    public int getMaxSize() {return maxLength;}
//    public void setMaxLength(int length) {
//        this.maxLength = maxLength;
//    }
//    @Override
//    public String getName() {return name;}
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public String getDescription() {return description;}
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    @Override
//    public boolean isRefPubKey() {
//        return isRefPubKey;
//    }
//    public void setRefPubKey(boolean pubKey) {
//        this.isRefPubKey = pubKey;
//    }
//
//    @Override
//    public boolean isRefPrivKey() {
//        return isRefPrivKey;
//    }
//    public void setRefPrivKey(boolean privKey) {
//        this.isRefPrivKey = privKey;
//    }
//
//    @Override
//    public boolean isRefSignatureDigest() {
//        return isRefSignatureDigest;
//    }
//    public void setRefSignatureDigest(boolean signatureDigest) {
//        this.isRefSignatureDigest = signatureDigest;
//    }
//
//    @Override
//    public boolean isRefHashDigest() {
//        return isRefHashDigest;
//    }
//    public void setRefHashDigest(boolean hashDigest) {
//        this.isRefHashDigest = hashDigest;
//    }
//
//    @Override
//    public Class<?> getContractTypeResolver() {
//        return this.contractTypeResolver;
//    }
//    public void setContractTypeResolver(Class<?> resolver) {
//        this.contractTypeResolver = resolver;
//    }
//
//    @Override
//    public boolean isRefIdentity() {
//        return isRefIdentity;
//    }
//    public void  setRefIdentity(boolean identity) {
//        this.isRefIdentity = identity;
//    }
//
//    @Override
//    public boolean isRefNetworkAddr() {
//        return isRefNetworkAddr;
//    }
//    public void setRefNetworkAddr(boolean networkAddr) {
//        this.isRefNetworkAddr = networkAddr;
//    }
//}
