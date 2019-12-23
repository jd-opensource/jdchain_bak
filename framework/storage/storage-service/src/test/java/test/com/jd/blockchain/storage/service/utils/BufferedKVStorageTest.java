package test.com.jd.blockchain.storage.service.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.utils.BufferedKVStorage;
import com.jd.blockchain.utils.Bytes;

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
}
