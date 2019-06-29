package com.jd.blockchain.contract;


import com.alibaba.fastjson.JSON;

public class ComplexContractImpl implements ComplexContract {
    @Override
    public String read(String address, String key) {
        String json = JSON.toJSONString(address);
        return System.currentTimeMillis() + "" + json;
    }
}
