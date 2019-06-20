package test.com.jd.blockchain.ledger.data;

import com.jd.blockchain.ledger.BytesValueEncoding;
import com.jd.blockchain.utils.Bytes;
import static org.junit.Assert.*;
import org.junit.Test;

public class BytesValueEncodingTest {
    @Test
    public void testSupport() {
        assertTrue(BytesValueEncoding.supportType(byte[].class));
        assertTrue(BytesValueEncoding.supportType(int.class));
        assertTrue(BytesValueEncoding.supportType(Integer.class));
        assertTrue(BytesValueEncoding.supportType(short.class));
        assertTrue(BytesValueEncoding.supportType(Short.class));
        assertTrue(BytesValueEncoding.supportType(long.class));
        assertTrue(BytesValueEncoding.supportType(Long.class));
        assertTrue(BytesValueEncoding.supportType(String.class));
        assertTrue(BytesValueEncoding.supportType(Bytes.class));



    }
}
