package test.my.utils.io;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

public class BytesTest {

	@Test
	public void test() {
		Bytes prefix = Bytes.fromString("A");
		byte[] textBytes = BytesUtils.toBytes("B");
		Bytes key1 = new Bytes(prefix, textBytes);
		byte[] key1Bytes = key1.toBytes();
		assertEquals(prefix.size() + textBytes.length, key1.size());
		assertEquals(prefix.size() + textBytes.length, key1Bytes.length);
		assertEquals(Arrays.hashCode(key1Bytes), key1.hashCode());
	}

}
