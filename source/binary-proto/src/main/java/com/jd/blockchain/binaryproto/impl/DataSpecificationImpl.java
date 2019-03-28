//package com.jd.blockchain.binaryproto.impl;
//
//import java.lang.reflect.Method;
//import java.util.*;
//
//import com.jd.blockchain.binaryproto.BinarySliceSpec;
//import com.jd.blockchain.binaryproto.DataField;
//import com.jd.blockchain.binaryproto.DataSpecification;
//import com.jd.blockchain.binaryproto.FieldSpec;
//
///**
// * Created by zhangshuang3 on 2018/6/21.
// */
//public class DataSpecificationImpl implements  DataSpecification {
//    private int code;
//    private long version;
//    private String name;
//    private String description;
//    private List<FieldSpec> fieldList = new ArrayList<FieldSpec>();
//    private List<BinarySliceSpec> sliceList = new ArrayList<>();
//
//
//    public DataSpecificationImpl() {
//
//    }
//    //sort method by order id
//    public Map<FieldSpec, Method> sortMapByValues (Map<FieldSpec, Method> mths) {
//        Set<Map.Entry<FieldSpec,Method>> mapEntries = mths.entrySet();
//        List<Map.Entry<FieldSpec,Method>> aList = new LinkedList<Map.Entry<FieldSpec, Method>>(mapEntries);
//        //sort list
//        Collections.sort(aList, new Comparator<Map.Entry<FieldSpec, Method>>() {
//            @Override
//            public int compare(Map.Entry<FieldSpec, Method> ele1,
//                               Map.Entry<FieldSpec, Method> ele2) {
//                return (ele1.getValue().getAnnotation(DataField.class).order()) - (ele2.getValue().getAnnotation(DataField.class).order());
//            }
//        });
//        int count = aList.size();
//        //init Capacity
//        Map<FieldSpec, Method> aMap = new LinkedHashMap<FieldSpec, Method>(count);
//        for(Map.Entry<FieldSpec, Method> entry: aList) {
//            aMap.put(entry.getKey(), entry.getValue());
//        }
//        return aMap;
//    }
//
//    @Override
//    public int getCode() {
//        return code;
//    }
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    @Override
//    public long getVersion() {
//        return version;
//    }
//    public void setVersion(long version) {
//        this.version = version;
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public String getDescription() {
//        return description;
//    }
//    public void setDescription(String description) {this.description = description;}
//
//    @Override
//    public List<FieldSpec> getFields() {
//        return fieldList;
//    }
//    public void setFields(FieldSpec field) {this.fieldList.add(field);}
//
//    @Override
//    public List<BinarySliceSpec> getSlices() {
//        return sliceList;
//    }
//    public void setSlices(BinarySliceSpec slice) {
//        this.sliceList.add(slice);
//    }
//    @Override
//    public String toHtml() {
//        return null;
//    }
//}
