package test.com.jd.blockchain.ledger;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.mockito.Mockito;

import com.jd.blockchain.utils.io.BytesEncoding;
import com.jd.blockchain.utils.io.NumberMask;

public class BytesEncodingTest {

	@Test
	public void testReadAndWriteSize() {
		assertReadAndWriteSizeOK(0, NumberMask.TINY);
		assertReadAndWriteSizeOK(255, NumberMask.TINY);
		assertReadAndWriteSizeOK(1, NumberMask.TINY);
		assertReadAndWriteSizeOK(128, NumberMask.TINY);
		assertReadAndWriteSizeError(256, NumberMask.TINY);
		assertReadAndWriteSizeError(Integer.MIN_VALUE, NumberMask.TINY);
		assertReadAndWriteSizeError(Integer.MAX_VALUE, NumberMask.TINY);

		assertReadAndWriteSizeOK(0, NumberMask.SHORT);
		assertReadAndWriteSizeOK(255, NumberMask.SHORT);
		assertReadAndWriteSizeOK(1, NumberMask.SHORT);
		assertReadAndWriteSizeOK(128, NumberMask.SHORT);
		assertReadAndWriteSizeOK(256, NumberMask.SHORT);
		assertReadAndWriteSizeOK(1 << 7, NumberMask.SHORT);
		assertReadAndWriteSizeOK((1 << 15) - 1, NumberMask.SHORT);
		assertReadAndWriteSizeError((1 << 15) + 1, NumberMask.SHORT);
		assertReadAndWriteSizeError(1 << 16, NumberMask.SHORT);
		assertReadAndWriteSizeError(Integer.MIN_VALUE, NumberMask.SHORT);
		assertReadAndWriteSizeError(Integer.MAX_VALUE, NumberMask.SHORT);

		assertReadAndWriteSizeOK(0, NumberMask.NORMAL);
		assertReadAndWriteSizeOK(255, NumberMask.NORMAL);
		assertReadAndWriteSizeOK(1, NumberMask.NORMAL);
		assertReadAndWriteSizeOK(128, NumberMask.NORMAL);
		assertReadAndWriteSizeOK(256, NumberMask.NORMAL);
		assertReadAndWriteSizeOK(1 << 7, NumberMask.NORMAL);
		assertReadAndWriteSizeOK((1 << 15) - 1, NumberMask.NORMAL);
		assertReadAndWriteSizeOK((1 << 15) + 1, NumberMask.NORMAL);
		assertReadAndWriteSizeOK(1 << 16, NumberMask.NORMAL);
		assertReadAndWriteSizeOK(Short.MAX_VALUE, NumberMask.NORMAL);
		assertReadAndWriteSizeOK((1 << 16) - 1, NumberMask.NORMAL);
		assertReadAndWriteSizeOK(1 << 24, NumberMask.NORMAL);
		assertReadAndWriteSizeOK((1 << 24) - 1, NumberMask.NORMAL);
		assertReadAndWriteSizeOK((1 << 30) - 1, NumberMask.NORMAL);
		assertReadAndWriteSizeError(1 << 30, NumberMask.NORMAL);
		assertReadAndWriteSizeError(Integer.MIN_VALUE, NumberMask.NORMAL);
		assertReadAndWriteSizeError(Integer.MAX_VALUE, NumberMask.NORMAL);
	}

	private void assertReadAndWriteSizeError(int size, NumberMask mask) {
		Exception error = null;
		try {
			assertReadAndWriteSizeOK(size, mask);
		} catch (Exception e) {
			error = e;
		}
		assertNotNull(error);
	}

	private void assertReadAndWriteSizeOK(int size, NumberMask mask) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		mask.writeMask(size, out);

		byte[] sizebytes = out.toByteArray();

		assertTrue(sizebytes.length <= mask.MAX_HEADER_LENGTH);
		assertTrue(size <= mask.getBoundarySize(sizebytes.length));

		ByteArrayInputStream in = new ByteArrayInputStream(sizebytes);
		int sizeResolved = mask.resolveMaskedNumber(in);

		assertEquals(size, sizeResolved);

		assertTrue(in.available() == 0);
	}

}
