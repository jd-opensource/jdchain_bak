package test.com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.codec.HexUtils;
import org.junit.Assert;
import org.junit.Test;

public class EncodeTest {

    @Test
    public void test() {
        String pubKey = "7VeRLdGtSz1Y91gjLTqEdnkotzUfaAqdap3xw6fQ1yKHkvVq";
        Bytes bytes = Bytes.fromBase58(pubKey);
        String hexString = HexUtils.encode(bytes.toBytes());
        String code = hexString.substring(2, 4);
        Assert.assertEquals(code, "15"); // 15为十六进制，对应十进制为21（ED25519)
    }
}
