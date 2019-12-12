package test.my.utils.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.Test;

import com.jd.blockchain.utils.io.BytesUtils;

public class BytesUtilsTest {

	@Test
	public void testByteOutputInput() {
		// 边界测试；
		assertByteOutputInput((byte) 0);
		assertByteOutputInput((byte) 1);
		assertByteOutputInput((byte) -1);
		assertByteOutputInput((byte) 128);
		assertByteOutputInput((byte) 255);
		assertByteOutputInput(Byte.MAX_VALUE);
		assertByteOutputInput(Byte.MIN_VALUE);

		byte[] emptyBytes = {};
		ByteArrayInputStream emptyIn = new ByteArrayInputStream(emptyBytes);
		Exception err = null;
		try {
			BytesUtils.readByte(emptyIn);
		} catch (Exception e) {
			err = e;
		}
		assertTrue(err != null);
	}

	private void assertByteOutputInput(byte value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BytesUtils.writeByte(value, out);
		byte[] bytes = out.toByteArray();
		assertEquals(bytes[0], value);

		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		byte val1 = BytesUtils.readByte(in);

		assertEquals(value, val1);
	}

	@Test
	public void testIntegerToBytes() {
		assertIntTest(0);
		assertIntTest(Integer.MAX_VALUE);
		assertIntTest(Integer.MIN_VALUE);
		assertIntTest(255);
		assertIntTest(256);
	}

	private void assertIntTest(int i) {
		byte[] bytes = BytesUtils.toBytes(i);
		int reallyInt = BytesUtils.toInt(bytes);
		assertEquals(i, reallyInt);
	}

	@Test
	public void testIntTestLowAlign() {
		int n = 65535;
		byte[] bytes = BytesUtils.toBytes(n);
		int reallyInt = BytesUtils.toInt(bytes, 2, 2, false);
		assertEquals(n, reallyInt);

		n = 0;
		bytes = BytesUtils.toBytes(n);
		reallyInt = BytesUtils.toInt(bytes, 2, 2, false);
		assertEquals(n, reallyInt);

		n = 255;
		bytes = BytesUtils.toBytes(n);
		reallyInt = BytesUtils.toInt(bytes, 2, 2, false);
		assertEquals(n, reallyInt);

		n = 256;
		bytes = BytesUtils.toBytes(n);
		reallyInt = BytesUtils.toInt(bytes, 2, 2, false);
		assertEquals(n, reallyInt);

		n = 255;
		bytes = BytesUtils.toBytes(n);
		reallyInt = BytesUtils.toInt(bytes, 3, 1, false);
		assertEquals(n, reallyInt);

		n = 16777215;
		bytes = BytesUtils.toBytes(n);
		reallyInt = BytesUtils.toInt(bytes, 1, 3, false);
		assertEquals(n, reallyInt);

		n = 65535;
		bytes = BytesUtils.toBytes(n);
		reallyInt = BytesUtils.toInt(bytes, 1, 3, false);
		assertEquals(n, reallyInt);

		n = 255;
		bytes = BytesUtils.toBytes(n);
		reallyInt = BytesUtils.toInt(bytes, 1, 3, false);
		assertEquals(n, reallyInt);

		n = 0;
		bytes = BytesUtils.toBytes(n);
		reallyInt = BytesUtils.toInt(bytes, 1, 3, false);
		assertEquals(n, reallyInt);
	}

	@Test
	public void testLongIntegerToBytes() {
		assertLongTest(0);
		assertLongTest(Integer.MAX_VALUE);
		assertLongTest(Integer.MIN_VALUE);
		assertLongTest(Long.MAX_VALUE);
		assertLongTest(Long.MIN_VALUE);
		assertLongTest(255);
		assertLongTest(256);
	}

	private void assertLongTest(long i) {
		byte[] bytes = BytesUtils.toBytes(i);
		long reallyInt = BytesUtils.toLong(bytes);
		assertEquals(i, reallyInt);
	}

	@Test
	public void testIntegerByteCasting() {
		testIntByteCasting(0);
		testIntByteCasting(-1);
		testIntByteCasting(1);
		testIntByteCasting(255);
		testIntByteCasting(256);
		testIntByteCasting(Integer.MAX_VALUE);
		testIntByteCasting(Integer.MIN_VALUE);
	}

