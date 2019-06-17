package com.jd.blockchain.sdk.samples;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

public class SDKDemo_Constant {

    public static final String GW_IPADDR = "127.0.0.1";

    public static final int GW_PORT = 11000;

    public static final String[] PUB_KEYS = {
            "3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9",
            "3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX",
            "3snPdw7i7PZi6TStiyc6mzjprnNhgs2atSGNS8wPYzhbKaUWGFJt7x",
            "3snPdw7i7PifPuRX7fu3jBjsb3rJRfDe9GtbDfvFJaJ4V4hHXQfhwk"};

    public static final String[] PRIV_KEYS = {
            "177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x",
            "177gju9p5zrNdHJVEQnEEKF4ZjDDYmAXyfG84V5RPGVc5xFfmtwnHA7j51nyNLUFffzz5UT",
            "177gjtwLgmSx5v1hFb46ijh7L9kdbKUpJYqdKVf9afiEmAuLgo8Rck9yu5UuUcHknWJuWaF",
            "177gk1pudweTq5zgJTh8y3ENCTwtSFsKyX7YnpuKPo7rKgCkCBXVXh5z2syaTCPEMbuWRns"};

    public static final String PASSWORD = "abc";

    public static final byte[] readChainCodes(String contractZip) {
        // 构建合约的字节数组;
        try {
            ClassPathResource contractPath = new ClassPathResource(contractZip);
            File contractFile = new File(contractPath.getURI());
            return FileUtils.readFileToByteArray(contractFile);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
