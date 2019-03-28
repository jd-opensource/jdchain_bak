package com.jd.blockchain.binaryproto.impl;

import java.util.HashMap;
import java.util.Map;

import com.jd.blockchain.binaryproto.EnumSpecification;

/**
 * Created by zhangshuang3 on 2018/6/27.
 */
public class EnumContractRegistry {
    private static Map<String, EnumSpecification> enumSpecs  = new HashMap<String, EnumSpecification>();

    public EnumContractRegistry() {
    }

    public static EnumSpecification getEnumSpec(Class<?> contractType) {
        //find encoder from dataSpecs by contractType
        for (String key : enumSpecs.keySet())
        {
            if (key.equals(contractType.getName())) {
                return enumSpecs.get(key);
            }
        }
        return null;
    }

    public static void setEnumSpecs(String key, EnumSpecification value) {
        enumSpecs.put(key, value);
    }

}
