package com.jd.blockchain.ledger.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.MerkleDataNode;
import com.jd.blockchain.ledger.MerkleNode;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesSerializable;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.NumberMask;

/**
 * 默克尔树；
 * <p>
 * 树的level是按照倒置的方式计算，而不是以根节点的距离衡量，即叶子节点的 level 是 0； <br>
 * 所有的数据的哈希索引都以叶子节点进行记录; <br>
 * 每一个数据节点都以标记一个序列号（Sequence Number, 缩写为 SN），并按照序列号的大小统一地在 level 0
 * 上排列，并填充从根节点到数据节点的所有路径节点； <br>
 * 随着数据节点的增加，整棵树以倒置方式向上增长（根节点在上，叶子节点在下），此设计带来显著特性是已有节点的信息都可以不必修改；
 * 
 * <p>
 * <strong>注：此实现不是线程安全的；</strong><br>
 * 但由于对单个账本中的写入过程被设计为同步写入，因而非线程安全的设计并不会影响在此场景下的使用，而且由于省去了线程间同步操作，反而提升了性能；
 * 
 * @author huanghaiquan
 *
 */
public class MerkleTree implements Transactional {

	public static final int TREE_DEGREE = 16;

	public static final int MAX_LEVEL = 14;

	// 正好是 2 的 56 次方(7字节），将 SN 8个字节中的首个字节预留作为 DataNode 的编码格式版本标记；
	public static final long MAX_DATACOUNT = power(TREE_DEGREE, MAX_LEVEL);

	public static final long MAX_SN = MAX_DATACOUNT - 1;

	public static final String NODE_PREFIX = "MKT://";

	public static final String PATH_SEPERATOR = "/";

	public static final boolean PARALLEL;

	static {
		// get property from System;
		PARALLEL = Boolean.getBoolean("parallel-merkle");
		System.out.println("------ [[ parallel-merkle=" + PARALLEL + " ]] -----");
	}

	private final Bytes keyPrefix;

	private CryptoSetting setting;

	private ExPolicyKVStorage kvStorage;

	private boolean readonly;

	private SortedMap<Long, DataNode> updatedDataNodes = Collections.synchronizedSortedMap(new TreeMap<>());

	private PathNode root;

	/**
	 * Merkle树的根哈希；
	 * 
	 * @return
	 */
	public HashDigest getRootHash() {
		return root.getNodeHash();
	}

	/**
	 * 当前 Merkle 数据记录的数据节点的总数；
	 * 
	 * @return
	 */
	public long getDataCount() {
		return root.getDataCount();
	}

	/**
	 * 节点的层级；
	 * <p>
	 * 叶子节点(即数据节点)的层级为 0；每一级父节点的层级加 1 ；
	 * 
	 * @return
	 */
	public int getLevel() {
		return root.getLevel();
	}

	/**
	 * 构建空的树；
	 * 
	 * @param kvStorage
	 */
	public MerkleTree(CryptoSetting setting, String keyPrefix, ExPolicyKVStorage kvStorage) {
		this(null, setting, Bytes.fromString(keyPrefix), kvStorage, false);
	}

	/**
	 * 构建空的树；
	 * 
	 * @param kvStorage
	 */
	public MerkleTree(CryptoSetting setting, Bytes keyPrefix, ExPolicyKVStorage kvStorage) {
		this(null, setting, keyPrefix, kvStorage, false);
	}

	// /**
	// * 创建一颗可写的 Merkle 树；
	// *
	// * @param rootHash
	// * 节点的根Hash; 如果指定为 null，则实际上创建一个空的 Merkle Tree；
	// * @param verifyOnLoad
	// * 从外部存储加载节点时是否校验节点的哈希；
	// * @param kvStorage
	// * 保存 Merkle 节点的存储服务；
	// */
	// public MerkleTree(HashDigest rootHash, CryptoSetting setting,
	// ExistentialKVStorage kvStorage) {
	// this(rootHash, setting, kvStorage, false);
	// }

	/**
	 * 创建 Merkle 树；
	 * 
	 * @param rootHash     节点的根Hash; 如果指定为 null，则实际上创建一个空的 Merkle Tree；
	 * @param verifyOnLoad 从外部存储加载节点时是否校验节点的哈希；
	 * @param kvStorage    保存 Merkle 节点的存储服务；
	 * @param readonly     是否只读；
	 */
	public MerkleTree(HashDigest rootHash, CryptoSetting setting, String keyPrefix, ExPolicyKVStorage kvStorage,
			boolean readonly) {
		this(rootHash, setting, Bytes.fromString(keyPrefix), kvStorage, readonly);
	}

	/**
	 * 创建 Merkle 树；
	 * 
	 * @param rootHash     节点的根Hash; 如果指定为 null，则实际上创建一个空的 Merkle Tree；
	 * @param verifyOnLoad 从外部存储加载节点时是否校验节点的哈希；
	 * @param kvStorage    保存 Merkle 节点的存储服务；
	 * @param readonly     是否只读；
	 */
	public MerkleTree(HashDigest rootHash, CryptoSetting setting, Bytes keyPrefix, ExPolicyKVStorage kvStorage,
			boolean readonly) {
		this.setting = setting;
		this.keyPrefix = keyPrefix;
		this.kvStorage = kvStorage;
		this.readonly = readonly;
		if (rootHash == null) {
			root = new PathNode(setting.getHashAlgorithm(), 0, (byte) 1, 0);
		} else {
			PathNode rootNode = loadPathNode(rootHash, setting.getAutoVerifyHash());
			if (rootNode == null) {
				throw new IllegalStateException(
						"The root path node[" + Base58Utils.encode(rootHash.toBytes()) + "] not exist!");
			}
			if (rootNode.getStartingSN() != 0) {
				String hashStr = Base58Utils.encode(rootNode.getNodeHash().toBytes());
				throw new MerkleProofException(String.format(
						"The starting sn of the specified merkle root node is not zero!  --[RootNodeHash=%s]",
						hashStr));
			}
			this.root = rootNode;
		}
	}

