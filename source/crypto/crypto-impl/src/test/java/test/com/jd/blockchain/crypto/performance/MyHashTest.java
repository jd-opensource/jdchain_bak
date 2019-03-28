//package test.com.jd.blockchain.crypto.performance;
//
//import com.jd.blockchain.crypto.impl.sm.hash.SM3HashFunction;
//import com.jd.blockchain.crypto.service.classic.RIPEMD160HashFunction;
//import com.jd.blockchain.crypto.service.classic.SHA256HashFunction;
//
//import java.util.Random;
//
//public class MyHashTest {
//
//    public static void main(String[] args) {
//
//        Random rand = new Random();
//        byte[] data1K = new byte[1024];
//        rand.nextBytes(data1K);
//        int count = 1000000;
//
//        SHA256HashFunction sha256hf = new SHA256HashFunction();
//
//        System.out.println("=================== do SHA256 hash test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                sha256hf.hash(data1K);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SHA256 hashing Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        RIPEMD160HashFunction ripemd160hf = new RIPEMD160HashFunction();
//
//        System.out.println("=================== do RIPEMD160 hash test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                ripemd160hf.hash(data1K);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("RIPEMD160 hashing Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        SM3HashFunction sm3hf = new SM3HashFunction();
//
//        System.out.println("=================== do SM3 hash test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                sm3hf.hash(data1K);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SM3 hashing Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//    }
//}
//
