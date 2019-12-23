package test.com.jd.blockchain.ledger;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.resolver.IntegerToBytesValueResolver;
import com.jd.blockchain.ledger.resolver.LongToBytesValueResolver;
import com.jd.blockchain.utils.Bytes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LongToBytesValueResolverTest {

    private LongToBytesValueResolver resolver = new LongToBytesValueResolver();

    @Test
    public void test() {
        long longVal = 65536L;

        BytesValue longBytesValue = resolver.encode(longVal);

        BytesValue longBytesValue1 = resolver.encode(longVal, long.class);

        BytesValue longBytesValue2 = resolver.encode(longVal, Long.class);

        assertEquals(longBytesValue.getBytes(), longBytesValue1.getBytes());

        assertEquals(longBytesValue.getBytes(), longBytesValue2.getBytes());

        Bytes longBytes = Bytes.fromLong(longVal);

        assertEquals(longBytes, longBytesValue.getBytes());

        assertEquals(longBytesValue.getType(), DataType.INT64);

        long resolveLong = (long)resolver.decode(longBytesValue);

        assertEquals(longVal, resolveLong);

        short resolveShort = (short) resolver.decode(longBytesValue, short.class);
        assertEquals(resolveShort, (short)65536);

        Short resolveShortObj = (Short) resolver.decode(longBytesValue, Short.class);
        assertEquals((short)resolveShortObj, resolveShort);

        int resolveInt = (int) resolver.decode(longBytesValue, int.class);
        assertEquals(resolveInt, 65536);

        Integer resolveIntObj = (Integer) resolver.decode(longBytesValue, Integer.class);
        assertEquals(resolveInt, (int) resolveIntObj);
    }
}
