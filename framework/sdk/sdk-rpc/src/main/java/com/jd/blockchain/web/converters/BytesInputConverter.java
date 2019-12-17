/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.gateway.converters.HashDigestInputConverter
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/11 下午5:05
 * Description:
 */
package com.jd.blockchain.web.converters;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.codec.Base58Utils;

import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author shaozhuguang
 * @create 2018/9/11
 * @since 1.0.0
 */

public class BytesInputConverter implements Converter<String, HashDigest> {

    @Override
    public HashDigest convert(String inText) {
        byte[] hashBytes = Base58Utils.decode(inText);
        return new HashDigest(hashBytes);
    }
}