	private void testIntByteCasting(int v) {
		byte b1 = (byte) (v & 0xFF);
		byte b2 = (byte) v;
		assertTrue(b1 == b2);
	}

	@Test
	public void testWriteByte() {
		assertByteWriting((byte) 0);
		assertByteWriting((byte) 128);
		assertByteWriting((byte) 1);
		assertByteWriting((byte) 0x0F);
		assertByteWriting((byte) 0xF0);
		assertByteWriting((byte) 255);
	}

	private void assertByteWriting(byte bt) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(bt);
		byte[] buff = out.toByteArray();
		assertEquals(bt, buff[0]);

		ByteArrayInputStream in = new ByteArrayInputStream(buff);
		int val = in.read();
		byte btIn = (byte) (val & 0xFF);
		byte btIn1 = (byte) (val);

		assertEquals(bt, btIn);
		assertEquals(bt, btIn1);
	}

	@Test
	public void testShortCasting() {
		doTestShortCasting((short) 0);
		doTestShortCasting((short) -1);
		doTestShortCasting((short) 1);
		doTestShortCasting((short) 255);
		doTestShortCasting((short) -255);
		doTestShortCasting(Short.MIN_VALUE);
		doTestShortCasting(Short.MAX_VALUE);
	}

	private void doTestShortCasting(short value) {
		byte[] bytes = BytesUtils.toBytes(value);
		assertEquals(2, bytes.length);

		short deValue = BytesUtils.toShort(bytes, 0);
		assertEquals(value, deValue);
	}

	@Test
	public void testConcatBytes() {
		Random rand = new Random();
		byte[] bs1 = new byte[64];
		byte[] bs2 = new byte[0];
		byte[] bs3 = new byte[512];

		rand.nextBytes(bs1);
		rand.nextBytes(bs3);

		byte[] bsAll = BytesUtils.concat(bs1, bs2, bs3);
		assertEquals(bs1.length + bs2.length + bs3.length, bsAll.length);
		for (int i = 0; i < bs1.length; i++) {
			assertEquals(bs1[i], bsAll[i]);
		}
		for (int i = 0; i < bs3.length; i++) {
			assertEquals(bs3[i], bsAll[bs1.length + i]);
		}
	}

	@Test
	public void testStreamingReadWrite() {
		testStreamingReadWriteInt(0);
		testStreamingReadWriteInt(1);
		testStreamingReadWriteInt(2);
		testStreamingReadWriteInt(256);
		testStreamingReadWriteInt(128);
		testStreamingReadWriteInt(-1);
		testStreamingReadWriteInt(-128);
		testStreamingReadWriteInt(Integer.MAX_VALUE);
		testStreamingReadWriteInt(Integer.MIN_VALUE);
		
		testStreamingReadWriteLong(0);
		testStreamingReadWriteLong(1);
		testStreamingReadWriteLong(2);
		testStreamingReadWriteLong(256);
		testStreamingReadWriteLong(128);
		testStreamingReadWriteLong(-1);
		testStreamingReadWriteLong(-128);
		testStreamingReadWriteLong(Integer.MAX_VALUE);
		testStreamingReadWriteLong(Integer.MIN_VALUE);
		testStreamingReadWriteLong(Long.MAX_VALUE);
		testStreamingReadWriteLong(Long.MIN_VALUE);
	}

	private void testStreamingReadWriteInt(int value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BytesUtils.writeInt(value, out);
		byte[] bytes0 = BytesUtils.toBytes(value);
		assertTrue(BytesUtils.equals(bytes0, out.toByteArray()));

		ByteArrayInputStream in = new ByteArrayInputStream(bytes0);
		int v = BytesUtils.readInt(in);
		assertEquals(value, v);
	}
	
	private void testStreamingReadWriteLong(long value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BytesUtils.writeLong(value, out);
		byte[] bytes0 = BytesUtils.toBytes(value);
		assertTrue(BytesUtils.equals(bytes0, out.toByteArray()));
		
		ByteArrayInputStream in = new ByteArrayInputStream(bytes0);
		long v = BytesUtils.readLong(in);
		assertEquals(value, v);
	}
}
