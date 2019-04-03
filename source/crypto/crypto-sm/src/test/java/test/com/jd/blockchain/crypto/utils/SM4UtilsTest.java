package test.com.jd.blockchain.crypto.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.jd.blockchain.crypto.utils.sm.SM4Utils;

public class SM4UtilsTest {

    private static final int KEY_SIZE = 16;
    private static final int BLOCK_SIZE = 16;

    @Test
    public void testGenerateKey() {
        byte[] key = SM4Utils.generateKey();
        assertEquals(KEY_SIZE,key.length);
    }

    @Test
    public void testEncrypt() {

        String plaintext            = "0123456789abcdeffedcba9876543210";
        String key                  = "0123456789abcdeffedcba9876543210";
        String iv                   = "00000000000000000000000000000000";
        String expectedCiphertextIn2ndBlock = "681edf34d206965e86b3e94f536e4246";

        byte[] plaintextBytes = Hex.decode(plaintext);
        byte[] keyBytes = Hex.decode(key);
        byte[] ivBytes = Hex.decode(iv);
        byte[] expectedCiphertextIn2ndBlockBytes = Hex.decode(expectedCiphertextIn2ndBlock);


            byte[] ciphertextbytes = SM4Utils.encrypt(plaintextBytes,keyBytes,ivBytes);

            assertEquals(BLOCK_SIZE*3,ciphertextbytes.length);

            byte[] ciphertextIn1stBlockBytes = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertextbytes,0,ciphertextIn1stBlockBytes,0,BLOCK_SIZE);
            assertArrayEquals(ivBytes,ciphertextIn1stBlockBytes);

            byte[] ciphertextIn2ndBlockBytes = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertextbytes,BLOCK_SIZE,ciphertextIn2ndBlockBytes,0,BLOCK_SIZE);
            assertArrayEquals(expectedCiphertextIn2ndBlockBytes,ciphertextIn2ndBlockBytes);


    }

    @Test
    public void testDecrypt() {

        String plaintext            = "0123456789abcdeffedcba987654321000112233445566778899";
        String key                  = "0123456789abcdeffedcba9876543210";
        String iv                   = "0123456789abcdeffedcba9876543210";

        byte[] plaintextBytes = Hex.decode(plaintext);
        byte[] keyBytes = Hex.decode(key);
        byte[] ivBytes = Hex.decode(iv);


            byte[] ciphertext = SM4Utils.encrypt(plaintextBytes,keyBytes,ivBytes);
            byte[] decryptedData = SM4Utils.decrypt(ciphertext,keyBytes);
            assertArrayEquals(plaintextBytes,decryptedData);

    }

//    @Test
//    public void encryptingPerformance() {
//
//        byte[] data = new byte[1000];
//        Random random = new Random();
//        random.nextBytes(data);
//
//        byte[] sm4Ciphertext = null;
//        byte[] aesCiphertext = null;
//
//        int count = 100000;
//
//        byte[] sm4Key = SM4Utils.generateKey();
//
//        System.out.println("=================== do SM4 encrypt test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                sm4Ciphertext = SM4Utils.encrypt(data, sm4Key);
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
//                SM4Utils.decrypt(sm4Ciphertext, sm4Key);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SM4 Decrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        byte[] aesKey = AESUtils.generateKey128_Bytes();
//
//        System.out.println("=================== do AES encrypt test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                aesCiphertext = AESUtils.encrypt(data, aesKey);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("AES Encrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//
//        System.out.println("=================== do AES decrypt test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                AESUtils.decrypt(aesCiphertext, aesKey);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("AES Decrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//    }
}