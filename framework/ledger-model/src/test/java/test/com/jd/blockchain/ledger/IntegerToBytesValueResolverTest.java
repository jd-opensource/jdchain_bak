package test.com.jd.blockchain.ledger;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.resolver.IntegerToBytesValueResolver;
import static org.junit.Assert.*;

import com.jd.blockchain.utils.Bytes;
import org.junit.Test;

public class IntegerToBytesValueResolverTest {

    private IntegerToBytesValueResolver resolver = new IntegerToBytesValueResolver();

    @Test
    public void test() {
        int intVal = 1024;

        BytesValue intBytesValue = resolver.encode(intVal);

        BytesValue intBytesValue1 = resolver.encode(intVal, int.class);

        BytesValue intBytesValue2 = resolver.encode(intVal, Integer.class);

        assertEquals(intBytesValue.getBytes(), intBytesValue1.getBytes());

        assertEquals(intBytesValue.getBytes(), intBytesValue2.getBytes());

        Bytes intBytes = Bytes.fromInt(intVal);

        assertEquals(intBytes, intBytesValue.getBytes());

        assertEquals(intBytesValue.getType(), DataType.INT32);

        int resolveInt = (int)resolver.decode(intBytesValue);

        assertEquals(intVal, resolveInt);

        short resolveShort = (short) resolver.decode(intBytesValue, short.class);
        assertEquals(resolveShort, 1024);
        Short resolveShortObj = (Short) resolver.decode(intBytesValue, Short.class);
        assertEquals((short)resolveShortObj, resolveShort);

        long resolveLong = (long) resolver.decode(intBytesValue, long.class);
        assertEquals(resolveLong, 1024L);

        Long resolveLongObj = (Long) resolver.decode(intBytesValue, Long.class);
        assertEquals(resolveLong, (long) resolveLongObj);

    }
}
