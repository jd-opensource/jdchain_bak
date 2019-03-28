package com.jd.blockchain.crypto.impl.jni.hash;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.crypto.jniutils.hash.JNISHA256Utils;

import java.util.Arrays;

import static com.jd.blockchain.crypto.CryptoAlgorithm.JNISHA256;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_BYTES;

public class JNISHA256HashFunction implements HashFunction {

    private static final int DIGEST_BYTES = 256/8;

    private static final int DIGEST_LENGTH = ALGORYTHM_BYTES + DIGEST_BYTES;

    @Override
    public CryptoAlgorithm getAlgorithm() {
        return JNISHA256;
    }

    @Override
    public HashDigest hash(byte[] data) {

        if (data == null)
            throw new IllegalArgumentException("This data is null!");

        JNISHA256Utils sha256 = new JNISHA256Utils();
        byte[] digestBytes = sha256.hash(data);
        return new HashDigest(JNISHA256,digestBytes);
    }

    @Override
    public boolean verify(HashDigest digest, byte[] data) {
        HashDigest hashDigest=hash(data);
        return Arrays.equals(hashDigest.toBytes(),digest.toBytes());
    }

    @Override
    public boolean supportHashDigest(byte[] digestBytes) {
        // 验证输入字节数组长度=算法标识长度+摘要长度，字节数组的算法标识对应JNISHA256算法
        return digestBytes.length == DIGEST_LENGTH && JNISHA256.CODE == digestBytes[0];
    }

    @Override
    public HashDigest resolveHashDigest(byte[] hashDigestBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
        return new HashDigest(hashDigestBytes);
    }

}
