package test.com.jd.blockchain.crypto.smutils;

import com.jd.blockchain.crypto.smutils.hash.SM3Utils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

public class SM3UtilsTest {

    private static final int SM3DIGEST_LENGTH = 32;

    @Test
    public void testHash() {

        String testString1 = "abc";
        String testString2 = "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd";
        String expectedResult1="66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0" ;
        String expectedResult2="debe9ff92275b8a138604889c18e5a4d6fdb70e5387e5765293dcba39c0c5732";

        byte[] testString1Bytes = testString1.getBytes();
        byte[] testString2Bytes = testString2.getBytes();
        byte[] hash1 = SM3Utils.hash(testString1Bytes);
        byte[] hash2 = SM3Utils.hash(testString2Bytes);
        byte[] expectedResult1Bytes = expectedResult1.getBytes();
        byte[] expectedResult2Bytes = expectedResult2.getBytes();
        assertEquals(hash1.length, SM3DIGEST_LENGTH);
        assertEquals(hash2.length, SM3DIGEST_LENGTH);
        assertArrayEquals(hash1, Hex.decode(expectedResult1Bytes));
        assertArrayEquals(hash2, Hex.decode(expectedResult2Bytes));
    }

}