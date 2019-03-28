//package com.jd.blockchain.crypto.impl;
//
//import com.jd.blockchain.crypto.Ciphertext;
//import com.jd.blockchain.crypto.CryptoAlgorithm;
//import com.jd.blockchain.crypto.SingleKey;
//import com.jd.blockchain.crypto.impl.sm.symmetric.SM4SymmetricEncryptionFunction;
//import com.jd.blockchain.crypto.service.classic.AESSymmetricEncryptionFunction;
//import com.jd.blockchain.crypto.symmetric.SymmetricCryptography;
//import com.jd.blockchain.crypto.symmetric.SymmetricEncryptionFunction;
//
//public class SymmetricCryptographyImpl implements SymmetricCryptography {
//
//    private static final SymmetricEncryptionFunction AES_ENCF = new AESSymmetricEncryptionFunction();
//    private static final SymmetricEncryptionFunction SM4_ENCF = new SM4SymmetricEncryptionFunction();
//
//    /**
//     * 封装了对称密码算法对应的密钥生成算法
//     */
//    @Override
//    public SingleKey generateKey(CryptoAlgorithm algorithm) {
//
//        //验证算法标识是对称加密算法，并根据算法生成对称密钥，否则抛出异常
//        if (algorithm.isEncryptable() && algorithm.isSymmetric() ){
//            return (SingleKey) getSymmetricEncryptionFunction(algorithm).generateSymmetricKey();
//        }
//        else throw new IllegalArgumentException("The specified algorithm is not symmetric encryption algorithm!");
//    }
//
//    @Override
//    public SymmetricEncryptionFunction getSymmetricEncryptionFunction(CryptoAlgorithm algorithm) {
//
//        // 遍历对称加密算法，如果满足，则返回实例
//        switch (algorithm) {
//            case AES:
//                return AES_ENCF;
//            case SM4:
//                return SM4_ENCF;
//            default:
//                break;
//        }
//        throw new IllegalArgumentException("The specified algorithm is not symmetric encryption algorithm!");
//    }
//
//    @Override
//    public byte[] decrypt(byte[] symmetricKeyBytes, byte[] ciphertextBytes) {
//
//        //分别得到SymmetricKey和Ciphertext类型的密钥和密文，以及symmetricKey对应的算法
//        SingleKey symmetricKey = resolveSymmetricKey(symmetricKeyBytes);
//        Ciphertext ciphertext = resolveCiphertext(ciphertextBytes);
//        CryptoAlgorithm algorithm = symmetricKey.getAlgorithm();
//
//        //验证两个输入中算法标识一致，否则抛出异常
//        if (algorithm != ciphertext.getAlgorithm())
//            throw new IllegalArgumentException("Ciphertext's algorithm and key's are not matching!");
//
//        //根据算法标识，调用对应算法实例来计算返回明文
//        return getSymmetricEncryptionFunction(algorithm).decrypt(symmetricKey,ciphertext);
//      }
//
//    @Override
//    public Ciphertext resolveCiphertext(byte[] ciphertextBytes) {
//        Ciphertext ciphertext = tryResolveCiphertext(ciphertextBytes);
//        if (ciphertext == null)
//            throw new IllegalArgumentException("This ciphertextBytes cannot be resolved!");
//        else return ciphertext;
//    }
//
//    @Override
//    public Ciphertext tryResolveCiphertext(byte[] ciphertextBytes) {
//        //遍历对称加密算法，如果满足，则返回解析结果
//        if (AES_ENCF.supportCiphertext(ciphertextBytes)) {
//            return AES_ENCF.resolveCiphertext(ciphertextBytes);
//        }
//        if (SM4_ENCF.supportCiphertext(ciphertextBytes)) {
//            return SM4_ENCF.resolveCiphertext(ciphertextBytes);
//        }
//        //否则返回null
//        return null;
//    }
//
//    @Override
//    public SingleKey resolveSymmetricKey(byte[] symmetricKeyBytes) {
//        SingleKey symmetricKey = tryResolveSymmetricKey(symmetricKeyBytes);
//        if (symmetricKey == null)
//            throw new IllegalArgumentException("This symmetricKeyBytes cannot be resolved!");
//        else return symmetricKey;
//    }
//
//    @Override
//    public SingleKey tryResolveSymmetricKey(byte[] symmetricKeyBytes) {
//        //遍历对称加密算法，如果满足，则返回解析结果
//        if(AES_ENCF.supportSymmetricKey(symmetricKeyBytes)) {
//            return AES_ENCF.resolveSymmetricKey(symmetricKeyBytes);
//        }
//        if(SM4_ENCF.supportSymmetricKey(symmetricKeyBytes)) {
//            return SM4_ENCF.resolveSymmetricKey(symmetricKeyBytes);
//        }
//        //否则返回null
//        return null;
//    }
//}