	/**
	 * 返回数据的默克尔证明；
	 * 
	 * <p>
	 * 如果不存在，则返回 null；
	 * <p>
	 * 如果 sn 超出范围，则引发 {@link IndexOutOfBoundsException} ；
	 * 
	 * @param sn 数据的序列号；
	 * @return 默克尔证明的实例；
	 */
	public MerkleProof getProof(long sn) {
		MerkleNode[] nodePath = new MerkleNode[root.level + 1];
		MerkleDataNode dataNode = seekPath(sn, nodePath);
		if (dataNode == null) {
			return null;
		}
		for (int i = 0; i < nodePath.length; i++) {
			if (i < nodePath.length - 1) {
				// PathNode will be changed on updating data;
				// So record the path info with the immutable ProofNodeEntry instead;
				PathNode n = (PathNode) nodePath[i];
				ProofNodeEntry p = new ProofNodeEntry();
				p.nodeHash = n.getNodeHash();
				p.level = n.getLevel();
				p.dataCount = n.getDataCount();
				p.startingSN = n.getStartingSN();
				nodePath[i] = p;
			}
		}
		return new MerkleProofImpl(sn, nodePath);
	}

	/**
	 * 以指定序号建立对指定键值的索引；
	 * <p>
	 * 
	 * <p>
	 * 此方法不会立即更新整棵默克尔树，直到方法 {@link #commit()} 被调用；
	 * 
	 * <p>
	 * 注：默克尔树只保存指定数据的哈希以及关联的键，而不会保存数据原文，因此调用者需要自己处理对数据的存储； <br>
	 * 此外，哈希计算是把键和数据内容拼接一起进行计算的；
	 * 
	 * @param sn         与此数据唯一相关的序列号；sn 必须大于等于 0 ；
	 * @param key        与此数据唯一相关的键；
	 * @param version
	 * @param hashedData 要参与哈希计算的数据内容；注：此参数值并不会被默克尔树保存；
	 * @return
	 */
	public MerkleDataNode setData(long sn, String key, long version, byte[] hashedData) {
		return setData(sn, Bytes.fromString(key), version, hashedData);
	}

	/**
	 * 以指定序号建立对指定键值的索引；
	 * <p>
	 * 
	 * <p>
	 * 此方法不会立即更新整棵默克尔树，直到方法 {@link #commit()} 被调用；
	 * 
	 * <p>
	 * 注：默克尔树只保存指定数据的哈希以及关联的键，而不会保存数据原文，因此调用者需要自己处理对数据的存储； <br>
	 * 此外，哈希计算是把键和数据内容拼接一起进行计算的；
	 * 
	 * @param sn         与此数据唯一相关的序列号；sn 必须大于等于 0 ；
	 * @param key        与此数据唯一相关的键；
	 * @param version
	 * @param hashedData 要参与哈希计算的数据内容；注：此参数值并不会被默克尔树保存；
	 * @return
	 */
	public MerkleDataNode setData(long sn, Bytes key, long version, byte[] hashedData) {
		if (readonly) {
			throw new IllegalStateException("This merkle tree is readonly!");
		}
		if (sn < 0) {
			throw new IllegalArgumentException("The sn is negative!");
		}
		if (sn > MAX_SN) {
			throw new IllegalArgumentException("The sn is great than MAX[" + MAX_SN + "]!");
		}
		DataNode dataNode = MerkleTreeEncoder.LATEST_DATANODE_ENCODER.create(setting.getHashAlgorithm(), sn, key,
				version, hashedData);
		updatedDataNodes.put(sn, dataNode);
		return dataNode;
	}

	public MerkleDataNode getData(long sn) {
		DataNode dataNode = updatedDataNodes.get(sn);
		if (dataNode != null) {
			return dataNode;
		}
		return seekPath(sn, null);
	}

	/**
	 * Return the max sequence number in all data nodes; <br>
	 * 
	 * Return -1 if no data node existed;
	 * 
	 * @return
	 */
	public long getMaxSn() {
		DataNode maxDataNode = getMaxDataNode();
		if (maxDataNode == null) {
			return -1;
		}
		return maxDataNode.sn;
	}

	private DataNode getMaxDataNode() {
		MerkleNode node = root;
		PathNode pathNode;
		int idx = -1;
		while (node.getLevel() > 0) {
			pathNode = (PathNode) node;
			// find the last child, because all children are ascension sorted by sn;
			for (idx = pathNode.childrenHashes.length - 1; idx > -1; idx--) {
				if (pathNode.childrenHashes[idx] != null) {
					break;
				}
			}
			if (idx == -1) {
				// no child;
				return null;
			}
			// if child node have been loaded, then load it;
			if (pathNode.children[idx] == null) {
				if (pathNode.getLevel() > 1) {
					// load path node;
					PathNode child = loadPathNode(pathNode.childrenHashes[idx], setting.getAutoVerifyHash());
					pathNode.attachChildNode(child, idx);
				} else {
					DataNode child = loadDataNode(pathNode.childrenHashes[idx], setting.getAutoVerifyHash());
					pathNode.attachChildNode(child, idx);
				}
			}
			node = pathNode.children[idx];
		}
		return (DataNode) node;
	}

	@Override
	public boolean isUpdated() {
		return updatedDataNodes.size() > 0;
	}

	/**
	 * 回滚上一次提交以来的变更；
	 */
	@Override
	public void cancel() {
		updatedDataNodes.clear();
	}

