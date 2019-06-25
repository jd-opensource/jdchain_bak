package test.com.jd.blockchain.storage.service.utils;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.utils.BufferedKVStorage;
import com.jd.blockchain.utils.Bytes;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class BufferedKVStorageTest {

	@Test
	public void test() throws UnsupportedEncodingException {
		ExPolicyKVStorage exStorage = Mockito.mock(ExPolicyKVStorage.class);
		when(exStorage.get(any())).thenReturn(null);
		when(exStorage.set(any(), any(), any())).thenReturn(true);

		VersioningKVStorage verStorage = Mockito.mock(VersioningKVStorage.class);
		when(verStorage.getVersion(any())).thenReturn(-1L);
		when(verStorage.get(any(), anyLong())).thenReturn(null);
		when(verStorage.getEntry(any(), anyLong())).thenReturn(null);
		when(verStorage.set(any(), any(), anyLong())).thenAnswer(new Answer<Long>() {
			@Override
			public Long answer(InvocationOnMock invocation) throws Throwable {
				long ver = (long) invocation.getArguments()[2];
				return ver + 1;
			}
		});

		BufferedKVStorage bufStorage = new BufferedKVStorage(exStorage, verStorage, false);

		byte[] data = "ABC".getBytes("UTF-8");
		long v = bufStorage.set(Bytes.fromString("A"), data, -1);
		assertEquals(0, v);
		v = bufStorage.set(Bytes.fromString("A"), data, v);
		assertEquals(1, v);
		v = bufStorage.set(Bytes.fromString("A"), data, v);
		assertEquals(2, v);

		boolean ok = bufStorage.set(Bytes.fromString("B"), data, ExPolicy.NOT_EXISTING);
		assertTrue(ok);
		ok = bufStorage.set(Bytes.fromString("C"), data, ExPolicy.NOT_EXISTING);
		assertTrue(ok);
		ok = bufStorage.set(Bytes.fromString("D"), data, ExPolicy.NOT_EXISTING);
		assertTrue(ok);

		verify(verStorage, times(0)).set(any(), any(), anyLong());
		verify(exStorage, times(0)).set(any(), any(), any());

		bufStorage.flush();

		verify(verStorage, times(3)).set(any(), any(), anyLong());
		verify(verStorage, times(1)).set(eq(Bytes.fromString("A")), any(), eq(-1L));
		verify(verStorage, times(1)).set(eq(Bytes.fromString("A")), any(), eq(0L));
		verify(verStorage, times(1)).set(eq(Bytes.fromString("A")), any(), eq(1L));

		verify(exStorage, times(3)).set(any(), any(), any());
		verify(exStorage, times(1)).set(eq(Bytes.fromString("B")), any(), eq(ExPolicy.NOT_EXISTING));
		verify(exStorage, times(1)).set(eq(Bytes.fromString("C")), any(), eq(ExPolicy.NOT_EXISTING));
		verify(exStorage, times(1)).set(eq(Bytes.fromString("D")), any(), eq(ExPolicy.NOT_EXISTING));
	}

	// 改变了存储结构，此测试用例不再适合；
	// @Test
	// public void testDataSet() {
	//
	// ExistancePolicyKVStorageMap memoryExStorage = new
	// ExistancePolicyKVStorageMap();
	//
	// VersioningKVStorageMap memoryVerStorage = new VersioningKVStorageMap();
	//
	// long v = memoryVerStorage.set("A", "A1".getBytes(), -1);
	// assertEquals(0, v);
	// v = memoryVerStorage.set("A", "A2".getBytes(), 0);
	// assertEquals(1, v);
	// v = memoryVerStorage.set("A", "A3".getBytes(), 1);
	// assertEquals(2, v);
	// v = memoryVerStorage.set("B", "B1".getBytes(), -1);
	// assertEquals(0, v);
	//
	// BufferedKVStorage bufferedStorage = new BufferedKVStorage(memoryExStorage,
	// memoryVerStorage, false);
	//
	// // test versioning get;
	// byte[] value = bufferedStorage.get("A", 0);
	// assertEquals("A1", new String(value));
	//
	// value = bufferedStorage.get("A", -1);
	// assertEquals("A3", new String(value));
	//
	// value = bufferedStorage.get("A", 2);
	// assertEquals("A3", new String(value));
	//
	// value = bufferedStorage.get("B", 0);
	// assertEquals("B1", new String(value));
	//
	//
	// // test versioning buffered set;
	// v = bufferedStorage.set("C", "C1".getBytes(), -1);
	// assertEquals(v, 0);
	//
	// v = bufferedStorage.set("C", "C2".getBytes(), 0);
	// assertEquals(v, 1);
	//
	// v = bufferedStorage.set("D", "D1".getBytes(), -1);
	// assertEquals(v, 0);
	//
	// v = bufferedStorage.set("E", "E1".getBytes(), 0);
	// assertEquals(v, -1);
	//
	//
	// value = bufferedStorage.get("C", 0);
	// assertEquals("C1", new String(value));
	// value = bufferedStorage.get("C", 1);
	// assertEquals("C2", new String(value));
	// value = bufferedStorage.get("D", 0);
	// assertEquals("D1", new String(value));
	// value = bufferedStorage.get("E", 0);
	// assertNull(value);
	//
	// value = memoryVerStorage.get("C", 0);
	// assertNull(value);
	// value = memoryVerStorage.get("D", 0);
	// assertNull(value);
	// value = memoryVerStorage.get("E", 0);
	// assertNull(value);
	//
	// bufferedStorage.flush();
	//
	// value = memoryVerStorage.get("C", 0);
	// assertEquals("C1", new String(value));
	// value = memoryVerStorage.get("C", 1);
	// assertEquals("C2", new String(value));
	// value = memoryVerStorage.get("D", 0);
	// assertEquals("D1", new String(value));
	// value = memoryVerStorage.get("E", 0);
	// assertNull(value);
	// }

}
