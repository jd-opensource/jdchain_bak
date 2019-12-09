package test.com.jd.blockchain.ledger;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.resolver.BytesToBytesValueResolver;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import static org.junit.Assert.*;
import org.junit.Test;

public class BytesToBytesValueResolverTest {

    private BytesToBytesValueResolver resolver = new BytesToBytesValueResolver();

    @Test
    public void test() {
        String text = "www.jd.com";

        byte[] bytes = BytesUtils.toBytes(text);

        Bytes bytesObj = Bytes.fromString(text);

        BytesValue bytesValue = resolver.encode(bytes);

        assertNotNull(bytesValue);

        assertEquals(bytesValue.getType(), DataType.BYTES);

        assertEquals(bytesObj, bytesValue.getBytes());

        Bytes resolveBytesObj = (Bytes)resolver.decode(bytesValue);

        assertEquals(bytesObj, resolveBytesObj);

        byte[] resolveBytes = (byte[])resolver.decode(bytesValue, byte[].class);

        assertArrayEquals(bytes, resolveBytes);

        String resolveText = (String)resolver.decode(bytesValue, String.class);

        assertEquals(text, resolveText);
    }
}
