//package test.com.jd.blockchain.crypto.performance;
//
//import com.jd.blockchain.crypto.PrivKey;
//import com.jd.blockchain.crypto.PubKey;
//import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
//import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
//import com.jd.blockchain.crypto.impl.sm.asymmetric.SM2CryptoFunction;
//import com.jd.blockchain.crypto.service.classic.ED25519SignatureFunction;
//
//import java.util.Random;
//
//public class MySignatureTest {
//
//    public static void main(String[] args) {
//
//        Random rand = new Random();
//        byte[] data = new byte[64];
//        rand.nextBytes(data);
//        int count = 10000;
//
//        ED25519SignatureFunction ed25519sf = new ED25519SignatureFunction();
//        CryptoKeyPair keyPairED25519 = ed25519sf.generateKeyPair();
//        PrivKey privKeyED25519 = keyPairED25519.getPrivKey();
//        PubKey pubKeyED25519 = keyPairED25519.getPubKey();
//
//        System.out.println("=================== do ED25519 sign test ===================");
//        SignatureDigest signatureDigestED25519 = null;
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                signatureDigestED25519 = ed25519sf.sign(privKeyED25519,data);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("ED25519 Signing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        System.out.println("=================== do ED25519 verify test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                ed25519sf.verify(signatureDigestED25519,pubKeyED25519,data);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("ED25519 Verifying Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        SM2CryptoFunction sm2 = new SM2CryptoFunction();
//        CryptoKeyPair keyPairSM2 = sm2.generateKeyPair();
//        PrivKey privKeySM2 = keyPairSM2.getPrivKey();
//        PubKey pubKeySM2 = keyPairSM2.getPubKey();
//
//
//        System.out.println("=================== do SM2 sign test ===================");
//        SignatureDigest signatureDigestSM2 = null;
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                signatureDigestSM2 = sm2.sign(privKeySM2,data);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SM2 Signing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        System.out.println("=================== do SM2 verify test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                sm2.verify(signatureDigestSM2,pubKeySM2,data);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SM2 Verifying Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//    }
//}
