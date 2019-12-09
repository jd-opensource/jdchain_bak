package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import org.junit.Test;
import org.mockito.Mockito;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.MerkleDataNode;
import com.jd.blockchain.ledger.MerkleNode;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.ledger.core.MerkleTree;
import com.jd.blockchain.storage.service.utils.ExistancePolicyKVStorageMap;
import com.jd.blockchain.utils.Bytes;

public class MerkleTreeTest {

	private static String keyPrefix = "";

	@Test
	public void testWriteAndRead() {

		Random rand = new Random();

		CryptoSetting setting = Mockito.mock(CryptoSetting.class);
		when(setting.getHashAlgorithm()).thenReturn(ClassicAlgorithm.SHA256.code());
		when(setting.getAutoVerifyHash()).thenReturn(true);

		// 测试从空的树开始，顺序增加数据节点；
		ExistancePolicyKVStorageMap kvs1 = new ExistancePolicyKVStorageMap();
		MerkleTree mkt = new MerkleTree(setting, Bytes.fromString(keyPrefix), kvs1);

		// 初始化，按照顺序的序列号加入10条记录；
		int count = 18;
		byte[] dataBuf = new byte[16];
		MerkleDataNode[] dataNodes = new MerkleDataNode[count];
		for (int i = 0; i < count; i++) {
			rand.nextBytes(dataBuf);
			long sn = i;
			dataNodes[i] = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			assertEquals(sn, dataNodes[i].getSN());
		}
		mkt.commit();

		HashDigest rootHash = mkt.getRootHash();
		assertNotNull(rootHash);
		long maxSN = mkt.getMaxSn();
		long dataCount = mkt.getDataCount();
		assertEquals(count, dataCount);
		assertEquals(dataCount - 1, maxSN);// 仅仅在采用顺序加入的情况下成立；

		// reload;
		mkt = new MerkleTree(rootHash, setting, keyPrefix, kvs1, false);
		// 校验是否与之前的一致；
		maxSN = mkt.getMaxSn();
		dataCount = mkt.getDataCount();
		assertEquals(count, dataCount);
		assertEquals(dataCount - 1, maxSN);// 仅仅在采用顺序加入的情况下成立；

		// 取每一个数据节点
		for (int i = 0; i <= maxSN; i++) {
			MerkleDataNode dataNode = mkt.getData(i);
			assertEquals(i, dataNode.getSN());
			assertEquals(dataNodes[i].getNodeHash(), dataNode.getNodeHash());
			assertEquals(dataNodes[i].getKey(), dataNode.getKey());
			assertEquals(dataNodes[i].getLevel(), dataNode.getLevel());
			assertEquals(dataNodes[i].getVersion(), dataNode.getVersion());
		}
	}

	/**
	 * 测试以单次提交的方式顺序地插入数据；
	 */
	@Test
	public void testSequenceInsert_OneCommit() {
		CryptoSetting setting = Mockito.mock(CryptoSetting.class);
		when(setting.getHashAlgorithm()).thenReturn(ClassicAlgorithm.SHA256.code());
		when(setting.getAutoVerifyHash()).thenReturn(true);

		// 测试从空的树开始，顺序增加数据节点；
		ExistancePolicyKVStorageMap kvs1 = new ExistancePolicyKVStorageMap();

		// 创建空的的树；
		MerkleTree mkt = new MerkleTree(setting, keyPrefix, kvs1);

		// 查询空树的最大序列号，将返回 -1；
		long maxSN = mkt.getMaxSn();
		assertEquals(-1, maxSN);

		long sn = 0;
		// 加入 4097 条数据记录，预期构成以一颗 4 层 16 叉树；
		int count = 4097;
		byte[] dataBuf = new byte[16];
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			rand.nextBytes(dataBuf);
			mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			sn++;
		}
		mkt.commit();

		// 检查节点数；
		assertEquals(count, mkt.getDataCount());

		// 检查最大序列号的正确性；
		maxSN = mkt.getMaxSn();
		assertEquals(sn - 1, maxSN);

		// 预期扩展到 4 层;
		assertEquals(4, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 预期扩展为 4 层16叉树，由 3 层满16叉树扩展 1 新分支（4个路径节点）而形成；
		long expectedNodes = getMaxPathNodeCount(3) + 4 + 4097;
		assertEquals(expectedNodes, kvs1.getCount());

		// 验证每一个数据节点都产生了存在性证明；
		MerkleProof proof = null;
		for (int i = 0; i <= maxSN; i++) {
			proof = mkt.getProof(i);
			assertNotNull(proof);
		}
	}

