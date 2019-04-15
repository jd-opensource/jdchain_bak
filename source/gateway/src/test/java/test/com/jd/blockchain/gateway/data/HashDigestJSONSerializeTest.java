package test.com.jd.blockchain.gateway.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

public class HashDigestJSONSerializeTest {

	private static class TestData {

		private int id;

		private HashDigest hash;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public HashDigest getHash() {
			return hash;
		}

		public void setHash(HashDigest hash) {
			this.hash = hash;
		}

	}

	@Test
	public void test() throws Exception {
		JSONSerializeUtils.configSerialization(HashDigest.class, HashDigestSerializer.INSTANCE,
				HashDigestDeserializer.INSTANCE);

		HashFunction hashFunc = Crypto.getHashFunction("SHA256");
		HashDigest hash = hashFunc.hash("jd-test".getBytes());

		String hashJson = JSONSerializeUtils.serializeToJSON(hash, true);
		HashDigest hashDigest = JSONSerializeUtils.deserializeFromJSON(hashJson, HashDigest.class);

		assertArrayEquals(hash.getRawDigest(), hashDigest.getRawDigest());
		assertEquals(hash.getAlgorithm(), hashDigest.getAlgorithm());

		TestData data = new TestData();
		data.setHash(hash);
		data.setId(10);

		String json = JSONSerializeUtils.serializeToJSON(data, true);

		TestData desData = JSONSerializeUtils.deserializeFromJSON(json, TestData.class);
		assertEquals(data.getHash(), desData.getHash());
		assertEquals(data.getId(), desData.getId());
	}

}
