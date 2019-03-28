//package test.com.jd.blockchain.crypto.jniutils;
//
//import com.jd.blockchain.crypto.jniutils.asymmetric.JNIED25519Utils;
//
//
//public class JNIED25519UtilsTest {
//
//    /* Program entry function */
//    public static void main(String args[]) {
//
//        byte[] msg = "abc".getBytes();
//        int i;
//        int j;
//        int count = 10000;
//
//        long startTS;
//        long elapsedTS;
//
//        byte[] privKey   = new byte[32];
//        byte[] pubKey    = new byte[32];
//        byte[] signature;
//
//
//        JNIED25519Utils ed25519 = new JNIED25519Utils();
//
//        System.out.println("=================== Key Generation test ===================");
//        ed25519.generateKeyPair(privKey,pubKey);
//        System.out.println("Private Key: ");
//        for(i = 0; i < privKey.length; i++) {
//            System.out.print(privKey[i] + " ");
//            if((i+1)%8 == 0)
//                System.out.println();
//        }
//        System.out.println();
//        System.out.println("Public Key: ");
//        for(i = 0; i < pubKey.length; i++) {
//            System.out.print(pubKey[i] + " ");
//            if((i+1)%8 == 0)
//                System.out.println();
//        }
//        System.out.println();
//
//        System.out.println("=================== Public Key Retrieval test ===================");
//        byte[] pk;
//        pk = ed25519.getPubKey(privKey);
//        System.out.println("Retrieved Public Key: ");
//        for(i = 0; i < pk.length; i++) {
//            System.out.print(pk[i] + " ");
//            if((i+1)%8 == 0)
//                System.out.println();
//        }
//        System.out.println();
//
//        System.out.println("=================== Signing test ===================");
//        signature = ed25519.sign(msg,privKey,pubKey);
//        System.out.println("Signature: ");
//        for(i = 0; i < signature.length; i++) {
//            System.out.print(signature[i] + " ");
//            if((i+1)%8 == 0)
//                System.out.println();
//        }
//        System.out.println();
//
//        System.out.println("=================== Verifying test ===================");
//        if (ed25519.verify(msg,pubKey,signature))
//            System.out.println("valid signature");
//        else System.out.println("invalid signature");
//
//        System.out.println("=================== Do ED25519 Key Pair Generation Test ===================");
//
//
//        for (j = 0; j < 5; j++) {
//            System.out.println("------------- round[" + j + "] --------------");
//            startTS = System.currentTimeMillis();
//            for (i = 0; i < count; i++) {
//                ed25519.generateKeyPair(privKey,pubKey);
//            }
//            elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("ED25519 Key Pair Generation: Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//        System.out.println();
//
//        System.out.println("=================== Do ED25519 Public Key Retrieval Test ===================");
//        for (j = 0; j < 5; j++) {
//            System.out.println("------------- round[" + j + "] --------------");
//            startTS = System.currentTimeMillis();
//            for (i = 0; i < count; i++) {
//                ed25519.getPubKey(privKey);
//            }
//            elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("ED25519 Public Key Retrieval: Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//        System.out.println();
//
//        System.out.println("=================== Do ED25519 Signing Test ===================");
//        for (j = 0; j < 5; j++) {
//            System.out.println("------------- round[" + j + "] --------------");
//            startTS = System.currentTimeMillis();
//            for (i = 0; i < count; i++) {
//                ed25519.sign(msg,privKey,pubKey);
//            }
//            elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("ED25519 Signing: Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//        System.out.println();
//
//        System.out.println("=================== Do ED25519 Verifying Test ===================");
//        for (j = 0; j < 5; j++) {
//            System.out.println("------------- round[" + j + "] --------------");
//            startTS = System.currentTimeMillis();
//            for (i = 0; i < count; i++) {
//                ed25519.verify(msg,pubKey,signature);
//            }
//            elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("ED25519 Verifying: Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//        System.out.println();
//    }
//}
