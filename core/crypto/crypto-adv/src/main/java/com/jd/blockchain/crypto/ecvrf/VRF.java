package com.jd.blockchain.crypto.ecvrf;


import com.jd.blockchain.crypto.CryptoException;
import com.sun.jna.Library;
import com.sun.jna.Native;

import java.security.SecureRandom;
import java.util.Objects;


public class VRF {

    public static String getLib() throws IllegalArgumentException{

        String lib;
        String path;
        String osName = System.getProperty("os.name").toLowerCase();

        // Mac OS
        if (osName.startsWith("mac")){
            lib = "libsodium.23.dylib";
        }

        // Linux OS
        else if (osName.contains("linux")){
            lib = "libsodium.so.23.1.0";
        }

        // unsupported OS
        else {
            throw new CryptoException("The VRF implementation is not supported in this Operation System!");
        }

        path = Objects.requireNonNull(VRF.class.getClassLoader().getResource(lib)).getPath();
        return path;
    }

    public interface CLibrary extends Library {

        CLibrary INSTANCE = (CLibrary)
                Native.load((getLib()), CLibrary.class);

        int crypto_vrf_is_valid_key(byte[] pk);
        int crypto_vrf_keypair_from_seed(byte[] pk, byte[] sk, byte[] seed);
        int crypto_vrf_prove(byte[] proof, byte[] sk, byte[] m, long mlen);
        int crypto_vrf_verify(byte[] output, byte[] pk, byte[] proof, byte[] m, long mlen);
        int crypto_vrf_proof_to_hash(byte[] hash, byte[] proof);
        void crypto_vrf_ietfdraft03_sk_to_pk(byte[] pk, byte[] sk);
    }

    public static byte[] genSecretKey(){
        byte [] seed = new byte[32];
        byte [] sk = new byte[64];
        byte [] pk = new byte[32];
        try{
            SecureRandom.getInstanceStrong().nextBytes(seed);
            CLibrary.INSTANCE.crypto_vrf_keypair_from_seed(pk, sk, seed);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sk;
    }

    public static byte[] sk2pk(byte[] sk){
        if (sk.length != 64){
            return null;
        }
        byte [] pk = new byte[32];
        CLibrary.INSTANCE.crypto_vrf_ietfdraft03_sk_to_pk(pk, sk);
        return pk;
    }

    public static boolean IsValidPk(byte[] pk){
        if (pk.length != 32){
            return false;
        }
        return CLibrary.INSTANCE.crypto_vrf_is_valid_key(pk) == 1;
    }

    public static byte[] prove(byte[] sk, byte[] msg){
        byte[] proof = new byte[80];
        if (CLibrary.INSTANCE.crypto_vrf_prove(proof, sk, msg, msg.length)==0){
            return proof;
        }
        return null;
    }

    public static byte[] proof2hash(byte[] proof){
        byte[] hash = new byte[64];
        if (proof.length != 80){
            return null;
        }
        CLibrary.INSTANCE.crypto_vrf_proof_to_hash(hash, proof);
        return hash;
    }

    public static boolean verify(byte[] pk, byte[] proof, byte[]msg){
        byte[] output = new byte[64];
        if (proof.length != 80 || pk.length != 32){
            return false;
        }
        return CLibrary.INSTANCE.crypto_vrf_verify(output, pk, proof, msg, msg.length) == 0;
    }
}