	/**
	 * 根据新修改重新计算哈希，并将产生变更的节点保存到存储服务；<br>
	 * 
	 * 注：调用者在执行批量更改时，不应该在每一次更改之后立即调用此方法，而是完成批量操作之后，仅调用此一次 commit 操作，这样可以更改的中间重复地对
	 * hash 节点进行计算，达到性能优化的目的；
	 */
	@Override
	public void commit() {
		if (!isUpdated()) {
			return;
		}
		long sn;
		PathNode leafPathNode = null;
		// 按照升序处理；
		Set<PathNode> updatedLeafNodes = new HashSet<>();
		for (Entry<Long, DataNode> entry : updatedDataNodes.entrySet()) {
			sn = entry.getKey().longValue();
			if (leafPathNode == null || sn >= leafPathNode.startingSN + leafPathNode.interval) {
				leafPathNode = extendPaths(sn);
			}
			leafPathNode.setData(sn, entry.getValue());
			updatedLeafNodes.add(leafPathNode);
		}

		// 遍历涉及更改的所有路径节点，重新计算根节点哈希；
		if (PARALLEL) {
			concurrentRehash(updatedLeafNodes);
		} else {
			rehash(updatedLeafNodes);
		}

		// List<AbstractMerkleNode> updatedNodes = Collections.synchronizedList(new
		// LinkedList<>());
		// rehash(root, updatedNodes);
		//
		// // 把更改的节点写入到存储服务；
		// for (AbstractMerkleNode merkleNode : updatedNodes) {
		// String key = encodeNodeKey(merkleNode.getNodeHash());
		// boolean nx = kvStorage.set(key, merkleNode.toBytes(), ExPolicy.NOT_EXISTING);
		// if (!nx) {
		// throw new LedgerException("Merkle node already exist!");
		// }
		// }

		// 清空缓存；
		updatedDataNodes.clear();
	}

	private void saveNode(AbstractMerkleNode merkleNode) {
		Bytes key = encodeNodeKey(merkleNode.getNodeHash());
		boolean nx = kvStorage.set(key, merkleNode.toBytes(), ExPolicy.NOT_EXISTING);
		if (!nx) {
			throw new LedgerException("Merkle node already exist!");
		}
	}

	private void rehash(Set<PathNode> updatedPathNodes) {

		Set<PathNode> updatedParentNodes = new HashSet<>();

		for (PathNode pathNode : updatedPathNodes) {
			AbstractMerkleNode[] children = pathNode.children;
			HashDigest[] childrenHashes = pathNode.childrenHashes;
			boolean updated = false;
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null) {
					continue;
				}
				HashDigest origChildHash = childrenHashes[i];
				HashDigest newChildHash = children[i].getNodeHash();
				if (origChildHash == null) {
					childrenHashes[i] = newChildHash;
					updated = true;
					if (pathNode.level == 1) {
						// 在叶子节点上发现新增加了数据节点；
						pathNode.increaseDataCount(1);
						// 同时保存新增的数据节点；
						saveNode(children[i]);
					}
				} else if (!origChildHash.equals(newChildHash)) {
					childrenHashes[i] = newChildHash;
					updated = true;
					if (pathNode.level == 1) {
						// 同时保存更新的数据节点；
						saveNode(children[i]);
					}
				}
			}

