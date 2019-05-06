/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.converters.BinarySerializeRequestConverter
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/5 下午5:09
 * Description: 序列化请求体
 */
package com.jd.blockchain.sdk.converters;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.consensus.ClientIdentifications;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.transaction.TxRequestMessage;
import com.jd.blockchain.utils.http.RequestBodyConverter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 序列化请求体
 * @author shaozhuguang
 * @create 2018/9/5
 * @since 1.0.0
 */

public class BinarySerializeRequestConverter implements RequestBodyConverter {

    public static final String CONTENT_TYPE_VALUE = "application/bin-obj";

    @Override
    public void write(Object param, OutputStream out) throws IOException {
        // 使用自定义的序列化方式
        if (param instanceof TransactionRequest) {
            byte[] serializeBytes = BinaryProtocol.encode(param, TransactionRequest.class);
            out.write(serializeBytes);
            out.flush();
        } else if (param instanceof ClientIdentifications) {
            byte[] serializeBytes = BinaryProtocol.encode(param, ClientIdentifications.class);
            out.write(serializeBytes);
            out.flush();
        }
    }
}