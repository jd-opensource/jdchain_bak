package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.SHA256Utils;
import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author zhanglin33
 * @title: SHA256UtilsTest
 * @description: Tests for the hash method in SHA256Utils
 * @date 2019-04-09, 16:18
 */
public class SHA256UtilsTest {

    @Test
    public void hashTest() {

        byte[] data1 = BytesUtils.toBytes("abc");
        byte[] data2 = BytesUtils.toBytes("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq");
        byte[] data3 = BytesUtils.toBytes("aaaaaaaaaa");

        byte[] result1 = SHA256Utils.hash(data1);
        byte[] result2 = SHA256Utils.hash(data2);
        byte[] result3 = SHA256Utils.hash(data3);

        String respectedResult1 = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
        String respectedResult2 = "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1";
        String respectedResult3 = "bf2cb58a68f684d95a3b78ef8f661c9a4e5b09e82cc8f9cc88cce90528caeb27";

        assertEquals(respectedResult1,Hex.toHexString(result1));
        assertEquals(respectedResult2,Hex.toHexString(result2));
        assertEquals(respectedResult3,Hex.toHexString(result3));
    }
}
