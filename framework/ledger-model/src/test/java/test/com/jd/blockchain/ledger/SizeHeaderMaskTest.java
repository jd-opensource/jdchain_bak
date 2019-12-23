package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jd.blockchain.utils.io.NumberMask;


public class SizeHeaderMaskTest {

	@Test
	public void testSizeHeaderMask() {
		assertTrue(NumberMask.TINY.MAX_HEADER_LENGTH == 1);
		assertEquals(256, NumberMask.TINY.getBoundarySize(1));
		
		assertTrue(NumberMask.SHORT.MAX_HEADER_LENGTH == 2);
		assertEquals(128, NumberMask.SHORT.getBoundarySize(1));
		assertEquals(32768, NumberMask.SHORT.getBoundarySize(2));
		
		assertTrue(NumberMask.NORMAL.MAX_HEADER_LENGTH == 4);
		assertEquals(64, NumberMask.NORMAL.getBoundarySize(1));
		assertEquals(16384, NumberMask.NORMAL.getBoundarySize(2));
		assertEquals(4194304, NumberMask.NORMAL.getBoundarySize(3));
		assertEquals(1073741824, NumberMask.NORMAL.getBoundarySize(4));
		
		//不考虑 long 的情况；
//		assertTrue(SizeHeaderMask.LONG.MAX_HEADER_LENGTH == 8);
//		assertEquals(32L, SizeHeaderMask.LONG.getBoundarySize((byte)1));
//		assertEquals(8192L, SizeHeaderMask.LONG.getBoundarySize((byte)2));
//		assertEquals(2097152L, SizeHeaderMask.LONG.getBoundarySize((byte)3));
//		assertEquals(536870912L, SizeHeaderMask.LONG.getBoundarySize((byte)4));
//		assertEquals(137438953472L, SizeHeaderMask.LONG.getBoundarySize((byte)5));
//		assertEquals(35184372088832L, SizeHeaderMask.LONG.getBoundarySize((byte)6));
//		assertEquals(9007199254740992L, SizeHeaderMask.LONG.getBoundarySize((byte)7));
//		assertEquals(2305843009213693952L, SizeHeaderMask.LONG.getBoundarySize((byte)8));
	}

}
