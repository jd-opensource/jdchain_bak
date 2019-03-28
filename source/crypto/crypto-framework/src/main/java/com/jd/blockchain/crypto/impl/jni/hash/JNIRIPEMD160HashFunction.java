package com.jd.blockchain.crypto.impl.jni.hash;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.crypto.jniutils.hash.JNIRIPEMD160Utils;

import java.util.Arrays;

import static com.jd.blockchain.crypto.CryptoAlgorithm.JNIRIPEMD160;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_BYTES;

public class JNIRIPEMD160HashFunction implements HashFunction {

    private static final int DIGEST_BYTES = 160 / 8;

    private static final int DIGEST_LENGTH = ALGORYTHM_BYTES + DIGEST_BYTES;

    @Override
    public CryptoAlgorithm getAlgorithm() {
        return JNIRIPEMD160;
    }

    @Override
    public HashDigest hash(byte[] data) {

        if (data == null)
            throw new IllegalArgumentException("This data is null!");

        JNIRIPEMD160Utils ripemd160 = new JNIRIPEMD160Utils();
        byte[] digestBytes = ripemd160.hash(data);
        return new HashDigest(JNIRIPEMD160, digestBytes);
    }

    @Override
    public boolean verify(HashDigest digest, byte[] data) {
        HashDigest hashDigest = hash(data);
        return Arrays.equals(hashDigest.toBytes(), digest.toBytes());
    }

    @Override
    public boolean supportHashDigest(byte[] digestBytes) {
        // 验证输入字节数组长度=算法标识长度+摘要长度，字节数组的算法标识对应JNIRIPEMD160算法
        return digestBytes.length == DIGEST_LENGTH && JNIRIPEMD160.CODE == digestBytes[0];
    }

    @Override
    public HashDigest resolveHashDigest(byte[] digestBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new HashDigest(digestBytes);
    }
}

