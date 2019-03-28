package com.jd.blockchain.crypto.jniutils.asymmetric;


import com.jd.blockchain.crypto.jniutils.hash.JNISHA256Utils;

import java.util.Objects;

public class JNIED25519Utils {

        /* load c library */
        static {
            //differentiate OS
            String osName = System.getProperty("os.name").toLowerCase();
            String path="";
            // Windows OS
            if (osName.startsWith("windows")){
                path = Objects.requireNonNull(JNIED25519Utils.class.getClassLoader().getResource("com/jd/blockchain/crypto/jniutils/asymmetric/c_ed25519.dll")).getPath();
            }
            // Linux OS
            else if (osName.contains("linux")){
                path = Objects.requireNonNull(JNIED25519Utils.class.getClassLoader().getResource("com/jd/blockchain/crypto/jniutils/asymmetric/libc_ed25519.so")).getPath();
            }
            // Mac OS
            else if (osName.contains("mac")){
                path = Objects.requireNonNull(JNISHA256Utils.class.getClassLoader().getResource("com/jd/blockchain/crypto/jniutils/asymmetric/libc_ed25519.jnilib")).getPath();
            }

            System.load(path);
    }

        /* define java native method */
        public native void generateKeyPair(byte[] privKey, byte[] pubKey);
        public native byte[] getPubKey(byte[] privKey);
        public native byte[] sign(byte[] msg, byte[] privKey, byte[] pubKey);
        public native boolean verify(byte[] msg, byte[] pubKey, byte[] signature);
    }

