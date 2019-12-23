package test.com.jd.blockchain.ledger;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.resolver.ShortToBytesValueResolver;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShortToBytesValueResolverTest {

    private ShortToBytesValueResolver resolver = new ShortToBytesValueResolver();

    @Test
    public void test() {
        short shortVal = 64;

        BytesValue shortBytesValue = resolver.encode(shortVal);

        Bytes shortBytes = new Bytes(BytesUtils.toBytes(shortVal));

        assertEquals(shortBytes, shortBytesValue.getBytes());

        assertEquals(shortBytesValue.getType(), DataType.INT16);

        short resolveShort = (short)resolver.decode(shortBytesValue);

        assertEquals(shortVal, resolveShort);

        int resolveInt = (int) resolver.decode(shortBytesValue, int.class);
        assertEquals(resolveInt, 64);
        Integer resolveIntObj = (Integer) resolver.decode(shortBytesValue, Integer.class);
        assertEquals((int)resolveIntObj, resolveShort);

        long resolveLong = (long) resolver.decode(shortBytesValue, long.class);
        assertEquals(resolveLong, 64L);

        Long resolveLongObj = (Long) resolver.decode(shortBytesValue, Long.class);
        assertEquals(resolveLong, (long) resolveLongObj);
    }
}
