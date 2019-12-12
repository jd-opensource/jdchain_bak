package test.com.jd.blockchain.crypto.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.jd.blockchain.crypto.utils.sm.SM3Utils;

public class SM3UtilsTest {

    private static final int SM3DIGEST_LENGTH = 32;

    @Test
    public void testHash() {

        String testString1 = "abc";
        String testString2 = "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd";
        String expectedResult1="66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0" ;
        String expectedResult2="debe9ff92275b8a138604889c18e5a4d6fdb70e5387e5765293dcba39c0c5732";

        byte[] testString1Bytes = BytesUtils.toBytes(testString1);
        byte[] testString2Bytes = BytesUtils.toBytes(testString2);
        byte[] hash1 = SM3Utils.hash(testString1Bytes);
        byte[] hash2 = SM3Utils.hash(testString2Bytes);
        byte[] expectedResult1Bytes = BytesUtils.toBytes(expectedResult1);
        byte[] expectedResult2Bytes = BytesUtils.toBytes(expectedResult2);
        assertEquals(hash1.length, SM3DIGEST_LENGTH);
        assertEquals(hash2.length, SM3DIGEST_LENGTH);
        assertArrayEquals(hash1, Hex.decode(expectedResult1Bytes));
        assertArrayEquals(hash2, Hex.decode(expectedResult2Bytes));
    }

//    @Test
//    public void hashingPerformance() {
//
//        byte[] data = new byte[1000];
//        Random random = new Random();
//        random.nextBytes(data);
//
//        int count = 1000000;
//
//        System.out.println("=================== do SM3 hash test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                SM3Utils.hash(data);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SM3 hashing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//
//        System.out.println("=================== do SHA256 hash test ===================");
//        for (int r = 0; r < 5; r++) {
//            System.out.println("------------- round[" + r + "] --------------");
//            long startTS = System.currentTimeMillis();
//            for (int i = 0; i < count; i++) {
//                ShaUtils.hash_256(data);
//            }
//            long elapsedTS = System.currentTimeMillis() - startTS;
//            System.out.println(String.format("SHA256 hashing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
//                    (count * 1000.00D) / elapsedTS));
//        }
//    }
}