			if (updated) {
				// 计算节点哈希：
				pathNode.rehash();

				saveNode(pathNode);

				if (pathNode.parent != null) {
					updatedParentNodes.add((PathNode) pathNode.parent);
				}
			}
		}

		if (updatedParentNodes.size() > 0) {
			rehash(updatedParentNodes);
		}
	}

	private void concurrentRehash(Set<PathNode> updatedPathNodes) {

		// Set<PathNode> updatedParentNodes = new HashSet<>();
		// List<RehashTask> tasks = new ArrayList<>();
		// for (PathNode pathNode : updatedPathNodes) {
		// RehashTask task = new RehashTask(pathNode, kvStorage);
		// ForkJoinPool.commonPool().execute(task);
		// tasks.add(task);
		// }
		PathNode[] nodes = updatedPathNodes.toArray(new PathNode[updatedPathNodes.size()]);
		RehashTask task = new RehashTask(nodes, this, kvStorage);
		List<PathNode> updatedNodes = ForkJoinPool.commonPool().invoke(task);
		Set<PathNode> updatedParentNodes = new HashSet<>();
		for (PathNode pathNode : updatedNodes) {
			if (pathNode.parent != null) {
				updatedParentNodes.add((PathNode) pathNode.parent);
			}
		}
		if (updatedParentNodes.size() > 0) {
			rehash(updatedParentNodes);
		}
	}

	/**
	 * @author huanghaiquan
	 *
	 */
	private static class RehashTask extends RecursiveTask<List<PathNode>> {

		private static final long serialVersionUID = -9165021733321713070L;

		private MerkleTree tree;

		private PathNode[] pathNodes;

		private ExPolicyKVStorage kvStorage;

		private static int TASK_THRESHOLD = 100;

		public RehashTask(PathNode[] pathNodes, MerkleTree tree, ExPolicyKVStorage kvStorage) {
			this.tree = tree;
			this.pathNodes = pathNodes;
			this.kvStorage = kvStorage;
		}

		@Override
		protected List<PathNode> compute() {
			int count = pathNodes.length;
			if (count > TASK_THRESHOLD) {
				PathNode[] nodes1 = new PathNode[count / 2];
				PathNode[] nodes2 = new PathNode[count - nodes1.length];
				System.arraycopy(pathNodes, 0, nodes1, 0, nodes1.length);
				System.arraycopy(pathNodes, nodes1.length, nodes2, 0, nodes2.length);
				RehashTask task1 = new RehashTask(nodes1, tree, kvStorage);
				RehashTask task2 = new RehashTask(nodes2, tree, kvStorage);
				ForkJoinTask.invokeAll(task1, task2);
				List<PathNode> updatedNodes = task1.join();
				updatedNodes.addAll(task2.join());
				return updatedNodes;
			} else {
				List<PathNode> updatedNodes = new ArrayList<>();
				for (PathNode pathNode : pathNodes) {
					if (rehash(pathNode)) {
						updatedNodes.add(pathNode);
					}
				}
				return updatedNodes;
			}
		}

		private boolean rehash(PathNode pathNode) {
			AbstractMerkleNode[] children = pathNode.children;
			HashDigest[] childrenHashes = pathNode.childrenHashes;
			boolean updated = false;
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null) {
					continue;
				}
				HashDigest origChildHash = childrenHashes[i];
				HashDigest newChildHash = children[i].getNodeHash();
				if (origChildHash == null) {
					childrenHashes[i] = newChildHash;
					updated = true;
					if (pathNode.level == 1) {
						// 在叶子节点上发现新增加了数据节点；
						// newDataCount++;
						pathNode.increaseDataCount(1);
						// 同时保存新增的数据节点；
						tree.saveNode(children[i]);
					}
				} else if (!origChildHash.equals(newChildHash)) {
					childrenHashes[i] = newChildHash;
					updated = true;
					if (pathNode.level == 1) {
						// 同时保存更新的数据节点；
						tree.saveNode(children[i]);
					}
				}
			}

			if (updated) {
				// 计算节点哈希：
				pathNode.rehash();
				tree.saveNode(pathNode);

				// if (pathNode.parent != null) {
				// return (PathNode) pathNode.parent;
				// }
				return true;
			}

			return false;
		}

		// private void saveNode(AbstractMerkleNode merkleNode) {
		// String key = encodeNodeKey(merkleNode.getNodeHash());
		// boolean nx = kvStorage.set(key, merkleNode.toBytes(), ExPolicy.NOT_EXISTING);
		// if (!nx) {
		// throw new LedgerException("Merkle node already exist!");
		// }
		// }

	}

	/**
	 * 重新计算所有子节点以及自身的哈希，并返回新加入的数据节点的数量；
	 * 
	 * @param pathNode     需要重新计算 hash 的路径节点；
	 * @param updatedNodes 用于记录已更新节点的列表；
	 * @return
	 */
	@SuppressWarnings("unused")
	private int rehash(PathNode pathNode, List<AbstractMerkleNode> updatedNodes) {
		// int newDataCount = 0;
		boolean updated = false;

		// 先检查并更新子节点的 hash；
		AbstractMerkleNode[] children = pathNode.children;
		HashDigest[] childrenHashes = pathNode.childrenHashes;

		if (pathNode.level == 1) {
			// 检查作为叶子的数据节点的哈希是否更新；
			// 注：因为数据节点加入时已经进行过计算了它本身的哈希，在此不需重新计算；
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null) {
					continue;
				}
				HashDigest origChildHash = childrenHashes[i];
				HashDigest newChildHash = children[i].getNodeHash();
				if (origChildHash == null) {
					// newDataCount++;
					pathNode.increaseDataCount(1);
					childrenHashes[i] = newChildHash;
					updated = true;
					updatedNodes.add(children[i]);
				} else if (!origChildHash.equals(newChildHash)) {
					childrenHashes[i] = newChildHash;
					updated = true;
					updatedNodes.add(children[i]);
				}
			}
		} else {
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null) {
					continue;
				}

				// 递归重新计算子路径的哈希；
				// 更新数据节点数量；
				// newDataCount += rehash((PathNode) children[i], updatedNodes);
				rehash((PathNode) children[i], updatedNodes);

				HashDigest origChildHash = childrenHashes[i];
				HashDigest newChildHash = children[i].getNodeHash();
				if (origChildHash == null || !origChildHash.equals(newChildHash)) {
					childrenHashes[i] = newChildHash;
					updated = true;
				}
			}
		}

		// 如果子节点发生了更改，则重新计算当前节点的 hash；
		// 注：当加入了新的数据节点，即 newDataCount > 0 时，必然地 updated > 0
		if (updated) {
			// 更新数据节点的计数器；
			// pathNode.dataCount += newDataCount;

			// 计算节点哈希：
			pathNode.rehash();

			updatedNodes.add(pathNode);
		}

		// return newDataCount;
		return 0;
	}

	/**
	 * 加载或创建能够到达指定序号的数据节点的完整路径节点，并返回路径最末端的路径节点；
	 * 
	 * @param sn
	 * @return
	 */
	private PathNode extendPaths(long sn) {
		if (root.getLevel() < 0) {
			throw new IllegalStateException("The level of root is negative!");
		}
		if (root.getLevel() > 15) {
			// 超过15会导致接下来的幂运算溢出；
			throw new IllegalStateException("The level of root is out of range, and it will lead to a overflow!");
		}
		if (root.startingSN != 0) {
			// 根节点的起始序号应该为 0 ；
			throw new IllegalStateException("The start sn of root is not zero!");
		}

		// 如果指定的序号超出当前根节点的范围，需要向上扩展根节点；
		// 逻辑最大序号(logicMaxSN)满足:logicMaxSN=rootNode.startSN + rootNode.interval;
		// 由于rootNode.startSN==0，所以简化为:logicMaxSN=rootNode.interval;
		long logicMaxSN = -1;
		PathNode rootNode = root;
		while ((logicMaxSN = rootNode.interval) <= sn) {
			if (rootNode.getLevel() == 15) {
				throw new IllegalStateException(
						"The specified starting sn exceed the upper limit[" + logicMaxSN + "]!");
			}
			PathNode newRoot = new PathNode(setting.getHashAlgorithm(), rootNode.startingSN,
					(byte) (rootNode.getLevel() + 1), rootNode.getDataCount());
			newRoot.attachChildNode(rootNode, 0);
			rootNode = newRoot;
		}
		this.root = rootNode;

		// 加载或创建从根节点到目标序号
		PathNode leafPathNode = rootNode;
		while (leafPathNode.level > 1) {
			int index = leafPathNode.index(sn);
			if (leafPathNode.children[index] == null) {
				if (leafPathNode.childrenHashes[index] == null) {
					// 创建新节点；
					leafPathNode.newEmptyChild(setting.getHashAlgorithm(), index);
				} else {
					// 加载节点；
					PathNode node = loadPathNode(leafPathNode.childrenHashes[index], setting.getAutoVerifyHash());
					if (node == null) {
						throw new IllegalStateException("The merkle path node["
								+ leafPathNode.childrenHashes[index].toBase58() + "] not exist!");
					}
					leafPathNode.attachChildNode(node, index);
				}
			}
			leafPathNode = (PathNode) leafPathNode.children[index];
		}

		return leafPathNode;
	}

	/**
	 * 查找指定序号的数据节点，并记录该数据节点的完整路径；
	 * 
	 * <br>
	 * 路径由节点数组表示，首个元素是根节点，最后一个元素是数据节点；
	 * <p>
	 * 
	 * 如果 sn 超出范围，则引发 {@link IndexOutOfBoundsException} ；
	 * 
	 * @param sn   数据节点的序列号；
	 * @param path 用于记录节点路径的列表，长度必须大于等于当前默克尔树的总的层级（即 path.length 大于等于 root.level +
	 *             1）；<br>
	 *             如果参数为 null，则不记录；
	 * @return 序列号对应的数据节点；<br>
	 *         如果不存在，则返回 null，注意，此时指定的路径参数 path 依然写入了查找过程的路径；
	 */
	private MerkleDataNode seekPath(long sn, MerkleNode[] path) {
		/*
		 * 如果指定的序号超出当前根节点的范围，需要向上扩展根节点；
		 * 
		 * 逻辑最大序号(logicMaxSN)满足:logicMaxSN=rootNode.startSN + rootNode.interval;
		 * 由于rootNode.startSN==0，所以简化为:logicMaxSN=rootNode.interval;
		 */
		long logicMaxSN = root.interval;
		if (sn < 0 || sn >= logicMaxSN) {
			throw new IndexOutOfBoundsException("The specified sn is out of range!");
		}

		if (path != null && path.length < (root.level + 1)) {
			throw new IllegalArgumentException("The path length is too short to contain all path nodes!");
		}
		// 加载或创建从根节点到目标序号
		// List<AbstractMerkleNode> path = new ArrayList<>();
		// MerkleNode[] path = new MerkleNode[root.level + 1];
		// path.add(leafPathNode);
		PathNode leafPathNode = root;
		if (path != null) {
			path[path.length - leafPathNode.level - 1] = leafPathNode;
		}

		while (leafPathNode.level > 1) {
			int index = leafPathNode.index(sn);
			if (leafPathNode.children[index] == null) {
				if (leafPathNode.childrenHashes[index] == null) {
					// 节点不存在；
					return null;
				} else {
					// 加载节点；
					PathNode node = loadPathNode(leafPathNode.childrenHashes[index], setting.getAutoVerifyHash());
					if (node == null) {
						return null;
					}
					leafPathNode.attachChildNode(node, index);
				}
			}
			leafPathNode = (PathNode) leafPathNode.children[index];
			if (path != null) {
				path[path.length - leafPathNode.level - 1] = leafPathNode;
			}
		}

		// 数据节点；
		int index = leafPathNode.index(sn);
		if (leafPathNode.children[index] == null) {
			if (leafPathNode.childrenHashes[index] == null) {
				// 节点不存在；
				return null;
			} else {
				// 加载节点；
				DataNode dataNode = loadDataNode(leafPathNode.childrenHashes[index], setting.getAutoVerifyHash());
				if (dataNode == null) {
					return null;
				}
				leafPathNode.setData(sn, dataNode);
			}
		}
		if (path != null) {
			path[path.length - leafPathNode.children[index].getLevel() - 1] = leafPathNode.children[index];
		}
		return (MerkleDataNode) leafPathNode.children[index];
	}

	private Bytes encodeNodeKey(HashDigest hashBytes) {
		// return keyPrefix + hashBytes.toBase58();
		return new Bytes(keyPrefix, hashBytes.toBytes());
	}

	/**
	 * Load {@link AbstractMerkleNode} from storage service;
	 * 
	 * <p>
	 * 
	 * @param hashDigest
	 * @param verify
	 * @return return instance of {@link PathNode}, or null if not exist;
	 */
	private PathNode loadPathNode(HashDigest hashDigest, boolean verify) {
		Bytes key = encodeNodeKey(hashDigest);
		byte[] bytes = kvStorage.get(key);
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		PathNode pathNode = PathNode.parse(bytes, verify);
		if (verify && !hashDigest.equals(pathNode.getNodeHash())) {
			String keyStr = hashDigest.toBase58();
			String actualHashStr = pathNode.getNodeHash().toBase58();
			throw new MerkleProofException(String.format(
					"The actually hash of PathNode is not equal with it's key! -- [Key=%s][ActualHash=%s]", keyStr,
					actualHashStr));
		}
		return pathNode;
	}

	private DataNode loadDataNode(HashDigest hashBytes, boolean verify) {
		Bytes key = encodeNodeKey(hashBytes);
		byte[] bytes = kvStorage.get(key);
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		DataNode dataNode = MerkleTreeEncoder.resolve(bytes);
		if (verify && !hashBytes.equals(dataNode.nodeHash)) {
			String keyStr = hashBytes.toBase58();
			String actualHashStr = dataNode.nodeHash.toBase58();
			throw new MerkleProofException(String.format(
					"The actually hash of DataNode is not equal with it's key! -- [Key=%s][ActualHash=%s]", keyStr,
					actualHashStr));
		}
		return dataNode;
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

	// =================================================

	/**
	 * 数据证明；
	 * 
	 * <p>
	 * 数据证明是由从 Merkle Tree 的根节点出发到目标数据节点的经过的全部节点构成的一条路径；
	 * <p>
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class MerkleProofImpl implements MerkleProof {

		/**
		 * 从根节点到数据节点的路径；<br>
		 * 路径的首个元素是根节点，最后一个原始是数据节点；
		 */
		private MerkleNode[] path;

		private long sn;

		private MerkleProofImpl(long sn, MerkleNode[] path) {
			this.sn = sn;
			this.path = path;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.MerkleProof#getSN()
		 */
		@Override
		public long getSN() {
			return sn;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.MerkleProof#getLevel()
		 */
		@Override
		public int getLevels() {
			return path[0].getLevel();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.MerkleProof#getHash(int)
		 */
		@Override
		public HashDigest getHash(int level) {
			return path[path.length - 1 - level].getNodeHash();
		}

		@Override
		public MerkleNode getNode(int level) {
			return path[path.length - 1 - level];
		}

		// @Override
		// public long getStartingSN(int level) {
		// return path[path.length - 1 - level].startingSN;
		// }
		//
		// @Override
		// public long getDataCount(int level) {
		// return path[path.length - 1 - level].dataCount;
		// }

		@Override
		public String toString() {
			StringBuilder strPath = new StringBuilder(NODE_PREFIX);
			for (int i = 0; i < path.length; i++) {
				if (i > 0) {
					strPath.append(PATH_SEPERATOR);
				}
				strPath.append(path[i].getNodeHash().toBase58());
			}
			return strPath.toString();
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(path);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj instanceof MerkleProofImpl) {
				MerkleProofImpl proof1 = (MerkleProofImpl) obj;
				if (path.length != proof1.path.length) {
					return false;
				}
				for (int i = 0; i < path.length; i++) {
					if (!path[i].equals(proof1.path[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

	}

	private static class ProofNodeEntry implements MerkleNode {

		private int level;

		@SuppressWarnings("unused")
		private long dataCount;

		@SuppressWarnings("unused")
		private long startingSN;

		private HashDigest nodeHash;

		@Override
		public HashDigest getNodeHash() {
			return nodeHash;
		}

		@Override
		public int getLevel() {
			return level;
		}

		@Override
		public int hashCode() {
			return nodeHash.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj instanceof ProofNodeEntry) {
				ProofNodeEntry node1 = (ProofNodeEntry) obj;
				return this.nodeHash.equals(node1.nodeHash);
			}
			return false;
		}
	}

	/**
	 * Abstract node of merkle tree;
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static abstract class AbstractMerkleNode implements BytesSerializable, MerkleNode {

		protected HashDigest nodeHash;

		protected AbstractMerkleNode parent;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.core.MerkleNode#getNodeHash()
		 */
		@Override
		public HashDigest getNodeHash() {
			return nodeHash;
		}

		/**
		 * 直接或间接从属在该节点下的数据节点的起始序号；
		 * 
		 * @return
		 */
		protected abstract long getStartingSN();

		/**
		 * 直接或间接从属在该节点下的数据节点的数量；
		 * 
		 * @return
		 */
		protected abstract long getDataCount();

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.core.MerkleNode#getLevel()
		 */
		@Override
		public abstract int getLevel();

	}

	/**
	 * 路径节点；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class PathNode extends AbstractMerkleNode {

		/**
		 * 当前节点采用的 hash 算法；
		 */
		private short hashAlgorithm;

		/**
		 * 节点的起始序列号；
		 */
		public final long startingSN;

		/**
		 * 节点在 MerkleTree 中的层级；
		 */
		public final int level;

		/**
		 * 当前节点的序列号区间大小；
		 */
		public final long interval;

		/**
		 * 当前节点的直接子节点的序列号区间大小；
		 */
		public final long subinterval;

		/**
		 * nodeHash = Hash(startSn + childCount + childNodeHashes);
		 */
		// private HashDigest nodeHash;

		public HashDigest[] childrenHashes;

		public AbstractMerkleNode[] children;

		// private long dataCount;
		private AtomicLong dataCount;

		/**
		 * 增加数据节点计数器；<br>
		 * 操作将级联更新到根节点；
		 * 
		 * @param newCount
		 */
		public void increaseDataCount(int newCount) {
			dataCount.addAndGet(newCount);
			if (parent != null) {
				((PathNode) parent).increaseDataCount(newCount);
			}
		}

		/**
		 * 创建一个路径节点；
		 * 
		 * @param hashAlgorithm 生成节点采用的哈希算法；
		 * @param startingSN    路径节点表示的子树的起始序列号；
		 * @param level         路径节点的层级深度；路径节点的深度从 1 开始往上递增（数据节点作为树的深度为 0）；
		 * @param dataCount     路径节点表示的子树所包含的数据节点的数量；
		 */
		private PathNode(CryptoAlgorithm hashAlgorithm, long startingSN, int level, long dataCount) {
			this(hashAlgorithm, startingSN, level, dataCount, new HashDigest[TREE_DEGREE], null);
		}

		private PathNode(short hashAlgorithm, long startingSN, int level, long dataCount) {
			this(hashAlgorithm, startingSN, level, dataCount, new HashDigest[TREE_DEGREE], null);
		}

		private PathNode(long startingSN, int level, long dataCount, HashDigest[] childrenHashes, HashDigest nodeHash) {
			this(nodeHash.getAlgorithm(), startingSN, level, dataCount, childrenHashes, nodeHash);
		}

		private PathNode(CryptoAlgorithm defaultHashAlgorithm, long startingSN, int level, long dataCount,
				HashDigest[] childrenHashes, HashDigest nodeHash) {
			this(defaultHashAlgorithm.code(), startingSN, level, dataCount, childrenHashes, nodeHash);
		}

		private PathNode(short hashAlgorithm, long startingSN, int level, long dataCount, HashDigest[] childrenHashes,
				HashDigest nodeHash) {
			if (startingSN < 0) {
				throw new IllegalArgumentException("The specified starting sn of PathNode is negative!");
			}
			if (level < 1) {
				throw new IllegalArgumentException("The specified level of PathNode is less than 1!");
			}
			if (dataCount < 0) {
				throw new IllegalArgumentException("The specified data count of PathNode is negative!");
			}
			this.hashAlgorithm = hashAlgorithm;
			this.startingSN = startingSN;
			this.level = level;

			this.interval = computeInterval();
			this.subinterval = computeSubinterval();

			if (dataCount > this.interval) {
				throw new IllegalArgumentException("The specified data count of PathNode exceed the upper limit!");
			}
			this.dataCount = new AtomicLong(dataCount);

			this.children = new AbstractMerkleNode[TREE_DEGREE];

			this.childrenHashes = childrenHashes;
			this.nodeHash = nodeHash;
		}

		public void attachChildNode(PathNode node, int index) {
			if (node.level != this.level - 1) {
				throw new IllegalArgumentException("The level of the attaching child node is illegal!");
			}
			long expectedStartingSN = startingSN + subinterval * index;
			if (expectedStartingSN != node.startingSN) {
				throw new IllegalArgumentException("The starting sn of the attaching child node is illegal!");
			}
			children[index] = node;
			node.parent = this;
		}

		public void attachChildNode(DataNode dataNode, int index) {
			if (level != 1) {
				throw new IllegalStateException("Cann't set data by the PathNode witch's isn't the leaf path!");
			}
			children[index] = dataNode;
			dataNode.parent = this;
		}

		private void setData(long sn, DataNode dataNode) {
			assert sn >= startingSN
					&& sn < startingSN + interval : "The specified sn of the DataNode exceed the upper limit!";

			int index = (int) (sn - startingSN);
			attachChildNode(dataNode, index);
		}

		/**
		 * 计算当前节点的序列号区间大小；
		 * 
		 * @return
		 */
		private long computeInterval() {
			return power(TREE_DEGREE, level);
		}

		/**
		 * 计算当前节点的直接子节点的序列号区间大小；
		 * 
		 * @return
		 */
		private long computeSubinterval() {
			return power(TREE_DEGREE, level - 1);
		}

		/**
		 * 定位指定序号在此路径的子节点列表中的下标索引；
		 * 
		 * @param sn
		 * @return
		 */
		private int index(long sn) {
			long s = subinterval;
			long offset = sn - startingSN;
			return (int) ((offset - offset % s) / s);
		}

		@SuppressWarnings("unused")
		private PathNode newEmptyChild(CryptoAlgorithm hashAlgorithm, int index) {
			return newEmptyChild(hashAlgorithm.code(), index);
		}

		private PathNode newEmptyChild(short hashAlgorithm, int index) {
			long newStartingSN = startingSN + subinterval * index;
			PathNode child = new PathNode(hashAlgorithm, newStartingSN, (byte) (level - 1), 0);
			attachChildNode(child, index);
			return child;
		}

		public long getStartingSN() {
			return startingSN;
		}

		@Override
		public long getDataCount() {
			return dataCount.get();
		}

		@Override
		public int getLevel() {
			return level;
		}

		/**
		 * 序列化当前节点的属性；包括节点哈希、数据节点计数器、子节点哈希列表；
		 */
		@Override
		public byte[] toBytes() {
			int bodySize = getBodySize();
			int hashSize = nodeHash.size();
			int totalSize = bodySize + NumberMask.TINY.MAX_HEADER_LENGTH + hashSize;
			byte[] totalBytes = new byte[totalSize];
			int offset = generateBodyBytes(totalBytes);

			offset += NumberMask.TINY.writeMask(hashSize, totalBytes, offset);
			System.arraycopy(nodeHash.toBytes(), 0, totalBytes, offset, hashSize);
			offset += hashSize;
			return totalBytes;
		}

		private int getBodySize() {
			int totalSize = 8 + 4 + 8;// startingSN + level + dataCount;
			HashDigest h;
			for (int i = 0; i < TREE_DEGREE; i++) {
				h = childrenHashes[i];
				totalSize += NumberMask.TINY.getMaskLength(h == null ? 0 : h.size());
				if (h != null) {
					totalSize += h.size();
				}
			}
			return totalSize;
		}

		private int generateBodyBytes(byte[] bodyBytes) {
			int offset = 0;
			offset += BytesUtils.toBytes(startingSN, bodyBytes, offset);

			offset += BytesUtils.toBytes(level, bodyBytes, offset);

			offset += BytesUtils.toBytes(getDataCount(), bodyBytes, offset);

			HashDigest h;
			for (int i = 0; i < TREE_DEGREE; i++) {
				h = childrenHashes[i];
				if (h == null) {
					// 只写入一个字节的长度头部，值为 0；
					bodyBytes[offset] = 0;
					offset++;
				} else {
					int len = h.size();
					offset += NumberMask.TINY.writeMask(len, bodyBytes, offset);
					System.arraycopy(h.toBytes(), 0, bodyBytes, offset, len);
					offset += len;
				}
			}

			return offset;
		}

		/**
		 * 从指定的字节数组反序列化节点；
		 * 
		 * @param bytes     字节数组；合法的输入应等同于 {@link #toBytes()} 方法的输出；
		 * @param checkHash 是否重新计算并校验节点的哈希；
		 * @return
		 */
		private static PathNode parse(byte[] bytes, boolean checkHash) {
			int offset = 0;

			long startingSN = BytesUtils.toLong(bytes, offset);
			offset += 8;

			int level = BytesUtils.toInt(bytes, offset);
			offset += 4;

			long dataCount = BytesUtils.toLong(bytes, offset);
			offset += 8;

			HashDigest[] childrenHashes = new HashDigest[TREE_DEGREE];
			byte[] h;
			for (int i = 0; i < TREE_DEGREE; i++) {
				int hashSize = NumberMask.TINY.resolveMaskedNumber(bytes, offset);
				offset += NumberMask.TINY.getMaskLength(hashSize);

				if (hashSize == 0) {
					continue;
				}
				h = new byte[hashSize];
				System.arraycopy(bytes, offset, h, 0, hashSize);
				offset += hashSize;
				childrenHashes[i] = new HashDigest(h);
			}

			int hashSize = NumberMask.TINY.resolveMaskedNumber(bytes, offset);
			offset += NumberMask.TINY.getMaskLength(hashSize);

			byte[] nodeHashBytes = new byte[hashSize];
			System.arraycopy(bytes, offset, nodeHashBytes, 0, hashSize);
			offset += hashSize;

			HashDigest nodeHash = new HashDigest(nodeHashBytes);

			PathNode node = new PathNode(startingSN, level, dataCount, childrenHashes, nodeHash);
			if (checkHash) {
				HashDigest actualHash = node.computeBodyHash();
				if (!node.nodeHash.equals(actualHash)) {
					String origHashStr = node.nodeHash.toBase58();
					String actualHashStr = actualHash.toBase58();
					throw new MerkleProofException(String.format(
							"The actually hash of PathNode is not equal with it's original hash! -- [OrigHash=%s][ActualHash=%s]",
							origHashStr, actualHashStr));
				}
			}

			return node;
		}

		/**
		 * 重新计算并更新当前节点的哈希；
		 */
		public void rehash() {
			this.nodeHash = computeBodyHash();
		}

		/**
		 * 计算节点的 hash，但不会更新 {@link #getNodeHash()} 属性；
		 * <p>
		 * 
		 * 节点哈希： <code>
		 * nodeHash = Hash(toBytes(startingSN) + toBytes(level) + toBytes(dataCount) + h1 + h2 + ... + h16);
		 * </code>
		 * 
		 * @param pathNode
		 * @return
		 */
		private HashDigest computeBodyHash() {
			int totalSize = getBodySize();
			byte[] bodyBytes = new byte[totalSize];
			generateBodyBytes(bodyBytes);
			HashFunction hashFunc = Crypto.getHashFunction(hashAlgorithm);
			return hashFunc.hash(bodyBytes);
		}

	}

	/**
	 * 数据节点；
	 * 
	 * @author huanghaiquan
	 *
	 */
	static class DataNode extends AbstractMerkleNode implements MerkleDataNode {

		private long sn;

		private Bytes key;

		private long version;

		private byte[] nodeBytes;

		private HashDigest valueHash;

		DataNode(HashDigest nodeHash, long sn, Bytes key, long version, HashDigest valueHash, byte[] nodeBytes) {
			this.sn = sn;
			this.key = key;
			this.version = version;
			this.nodeHash = nodeHash;
			this.valueHash = valueHash;
			this.nodeBytes = nodeBytes;
		}

		@Override
		protected long getStartingSN() {
			return sn;
		}

		@Override
		protected long getDataCount() {
			return 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.core.MerkleDataNode#getLevel()
		 */
		@Override
		public int getLevel() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.core.MerkleDataNode#getSN()
		 */
		@Override
		public long getSN() {
			return sn;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.core.MerkleDataNode#getKey()
		 */
		@Override
		public Bytes getKey() {
			return key;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jd.blockchain.ledger.core.MerkleDataNode#getVersion()
		 */
		@Override
		public long getVersion() {
			return version;
		}

		@Override
		public HashDigest getValueHash() {
			return valueHash;
		}

		@Override
		public byte[] toBytes() {
			// ByteArrayOutputStream out = new ByteArrayOutputStream();
			//
			// BytesUtils.writeLong(sn, out);
			//
			// byte[] keyBytes = BytesUtils.toBytes(key);
			// BytesEncoding.write(keyBytes, NumberMask.SHORT, out);
			//
			// BytesUtils.writeLong(version, out);

			// int hashSize = nodeHash.size();
			//
			// int totalSize = dataNodeBytes.length + NumberMask.TINY.MAX_HEADER_LENGTH +
			// hashSize;
			// byte[] totalBytes = new byte[totalSize];
			//
			// int offset = 0;
			// System.arraycopy(dataNodeBytes, 0, totalBytes, offset, dataNodeBytes.length);
			// offset += dataNodeBytes.length;

			// BytesEncoding.write(nodeHash.toBytes(), NumberMask.SHORT, out);
			// NumberMask.TINY.writeMask(hashSize, totalBytes, offset);
			// offset += NumberMask.TINY.MAX_HEADER_LENGTH;
			//
			// System.arraycopy(nodeHash.toBytes(), 0, totalBytes, offset, hashSize);

			return nodeBytes;
		}

		@Override
		public int hashCode() {
			return nodeHash.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj instanceof DataNode) {
				DataNode node1 = (DataNode) obj;
				return this.nodeHash.equals(node1.nodeHash);
			}
			return false;
		}
	}

}
