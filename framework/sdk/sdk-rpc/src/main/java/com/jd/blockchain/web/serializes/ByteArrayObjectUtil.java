/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.web.serializes.ByteArrayObjectUtil
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/3/27 上午11:23
 * Description:
 */
package com.jd.blockchain.web.serializes;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

/**
 *
 * @author shaozhuguang
 * @create 2019/3/27
 * @since 1.0.0
 */

public class ByteArrayObjectUtil {

    public static final Class<?>[] BYTEARRAY_JSON_SERIALIZE_CLASS = new Class<?>[] {
            HashDigest.class,
            PubKey.class,
            SignatureDigest.class,
            Bytes.class,
            BytesSlice.class};

    public static void init() {
        for (Class<?> byteArrayClass : BYTEARRAY_JSON_SERIALIZE_CLASS) {
            JSONSerializeUtils.configSerialization(byteArrayClass,
                    ByteArrayObjectJsonSerializer.getInstance(byteArrayClass),
                    ByteArrayObjectJsonDeserializer.getInstance(byteArrayClass));
        }
    }
}