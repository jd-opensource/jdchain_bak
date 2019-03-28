//package test.com.jd.blockchain.crypto.performance;
//
//import com.jd.blockchain.crypto.Ciphertext;
//import com.jd.blockchain.crypto.SingleKey;
//import com.jd.blockchain.crypto.impl.sm.symmetric.SM4SymmetricEncryptionFunction;
//import com.jd.blockchain.crypto.service.classic.AESSymmetricEncryptionFunction;
//
//import org.bouncycastle.util.encoders.Hex;
//
//public class MySymmetricEncryptionTest {
//
//    public static void main(String[] args) {
//
//        String string1K = "0123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba9876543210";
//
////        String string1M = "";
////        for (int i = 0; i < 1024 ; i++)
////        {
////            string1M = string1M + string1K;
////        }
//
//        byte[] data1K = Hex.decode(string1K);
////        byte[] data1M = Hex.decode(string1M);
//
//        int count = 100000;
//
//
//        AESSymmetricEncryptionFunction aes = new AESSymmetricEncryptionFunction();
//        SingleKey keyAES = (SingleKey) aes.generateSymmetricKey();
//        Ciphertext ciphertext1KAES = null;
//        Ciphertext ciphertext1MAES = null;
//
//        System.out.println("=================== do AES encrypt test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                ciphertext1KAES = aes.encrypt(keyAES,data1K);
////                ciphertext1MAES = aes.encrypt(keyAES,data1M);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("AES Encrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//
//
//        System.out.println("=================== do AES decrypt test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                aes.decrypt(keyAES,ciphertext1KAES);
////                aes.decrypt(keyAES,ciphertext1MAES);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("AES Decrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        SM4SymmetricEncryptionFunction sm4 = new SM4SymmetricEncryptionFunction();
//        SingleKey keySM4 = (SingleKey) sm4.generateSymmetricKey();
//        Ciphertext ciphertext1KSM4 = null;
//        Ciphertext ciphertext1MSM4 = null;
//
//        System.out.println("=================== do SM4 encrypt test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                ciphertext1KSM4 = sm4.encrypt(keySM4,data1K);
////                ciphertext1MSM4 =sm4.encrypt(keySM4,data1M);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SM4 Encrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        System.out.println("=================== do SM4 decrypt test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                sm4.decrypt(keySM4,ciphertext1KSM4);
////                sm4.decrypt(keySM4,ciphertext1MSM4);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SM4 Decrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//    }
//}
