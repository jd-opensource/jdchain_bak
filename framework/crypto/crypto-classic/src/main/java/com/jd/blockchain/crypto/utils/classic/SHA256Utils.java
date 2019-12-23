package com.jd.blockchain.crypto.utils.classic;

import org.bouncycastle.crypto.digests.SHA256Digest;

/**
 * @author zhanglin33
 * @title: SHA256Utils
 * @description: SHA256 hash algorithm
 * @date 2019-04-09, 14:28
 */
public class SHA256Utils {

    // The length of SHA256 output is 32 bytes
    public static final int SHA256DIGEST_LENGTH = 256 / 8;

    public static byte[] hash(byte[] data){

        byte[] result = new byte[SHA256DIGEST_LENGTH];
        SHA256Digest sha256Digest = new SHA256Digest();

        sha256Digest.update(data,0,data.length);
        sha256Digest.doFinal(result,0);
        return result;
    }
    
    public static byte[] hash(byte[] data, int offset, int len){
    	
    	byte[] result = new byte[SHA256DIGEST_LENGTH];
    	SHA256Digest sha256Digest = new SHA256Digest();
    	
    	sha256Digest.update(data, offset, len);
    	sha256Digest.doFinal(result,0);
    	return result;
    }
}
