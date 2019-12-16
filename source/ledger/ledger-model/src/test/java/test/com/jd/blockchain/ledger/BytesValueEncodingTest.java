package test.com.jd.blockchain.ledger;

import com.jd.blockchain.ledger.*;
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
        assertTrue(BytesValueEncoding.supportType(Operation.class));
        assertFalse(BytesValueEncoding.supportType(byte.class));
    }

    @Test
    public void testSingle() {
        long longVal = 1024L;
        BytesValue longBytesVal1 = BytesValueEncoding.encodeSingle(longVal, null);
        BytesValue longBytesVal2 = BytesValueEncoding.encodeSingle(longVal, long.class);
        BytesValue longBytesVal3 = BytesValueEncoding.encodeSingle(longVal, Long.class);

        assertEquals(longBytesVal1.getBytes(), longBytesVal2.getBytes());
        assertEquals(longBytesVal1.getType(), longBytesVal2.getType());
        assertEquals(longBytesVal2.getBytes(), longBytesVal3.getBytes());
        assertEquals(longBytesVal2.getType(), longBytesVal3.getType());

        long resolveLongVal1 = (long)BytesValueEncoding.decode(longBytesVal1);
        long resolveLongVal2 = (long)BytesValueEncoding.decode(longBytesVal2);
        long resolveLongVal3 = (long)BytesValueEncoding.decode(longBytesVal3);

        assertEquals(resolveLongVal1, 1024L);
        assertEquals(resolveLongVal2, 1024L);
        assertEquals(resolveLongVal3, 1024L);
    }

    @Test
    public void testArray() {
        Object[] values = new Object[]{1024L, "zhangsan", "lisi".getBytes(), 16};
        Class<?>[] classes = new Class[]{long.class, String.class, byte[].class, int.class};

        BytesValueList bytesValueList = BytesValueEncoding.encodeArray(values, null);
        BytesValueList bytesValueList1 = BytesValueEncoding.encodeArray(values, classes);

        assertEquals(bytesValueList1.getValues().length, values.length);


        BytesValue[] bytesValues = bytesValueList.getValues();
        assertEquals(bytesValues.length, values.length);

        assertEquals(DataType.INT64, bytesValues[0].getType());
        assertEquals(DataType.TEXT, bytesValues[1].getType());
        assertEquals(DataType.BYTES, bytesValues[2].getType());
        assertEquals(DataType.INT32, bytesValues[3].getType());

        Object[] resolveObjs = BytesValueEncoding.decode(bytesValueList, classes);

        assertArrayEquals(resolveObjs, values);
    }

}
