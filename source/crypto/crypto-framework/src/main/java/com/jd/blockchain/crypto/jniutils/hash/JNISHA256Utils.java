package com.jd.blockchain.crypto.jniutils.hash;

import java.util.Objects;

public class JNISHA256Utils {

    /* load c library */
    static {
        //differentiate OS
        String osName = System.getProperty("os.name").toLowerCase();
        String path="";
        // Windows OS
        if (osName.startsWith("windows")){
            path = Objects.requireNonNull(JNISHA256Utils.class.getClassLoader().getResource("com/jd/blockchain/crypto/jniutils/hash/c_sha256.dll")).getPath();
        }
        // Linux OS
        else if (osName.contains("linux")){
            path = Objects.requireNonNull(JNISHA256Utils.class.getClassLoader().getResource("com/jd/blockchain/crypto/jniutils/hash/libc_sha256.so")).getPath();
        }
        // Mac OS
        else if (osName.contains("mac")){
            path = Objects.requireNonNull(JNISHA256Utils.class.getClassLoader().getResource("com/jd/blockchain/crypto/jniutils/hash/libc_sha256.jnilib")).getPath();
        }

        System.load(path);
    }

    /* define java native method */
    public native byte[] hash(byte[] msg);
}