	/**
	 * 测试以多次提交的方式顺序地插入数据；
	 */
	@Test
	public void testSequenceInsert_MultiCommit() {
		CryptoSetting setting = Mockito.mock(CryptoSetting.class);
		when(setting.getHashAlgorithm()).thenReturn(ClassicAlgorithm.SHA256.code());
		when(setting.getAutoVerifyHash()).thenReturn(true);

		// 测试从空的树开始，顺序增加数据节点；
		ExistancePolicyKVStorageMap kvs1 = new ExistancePolicyKVStorageMap();
		MerkleTree mkt = new MerkleTree(setting, keyPrefix, kvs1);

		long sn = 0;
		// 初始化，加入10条记录，预期目前的树只有一层；
		int count = 10;
		byte[] dataBuf = new byte[16];
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			rand.nextBytes(dataBuf);
			mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			sn++;
		}
		mkt.commit();

		assertEquals(count, mkt.getDataCount());
		// 检查最大序列号的正确性；
		long maxSN = mkt.getMaxSn();
		assertEquals(sn - 1, maxSN);

		assertEquals(1, mkt.getLevel());

		// 路径节点 + 数据节点；1 层只有一个路径节点；
		long expectedNodes = 1 + count;
		assertEquals(11, expectedNodes);

		assertEquals(expectedNodes, kvs1.getCount());

		// 验证每一个数据节点都产生了存在性证明；
		MerkleProof proof = null;
		for (int i = 0; i <= maxSN; i++) {
			proof = mkt.getProof(i);
			assertNotNull(proof);
		}

		// --------------------------
		// 再加入 6 条记录；总共 16 条，预期目前的树也只有一层；
		for (int i = 0; i < 6; i++) {
			rand.nextBytes(dataBuf);
			mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			sn++;
		}
		mkt.commit();

		count = 16;
		assertEquals(count, mkt.getDataCount());
		// 检查最大序列号的正确性；
		maxSN = mkt.getMaxSn();
		assertEquals(sn - 1, maxSN);

		// 预期只有 1 层;
		assertEquals(1, mkt.getLevel());

		// 路径节点 + 数据节点；1 层只有一个路径节点；新增了1个路径节点，以及新增 6 个数据节点；
		// 注：上一次未填充满的路径节点由于哈希变化而重新生成，因此多出 1 条；
		expectedNodes = expectedNodes + 1 + 6;
		assertEquals(expectedNodes, kvs1.getCount());

		// --------------------------
		// 再加入 10 条记录；总共 26 条数据，预期目前的树有2层[17-32]；
		for (int i = 0; i < 10; i++) {
			rand.nextBytes(dataBuf);
			mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			sn++;
		}
		mkt.commit();

		count = 26;
		assertEquals(count, mkt.getDataCount());
		// 预期扩展到 2 层;
		assertEquals(2, mkt.getLevel());

		// 路径节点 + 数据节点；扩展到 2 层, 新增加了2个路径节点（包括：新的根节点和其中1个子节点）,以及10个新增的数据节点；
		expectedNodes = expectedNodes + 2 + 10;
		assertEquals(expectedNodes, kvs1.getCount());

		// 验证每一个数据节点都产生了存在性证明；
		for (int i = 0; i <= maxSN; i++) {
			proof = mkt.getProof(i);
			assertNotNull(proof);
		}

		// ----------------------------------------------------------------
		// 再加入 230 条记录，总共 256 条数据记录，预期构成以一颗满的 2 层 16 叉树；
		for (int i = 0; i < 230; i++) {
			rand.nextBytes(dataBuf);
			mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			sn++;
		}
		mkt.commit();

		count = 256;
		assertEquals(count, mkt.getDataCount());
		// 检查最大序列号的正确性；
		maxSN = mkt.getMaxSn();
		assertEquals(sn - 1, maxSN);

