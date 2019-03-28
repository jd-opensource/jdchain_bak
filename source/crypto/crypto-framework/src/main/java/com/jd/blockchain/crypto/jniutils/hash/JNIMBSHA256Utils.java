package com.jd.blockchain.crypto.jniutils.hash;

import java.util.Objects;

public class JNIMBSHA256Utils {
    /* load c library */
    static{
        //differentiate OS
        String osName = System.getProperty("os.name").toLowerCase();
        String pathOfSo;
        String pathOfSo2;
        if (osName.contains("linux")){
            pathOfSo = Objects.requireNonNull(JNIMBSHA256Utils.class.getClassLoader().getResource("com/jd/blockchain/crypto/jniutils/hash/libc_mbsha256.so")).getPath();
            pathOfSo2 = Objects.requireNonNull(JNIMBSHA256Utils.class.getClassLoader().getResource("com/jd/blockchain/crypto/jniutils/hash/libisal_crypto.so.2")).getPath();
        }
        else throw new IllegalArgumentException("The JNIMBSHA256 implementation is not supported in this Operation System!");

        System.load(pathOfSo2);
        System.load(pathOfSo);
    }

    /* define java native method */
    public native byte[][] multiBufferHash(byte[][] multiMsgs);
}
