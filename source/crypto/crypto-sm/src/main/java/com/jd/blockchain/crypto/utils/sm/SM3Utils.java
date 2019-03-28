package com.jd.blockchain.crypto.utils.sm;

import org.bouncycastle.crypto.digests.SM3Digest;

public class SM3Utils {

    // The length of sm3 output is 32 bytes
    private static final int SM3DIGEST_LENGTH = 32;

    public static byte[] hash(byte[] data) {

        byte[] result = new byte[SM3DIGEST_LENGTH];

        SM3Digest sm3digest = new SM3Digest();

        sm3digest.update(data, 0, data.length);
        sm3digest.doFinal(result, 0);

        return result;
    }
}