		// 预期仍然维持 2 层;
		assertEquals(2, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 由于原来的两个未满的路径节点重新计算 hash 而新增加了2个路径节点，新增14个 Level(1) 的路径节点，加上230个新增数据记录；
		expectedNodes = expectedNodes + 2 + 14 + 230;
		assertEquals(expectedNodes, kvs1.getCount());

		// 验证每一个数据节点都产生了存在性证明；
		for (int i = 0; i <= maxSN; i++) {
			proof = mkt.getProof(i);
			assertNotNull(proof);
		}

		// ----------------------------------------------------------------
		// 再加入 3840 条记录，总共 4096 条数据记录，预期构成以一颗满的 3 层 16 叉树；
		for (int i = 0; i < 3840; i++) {
			rand.nextBytes(dataBuf);
			mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			sn++;
		}
		mkt.commit();

		count = 4096;
		assertEquals(count, mkt.getDataCount());
		// 检查最大序列号的正确性；
		maxSN = mkt.getMaxSn();
		assertEquals(sn - 1, maxSN);

		// 预期扩展到 3 层;
		assertEquals(3, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 预期扩展为 3 层的满16叉树，新增256的路径节点，加上3840个新增数据记录；
		expectedNodes = expectedNodes + 256 + 3840;
		assertEquals(expectedNodes, kvs1.getCount());

		// 验证每一个数据节点都产生了存在性证明；
		for (int i = 0; i <= maxSN; i++) {
			proof = mkt.getProof(i);
			assertNotNull(proof);
		}

		// ----------------------------------------------------------------
		// 再加入 1 条记录，总共 4097 条数据记录，预期构成以一颗 4 层 16 叉树；
		for (int i = 0; i < 1; i++) {
			rand.nextBytes(dataBuf);
			mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			sn++;
		}
		mkt.commit();

		count = 4097;
		assertEquals(count, mkt.getDataCount());
		// 检查最大序列号的正确性；
		maxSN = mkt.getMaxSn();
		assertEquals(sn - 1, maxSN);

		// 预期扩展到 4 层;
		assertEquals(4, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 预期扩展为 4 层16叉树，新增4个路径节点，加上1个新增数据记录；
		expectedNodes = expectedNodes + 4 + 1;
		assertEquals(expectedNodes, kvs1.getCount());

		// 验证每一个数据节点都产生了存在性证明；
		for (int i = 0; i <= maxSN; i++) {
			proof = mkt.getProof(i);
			assertNotNull(proof);
		}
	}

	/**
	 * 测试以多次提交的方式随机地插入数据；
	 */
	@Test
	public void testRandomInsert_MultiCommit() {
		CryptoSetting setting = Mockito.mock(CryptoSetting.class);
		when(setting.getHashAlgorithm()).thenReturn(ClassicAlgorithm.SHA256.code());
		when(setting.getAutoVerifyHash()).thenReturn(true);

		// 保存所有写入的数据节点的 SN-Hash 映射表；
		TreeMap<Long, HashDigest> dataNodes = new TreeMap<>();
		MerkleNode nd;

		// 测试从空的树开始，顺序增加数据节点；
		ExistancePolicyKVStorageMap kvs1 = new ExistancePolicyKVStorageMap();
		MerkleTree mkt = new MerkleTree(setting, keyPrefix, kvs1);

		// 加入 30 条数据记录，分两批各15条分别从序号两端加入，预期构成以一颗 4 层 16 叉树；
		int count = 4097;
		byte[] dataBuf = new byte[16];
		Random rand = new Random();
		long sn = 0;
		for (int i = 0; i < 15; i++) {
			rand.nextBytes(dataBuf);
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			dataNodes.put(sn, nd.getNodeHash());
			sn++;
		}
		sn = count - 1;

		for (int i = 0; i < 15; i++) {
			rand.nextBytes(dataBuf);
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			dataNodes.put(sn, nd.getNodeHash());
			sn--;
		}
		mkt.commit();

		assertEquals(30, mkt.getDataCount());
		// 检查最大序列号的正确性；
		long maxSN = mkt.getMaxSn();
		assertEquals(count - 1, maxSN);

		// 预期扩展到 4 层;
		assertEquals(4, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 预期扩展为 4 层16叉树，共9个路径节点，加30个数据节点；
		long expectedNodes = 9 + 30;
		assertEquals(expectedNodes, kvs1.getCount());

		// ---------------------------------
		// 在 15 - 4081 之间随机插入；
		int randomInterval = 4082 - 15;
		List<Long> snPool = new LinkedList<>();
		for (int i = 0; i < randomInterval; i++) {
			snPool.add(15L + i);
		}
		for (int i = 0; i < randomInterval; i++) {
			int selected = rand.nextInt(snPool.size());
			sn = snPool.remove(selected).longValue();
			rand.nextBytes(dataBuf);
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			dataNodes.put(sn, nd.getNodeHash());
		}
		mkt.commit();

		assertEquals(4097, mkt.getDataCount());
		// 检查最大序列号的正确性；
		maxSN = mkt.getMaxSn();
		assertEquals(count - 1, maxSN);

		// 预期扩展到 4 层;
		assertEquals(4, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 预期扩展为 4 层16叉树，之前的路径节点全部被重新计算哈希, 等同于在3层满16叉树之上加入1条记录，共273路径节点，加上 sn 为 4096
		// 的数据对于 4个路径节点（由于是上次写入的，4个路径节点中本次只更新了根节点）；
		expectedNodes = expectedNodes + 273 + 1 + randomInterval;
		assertEquals(expectedNodes, kvs1.getCount());

		// 验证每一个数据节点都产生了存在性证明；
		MerkleProof proof = null;
		for (Long n : dataNodes.keySet()) {
			proof = mkt.getProof(n.longValue());
			assertNotNull(proof);
			assertEquals(dataNodes.get(n), proof.getHash(0));
		}
	}

	/**
	 * 测试以单次提交的方式顺序地插入数据；
	 */
	@Test
	public void testDataModify() {
		CryptoSetting setting = Mockito.mock(CryptoSetting.class);
		when(setting.getHashAlgorithm()).thenReturn(ClassicAlgorithm.SHA256.code());
		when(setting.getAutoVerifyHash()).thenReturn(true);

		// 保存所有写入的数据节点的 SN-Hash 映射表；
		TreeMap<Long, HashDigest> dataNodes = new TreeMap<>();
		MerkleNode nd;

		// 测试从空的树开始，顺序增加数据节点；
		ExistancePolicyKVStorageMap storage = new ExistancePolicyKVStorageMap();

		// 创建空的的树；
		MerkleTree mkt = new MerkleTree(setting, keyPrefix, storage);

		long sn = 0;
		// 加入 4097 条数据记录，预期构成以一颗 4 层 16 叉树；
		int count = 4097;
		byte[] dataBuf = new byte[16];
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			rand.nextBytes(dataBuf);
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			dataNodes.put(sn, nd.getNodeHash());
			sn++;
		}
		mkt.commit();

		// 检查节点数；
		assertEquals(count, mkt.getDataCount());

		// 检查最大序列号的正确性；
		long expectedMaxSN = 4096;// count-1;
		long maxSN = mkt.getMaxSn();
		assertEquals(expectedMaxSN, maxSN);

		// 预期扩展到 4 层;
		assertEquals(4, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 预期扩展为 4 层16叉树，由 3 层满16叉树扩展 1 新分支（4个路径节点）而形成；
		long expectedNodes = getMaxPathNodeCount(3) + 4 + 4097;
		assertEquals(expectedNodes, storage.getCount());

		// 覆盖到每一路分支修改数据节点；
		int storageDataCountBefore = storage.getCount();
		// maxSn = 4096;
		int i;
		for (i = 0; i <= maxSN; i += 16) {
			rand.nextBytes(dataBuf);
			sn = i;
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			dataNodes.put(sn, nd.getNodeHash());
		}

		mkt.commit();
		// 检查节点数；
		assertEquals(count, mkt.getDataCount());

		// 检查最大序列号的正确性；
		maxSN = mkt.getMaxSn();
		assertEquals(expectedMaxSN, maxSN);

		// 由于覆盖到每一个分支节点，全部分支节点都重新生成，因此:
		// 新增节点数=修改的数据节点数 + 全部分支节点数；
		long addCounts = i / 16 + getMaxPathNodeCount(3) + 4;
		assertEquals(storageDataCountBefore + addCounts, storage.getCount());

		// 验证每一个数据节点都产生了存在性证明；
		MerkleProof proof = null;
		for (Long n : dataNodes.keySet()) {
			proof = mkt.getProof(n.longValue());
			assertNotNull(proof);
			HashDigest expHash = dataNodes.get(n);
			assertEquals(expHash.toBase58(), proof.getHash(0).toBase58());
		}

	}

	/**
	 * 测试单独修改版本而不变更数据时，是否能够正确地更新 merkle 树；；
	 */
	@Test
	public void testDataVersionModify() {
		CryptoSetting setting = Mockito.mock(CryptoSetting.class);
		when(setting.getHashAlgorithm()).thenReturn(ClassicAlgorithm.SHA256.code());
		when(setting.getAutoVerifyHash()).thenReturn(true);

		// 保存所有写入的数据节点的 SN-Hash 映射表；
		TreeMap<Long, HashDigest> dataNodes = new TreeMap<>();
		MerkleNode nd;

		// 测试从空的树开始，顺序增加数据节点；
		ExistancePolicyKVStorageMap storage = new ExistancePolicyKVStorageMap();

		// 创建空的的树；
		MerkleTree mkt = new MerkleTree(setting, keyPrefix, storage);

		long sn = 0;
		// 加入 4097 条数据记录，预期构成以一颗 4 层 16 叉树；
		int count = 4097;
		byte[] dataBuf = new byte[16];
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			rand.nextBytes(dataBuf);
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			dataNodes.put(sn, nd.getNodeHash());
			sn++;
		}
		mkt.commit();
		byte[] dataOfMaxSnNode = Arrays.copyOf(dataBuf, dataBuf.length);

		// 检查节点数；
		assertEquals(count, mkt.getDataCount());

		// 检查最大序列号的正确性；
		long expectedMaxSN = 4096;// count-1;
		long maxSN = mkt.getMaxSn();
		assertEquals(expectedMaxSN, maxSN);

		// 预期扩展到 4 层;
		assertEquals(4, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 预期扩展为 4 层16叉树，由 3 层满16叉树扩展 1 新分支（4个路径节点）而形成；
		long expectedNodes = getMaxPathNodeCount(3) + 4 + 4097;
		assertEquals(expectedNodes, storage.getCount());

		// 仅仅更新最大的 sn 的数据节点的版本(由 0 升级为 1)，预期将产生 4 个更新的路径节点和 1 个新的数据节点；
		long currNodes = expectedNodes;
		mkt.setData(maxSN, "KEY-" + maxSN, 1, dataOfMaxSnNode);
		mkt.commit();

		// 验证；
		maxSN = mkt.getMaxSn();
		assertEquals(expectedMaxSN, maxSN);

		// 预期扩展到 4 层;
		assertEquals(4, mkt.getLevel());

		// 路径节点 + 数据节点；
		// 预期将产生 4 个更新的路径节点和 1 个新的数据节点；
		expectedNodes = currNodes + 4 + 1;
		assertEquals(expectedNodes, storage.getCount());
	}

	/**
	 * 测试从存储重新加载 Merkle 树的正确性；
	 */
	/**
	 * 
	 */
	@Test
	public void testMerkleReload() {
		CryptoSetting setting = Mockito.mock(CryptoSetting.class);
		when(setting.getHashAlgorithm()).thenReturn(ClassicAlgorithm.SHA256.code());
		when(setting.getAutoVerifyHash()).thenReturn(true);

		// 保存所有写入的数据节点的 SN-Hash 映射表；
		TreeMap<Long, HashDigest> expectedDataNodes = new TreeMap<>();
		MerkleNode nd;

		// 测试从空的树开始，顺序增加数据节点；
		ExistancePolicyKVStorageMap storage = new ExistancePolicyKVStorageMap();

		// 创建空的的树；
		MerkleTree mkt = new MerkleTree(setting, keyPrefix, storage);

		long sn = 0;
		// 加入 4097 条数据记录，预期构成以一颗 4 层 16 叉树；
		int count = 4097;
		byte[] dataBuf = new byte[16];
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			rand.nextBytes(dataBuf);
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			expectedDataNodes.put(sn, nd.getNodeHash());
			sn++;
		}
		mkt.commit();

		// 记录一次提交的根哈希以及部分节点信息，用于后续的加载校验；
		HashDigest r1_rootHash = mkt.getRootHash();
		long r1_dataCount = mkt.getDataCount();
		long r1_maxSN = mkt.getMaxSn();
		long r1_sn1 = r1_maxSN;
		String r1_proof1 = mkt.getProof(r1_sn1).toString();
		long r1_sn2 = 1024;
		String r1_proof2 = mkt.getProof(r1_sn2).toString();

		{
			// 检查节点数；
			assertEquals(count, mkt.getDataCount());

			// 检查最大序列号的正确性；
			long maxSN = mkt.getMaxSn();
			long expectedMaxSN = 4096;// count-1;
			assertEquals(expectedMaxSN, maxSN);

			// 预期扩展到 4 层;
			assertEquals(4, mkt.getLevel());

			// 路径节点 + 数据节点；
			// 预期扩展为 4 层16叉树，由 3 层满16叉树扩展 1 新分支（4个路径节点）而形成；
			long expectedNodes = getMaxPathNodeCount(3) + 4 + 4097;
			assertEquals(expectedNodes, storage.getCount());
			
			//重新加载，判断数据是否正确；
			MerkleTree r1_mkt = new MerkleTree(r1_rootHash, setting, keyPrefix, storage, true);
			{
				// 验证每一个数据节点都产生了存在性证明；
				MerkleProof proof = null;
				HashDigest expectedNodeHash = null;
				MerkleDataNode reallyDataNode = null;
				for (long n = 0; n < maxSN; n++) {
					expectedNodeHash = expectedDataNodes.get(n);
					reallyDataNode = r1_mkt.getData(n);
					assertEquals(expectedNodeHash, reallyDataNode.getNodeHash());
					
					proof = r1_mkt.getProof(n);
					assertNotNull(proof);
					assertEquals(expectedNodeHash, proof.getHash(0));
				}
			}
		}

		// 覆盖到每一路分支修改数据节点；
		int storageDataCountBefore = storage.getCount();
		// maxSn = 4096;
		long maxSN = mkt.getMaxSn();
		int i;
		for (i = 0; i <= maxSN; i += 16) {
			rand.nextBytes(dataBuf);
			sn = i;
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			expectedDataNodes.put(sn, nd.getNodeHash());
		}

		mkt.commit();

		// 记录一次提交的根哈希以及部分节点信息，用于后续的加载校验；
		HashDigest r2_rootHash = mkt.getRootHash();
		long r2_dataCount = mkt.getDataCount();
		long r2_maxSN = mkt.getMaxSn();
		long r2_sn1 = r1_sn1;
		String r2_proof1 = mkt.getProof(r2_sn1).toString();
		long r2_sn2 = r1_sn2;
		String r2_proof2 = mkt.getProof(r2_sn2).toString();

		{
			// 检查节点数；
			assertEquals(count, mkt.getDataCount());

			assertEquals(r1_dataCount, r2_dataCount);

			// 检查最大序列号的正确性；
			maxSN = mkt.getMaxSn();
			long expectedMaxSN = 4096;// count-1;
			assertEquals(expectedMaxSN, maxSN);

			// 由于覆盖到每一个分支节点，全部分支节点都重新生成，因此:
			// 新增节点数=修改的数据节点数 + 全部分支节点数；
			long addCounts = i / 16 + getMaxPathNodeCount(3) + 4;
			assertEquals(storageDataCountBefore + addCounts, storage.getCount());
		}

		// 新插入数据；
		final int NEW_INSERTED_COUNT = 18;
		for (i = 0; i < NEW_INSERTED_COUNT; i++) {
			rand.nextBytes(dataBuf);
			sn = maxSN + 1 + i;
			nd = mkt.setData(sn, "KEY-" + sn, 0, dataBuf);
			expectedDataNodes.put(sn, nd.getNodeHash());
		}
		mkt.commit();

		{
			// 验证每一个数据节点都产生了存在性证明；
			MerkleProof proof = null;
			for (Long n : expectedDataNodes.keySet()) {
				proof = mkt.getProof(n.longValue());
				assertNotNull(proof);
				assertEquals(expectedDataNodes.get(n), proof.getHash(0));
			}
		}

		// 记录一次提交的根哈希以及部分节点信息，用于后续的加载校验；
		HashDigest r3_rootHash = mkt.getRootHash();
		long r3_maxSN = mkt.getMaxSn();
		long r3_sn1 = r2_sn1;
		String r3_proof1 = mkt.getProof(r3_sn1).toString();
		long r3_sn2 = r2_sn2;
		String r3_proof2 = mkt.getProof(r3_sn2).toString();
		long r3_sn3 = 4096 + NEW_INSERTED_COUNT - 5;
		String r3_proof3 = mkt.getProof(r3_sn3).toString();

		{
			// 检查节点数；
			assertEquals(count + NEW_INSERTED_COUNT, mkt.getDataCount());

			// 检查最大序列号的正确性；
			maxSN = mkt.getMaxSn();
			long expectedMaxSN = 4096 + NEW_INSERTED_COUNT;// count-1;
			assertEquals(expectedMaxSN, maxSN);
		}

		// --------------------
		// 重新从存储加载生成新的 MerkleTree 实例，验证与初始实例的一致性；
		// 从第 2 轮提交的 Merkle 根哈希加载；
		MerkleTree r1_mkt = new MerkleTree(r1_rootHash, setting, keyPrefix, storage, true);
		assertEquals(r1_maxSN, r1_mkt.getMaxSn());
		assertEquals(r1_rootHash, r1_mkt.getRootHash());
		assertEquals(r1_dataCount, r1_mkt.getDataCount());
		assertEquals(r1_proof1, r1_mkt.getProof(r1_sn1).toString());
		assertEquals(r1_proof2, r1_mkt.getProof(r1_sn2).toString());


		// 从第 2 轮提交的 Merkle 根哈希加载；
		// 第 2 轮生成的 Merkle 树是对第 1 轮的数据的全部节点的修改，因此同一个 SN 的节点的证明是不同的；
		MerkleTree r2_mkt = new MerkleTree(r2_rootHash, setting, keyPrefix, storage, true);
		assertEquals(r1_maxSN, r2_mkt.getMaxSn());
		assertEquals(r1_dataCount, r2_mkt.getDataCount());

		assertNotEquals(r1_rootHash, r2_mkt.getRootHash());
		assertNotEquals(r1_proof1, r2_mkt.getProof(r1_sn1).toString());
		assertNotEquals(r1_proof2, r2_mkt.getProof(r1_sn2).toString());

		assertEquals(r2_maxSN, r2_mkt.getMaxSn());
		assertEquals(r2_rootHash, r2_mkt.getRootHash());
		assertEquals(r2_dataCount, r2_mkt.getDataCount());
		assertEquals(r2_proof1, r2_mkt.getProof(r2_sn1).toString());
		assertEquals(r2_proof2, r2_mkt.getProof(r2_sn2).toString());

		// 从第 3 轮提交的 Merkle 根哈希加载；
		// 第 3 轮生成的 Merkle 树是在第 2 轮的数据基础上做新增，因此非新增的同一个 SN 的节点的Merkle证明是相同的；
		MerkleTree r3_mkt = new MerkleTree(r3_rootHash, setting, keyPrefix, storage, true);
		assertEquals(r2_maxSN + NEW_INSERTED_COUNT, r3_mkt.getMaxSn());
		assertNotEquals(r2_rootHash, r3_mkt.getRootHash());
		assertEquals(r2_dataCount + NEW_INSERTED_COUNT, r3_mkt.getDataCount());

		assertEquals(r3_maxSN, r3_mkt.getMaxSn());
		assertEquals(r3_rootHash, r3_mkt.getRootHash());
		assertEquals(r3_proof1, r3_mkt.getProof(r3_sn1).toString());
		assertEquals(r3_proof2, r3_mkt.getProof(r3_sn2).toString());
		assertEquals(r3_proof3, r3_mkt.getProof(r3_sn3).toString());

		// 验证每一个数据节点都产生了存在性证明；
		{
			MerkleProof proof = null;
			for (Long n : expectedDataNodes.keySet()) {
				proof = r3_mkt.getProof(n.longValue());
				assertNotNull(proof);
				assertEquals(expectedDataNodes.get(n), proof.getHash(0));
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static int getLevel(long dataCount) {
		if (dataCount < 0) {
			throw new IllegalArgumentException("The specified data count is negative!");
		}
		int l = 1;
		while (dataCount > power(MerkleTree.TREE_DEGREE, l)) {
			l++;
		}
		return l;
	}

	/**
	 * 计算整个 Merkle 树的路径节点的最大数量；
	 * 
	 * @param level
	 * @return
	 */
	private static long getMaxPathNodeCount(int level) {
		if (level < 1) {
			throw new IllegalArgumentException("The specified level is less than 1!");
		}
		long count = 0;
		for (int i = 0; i < level; i++) {
			count += power(MerkleTree.TREE_DEGREE, i);
		}
		return count;
	}

	/**
	 * 计算 value 的 x 次方；
	 * <p>
	 * 注：此方法不处理溢出；调用者需要自行规避；
	 * 
	 * @param value
	 * @param x     大于等于 0 的整数；
	 * @return
	 */
	private static long power(long value, int x) {
		if (x == 0) {
			return 1;
		}
		long r = value;
		for (int i = 1; i < x; i++) {
			r *= value;
		}
		return r;
	}
}
