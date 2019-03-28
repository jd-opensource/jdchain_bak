package test.my.utils.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.junit.Test;

import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesEncoding;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.NumberMask;

public class BytesEncodingTest {

	@Test
	public void testWriteAndRead() throws UnsupportedEncodingException {
		// 针对正常数据的测试；
		byte[] data = UUID.randomUUID().toString().getBytes("UTF-8");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int s = BytesEncoding.write(data, NumberMask.TINY, out);

		byte[] outBytes = out.toByteArray();
		assertEquals(outBytes.length, s);
		assertEquals(data.length + 1, s);

		ByteArrayInputStream in = new ByteArrayInputStream(outBytes);
		byte[] resovledBytes = BytesEncoding.read(NumberMask.TINY, in);

		assertTrue(BytesUtils.equals(data, resovledBytes));

		// 针对 null 的测试；
		data = null;

		out = new ByteArrayOutputStream();
		s = BytesEncoding.write(data, NumberMask.TINY, out);

		outBytes = out.toByteArray();
		assertEquals(outBytes.length, s);
		assertEquals(1, s);

		in = new ByteArrayInputStream(outBytes);
		resovledBytes = BytesEncoding.read(NumberMask.TINY, in);

		assertTrue(BytesUtils.equals(BytesUtils.EMPTY_BYTES, resovledBytes));
	}

	@Test
	public void testWriteAndRead1() throws UnsupportedEncodingException {
		// 针对正常数据的测试；
		ByteArray data = ByteArray.parseString(UUID.randomUUID().toString(), "UTF-8");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int s = BytesEncoding.write(data, NumberMask.TINY, out);

		byte[] outBytes = out.toByteArray();
		assertEquals(outBytes.length, s);
		assertEquals(data.size() + 1, s);

		ByteArrayInputStream in = new ByteArrayInputStream(outBytes);
		byte[] resovledBytes = BytesEncoding.read(NumberMask.TINY, in);

		assertTrue(BytesUtils.equals(data.bytes(), resovledBytes));

		// 针对 null 的测试；
		data = null;

		out = new ByteArrayOutputStream();
		s = BytesEncoding.write(data, NumberMask.TINY, out);

		outBytes = out.toByteArray();
		assertEquals(outBytes.length, s);
		assertEquals(1, s);

		in = new ByteArrayInputStream(outBytes);
		resovledBytes = BytesEncoding.read(NumberMask.TINY, in);

		assertTrue(BytesUtils.equals(BytesUtils.EMPTY_BYTES, resovledBytes));
	}

}
