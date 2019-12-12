package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.RIPEMD160Utils;
import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author zhanglin33
 * @title: RIPEMD160UtilsTest
 * @description: Tests for the hash method in RIPEMD160Utils
 * @date 2019-04-10, 16:54
 */
public class RIPEMD160UtilsTest {

    @Test
    public void hashTest() {

        byte[] data1 = BytesUtils.toBytes("a");
        byte[] data2 = BytesUtils.toBytes("abc");

        byte[] result1 = RIPEMD160Utils.hash(data1);
        byte[] result2 = RIPEMD160Utils.hash(data2);

        String respectedResult1 = "0bdc9d2d256b3ee9daae347be6f4dc835a467ffe";
        String respectedResult2 = "8eb208f7e05d987a9b044a8e98c6b087f15a0bfc";

        assertEquals(respectedResult1, Hex.toHexString(result1));
        assertEquals(respectedResult2, Hex.toHexString(result2));
    }
}
