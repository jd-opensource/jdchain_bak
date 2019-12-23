package com.jd.blockchain.ledger.core;

import java.util.HashMap;
import java.util.Map;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.ledger.MerkleSnapshot;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;
import com.jd.blockchain.utils.Transactional;

public class MerkleAccountSet implements Transactional, MerkleProvable, AccountQuery<CompositeAccount> {

	static {
		DataContractRegistry.register(MerkleSnapshot.class);
		DataContractRegistry.register(BlockchainIdentity.class);
	}

	private final Bytes keyPrefix;

	/**
	 * 账户根哈希的数据集；
	 */
	private MerkleDataSet merkleDataset;

	/**
	 * The cache of latest version accounts, including accounts getting by querying
	 * and by new regiestering ;
	 * 
	 */
	// TODO:未考虑大数据量时，由于缺少过期策略，会导致内存溢出的问题；
	private Map<Bytes, InnerMerkleAccount> latestAccountsCache = new HashMap<>();

	private ExPolicyKVStorage baseExStorage;

	private VersioningKVStorage baseVerStorage;

	private CryptoSetting cryptoSetting;

	private volatile boolean updated;

	private AccountAccessPolicy accessPolicy;

	public boolean isReadonly() {
		return merkleDataset.isReadonly();
	}

	void setReadonly() {
		merkleDataset.setReadonly();
	}

	public MerkleAccountSet(CryptoSetting cryptoSetting, Bytes keyPrefix, ExPolicyKVStorage exStorage,
			VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy) {
		this(null, cryptoSetting, keyPrefix, exStorage, verStorage, false, accessPolicy);
	}

	public MerkleAccountSet(HashDigest rootHash, CryptoSetting cryptoSetting, Bytes keyPrefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
			AccountAccessPolicy accessPolicy) {
		this.keyPrefix = keyPrefix;
		this.cryptoSetting = cryptoSetting;
		this.baseExStorage = exStorage;
		this.baseVerStorage = verStorage;
		this.merkleDataset = new MerkleDataSet(rootHash, cryptoSetting, keyPrefix, this.baseExStorage,
				this.baseVerStorage, readonly);

		this.accessPolicy = accessPolicy;
	}

	@Override
	public HashDigest getRootHash() {
		return merkleDataset.getRootHash();
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		return merkleDataset.getProof(key);
	}

	@Override
	public BlockchainIdentity[] getHeaders(int fromIndex, int count) {
		DataEntry<Bytes, byte[]>[] results = merkleDataset.getLatestDataEntries(fromIndex, count);

		BlockchainIdentity[] ids = new BlockchainIdentity[results.length];
		for (int i = 0; i < results.length; i++) {
			InnerMerkleAccount account = createAccount(results[i].getKey(), new HashDigest(results[i].getValue()),
					results[i].getVersion(), true);
			ids[i] = account.getID();
		}
		return ids;
	}

	/**
	 * 返回账户的总数量；
	 * 
	 * @return
	 */
	public long getTotal() {
		return merkleDataset.getDataCount();
	}

	@Override
	public CompositeAccount getAccount(String address) {
		return getAccount(Bytes.fromBase58(address));
	}

	/**
	 * 返回最新版本的 Account;
	 * 
	 * @param address
	 * @return
	 */
	@Override
	public CompositeAccount getAccount(Bytes address) {
		return this.getAccount(address, -1);
	}

	/**
	 * 账户是否存在；<br>
	 * 
	 * 如果指定的账户已经注册（通过 {@link #register(String, PubKey)} 方法），但尚未提交（通过
	 * {@link #commit()} 方法），此方法对该账户仍然返回 false；
	 * 
	 * @param address
	 * @return
	 */
	public boolean contains(Bytes address) {
		InnerMerkleAccount acc = latestAccountsCache.get(address);
		if (acc != null) {
			// 无论是新注册未提交的，还是缓存已提交的账户实例，都认为是存在；
			return true;
		}
		long latestVersion = merkleDataset.getVersion(address);
		return latestVersion > -1;
	}

	/**
	 * 返回指定账户的版本； <br>
	 * 如果账户已经注册，则返回该账户的最新版本，值大于等于 0； <br>
	 * 如果账户不存在，则返回 -1；<br>
	 * 如果账户已经注册（通过 {@link #register(String, PubKey)} 方法），但尚未提交（通过 {@link #commit()}
	 * 方法），则返回 -1； <br>
	 * 
	 * @param address
	 * @return
	 */
	public long getVersion(Bytes address) {
		InnerMerkleAccount acc = latestAccountsCache.get(address);
		if (acc != null) {
			// 已注册尚未提交，也返回 -1;
			return acc.getVersion();
		}

		return merkleDataset.getVersion(address);
	}

	/**
	 * 返回指定版本的 Account；
	 * 
	 * 只有最新版本的账户才能可写的，其它都是只读；
	 * 
	 * @param address 账户地址；
	 * @param version 账户版本；如果指定为 -1，则返回最新版本；
	 * @return
	 */
	public CompositeAccount getAccount(Bytes address, long version) {
		version = version < 0 ? -1 : version;
		InnerMerkleAccount acc = latestAccountsCache.get(address);
		if (acc != null && version == -1) {
			return acc;
		} else if (acc != null && acc.getVersion() == version) {
			return acc;
		}

		long latestVersion = merkleDataset.getVersion(address);
		if (latestVersion < 0) {
			// Not exist;
			return null;
		}
		if (version > latestVersion) {
			return null;
		}

		// 如果是不存在的，或者刚刚新增未提交的账户，则前面一步查询到的 latestVersion 小于 0， 代码不会执行到此；
		if (acc != null && acc.getVersion() != latestVersion) {
			// 当执行到此处时，并且缓冲列表中缓存了最新的版本，
			// 如果当前缓存的最新账户的版本和刚刚从存储中检索得到的最新版本不一致，可能存在外部的并发更新，这超出了系统设计的逻辑；

			// TODO:如果是今后扩展至集群方案时，这种不一致的原因可能是由其它集群节点实例执行了更新，这种情况下，最好是放弃旧缓存，并重新加载和缓存最新版本；
			// by huanghaiquan at 2018-9-2 23:03:00;
			throw new IllegalStateException("The latest version in cache is not equals the latest version in storage! "
					+ "Mybe some asynchronzing updating are performed out of current server.");
		}

		// Now, be sure that "acc == null", so get account from storage;
		// Set readonly for the old version account;
		boolean readonly = (version > -1 && version < latestVersion) || isReadonly();
		
		long qVersion = version == -1 ? latestVersion : version;
		// load account from storage;
		acc = loadAccount(address, readonly, qVersion);
		if (acc == null) {
			return null;
		}
		if (!readonly) {
			// cache the latest version witch enable reading and writing;
			// readonly version of account not necessary to be cached;
			latestAccountsCache.put(address, acc);
		}
		return acc;
	}

	public CompositeAccount register(Bytes address, PubKey pubKey) {
		return register(new BlockchainIdentityData(address, pubKey));
	}

	/**
	 * 注册一个新账户； <br>
	 * 
	 * 如果账户已经存在，则会引发 {@link LedgerException} 异常； <br>
	 * 
	 * 如果指定的地址和公钥不匹配，则会引发 {@link LedgerException} 异常；
	 * 
	 * @param address 区块链地址；
	 * @param pubKey  公钥；
	 * @return 注册成功的账户对象；
	 */
	public CompositeAccount register(BlockchainIdentity accountId) {
		if (isReadonly()) {
			throw new IllegalArgumentException("This AccountSet is readonly!");
		}

		Bytes address = accountId.getAddress();
		PubKey pubKey = accountId.getPubKey();
		verifyAddressEncoding(address, pubKey);

		InnerMerkleAccount cachedAcc = latestAccountsCache.get(address);
		if (cachedAcc != null) {
			if (cachedAcc.getVersion() < 0) {
				// 同一个新账户已经注册，但尚未提交，所以重复注册不会引起任何变化；
				return cachedAcc;
			}
			// 相同的账户已经存在；
			throw new LedgerException("The registering account already exist!");
		}
		long version = merkleDataset.getVersion(address);
		if (version >= 0) {
			throw new LedgerException("The registering account already exist!");
		}

		if (!accessPolicy.checkRegistering(address, pubKey)) {
			throw new LedgerException("Account Registering was rejected for the access policy!");
		}

		Bytes prefix = keyPrefix.concat(address);
		InnerMerkleAccount acc = createInstance(accountId, cryptoSetting, prefix);
		latestAccountsCache.put(address, acc);
		updated = true;

		return acc;
	}

	private void verifyAddressEncoding(Bytes address, PubKey pubKey) {
		Bytes chAddress = AddressEncoding.generateAddress(pubKey);
		if (!chAddress.equals(address)) {
			throw new LedgerException("The registering Address mismatch the specified PubKey!");
		}
	}

	private InnerMerkleAccount createInstance(BlockchainIdentity header, CryptoSetting cryptoSetting, Bytes keyPrefix) {
		return new InnerMerkleAccount(header, cryptoSetting, keyPrefix, baseExStorage, baseVerStorage);
	}

	/**
	 * 加载指定版本的账户；
	 * 
	 * @param address  账户地址；
	 * @param readonly 是否只读；
	 * @param version  账户的版本；大于等于 0 ；
	 * @return
	 */
	private InnerMerkleAccount loadAccount(Bytes address, boolean readonly, long version) {
		byte[] rootHashBytes = merkleDataset.getValue(address, version);
		if (rootHashBytes == null) {
			return null;
		}
		HashDigest rootHash = new HashDigest(rootHashBytes);

		return createAccount(address, rootHash, version, readonly);
	}

	private InnerMerkleAccount createAccount(Bytes address, HashDigest rootHash, long version, boolean readonly) {
		// prefix;
		Bytes prefix = keyPrefix.concat(address);

		return new InnerMerkleAccount(address, version, rootHash, cryptoSetting, prefix, baseExStorage, baseVerStorage,
				readonly);
	}

	// TODO:优化：区块链身份(地址+公钥)与其Merkle树根哈希分开独立存储；
	// 不必作为一个整块，避免状态数据写入时频繁重写公钥，尤其某些算法的公钥可能很大；

	/**
	 * 保存账户的根哈希，返回账户的新版本；
	 * 
	 * @param account
	 * @return
	 */
	private long saveAccount(InnerMerkleAccount account) {
		// 提交更改，更新哈希；
		account.commit();

		return account.getVersion();
	}

	@Override
	public boolean isUpdated() {
		return updated;
	}

	@Override
	public void commit() {
		if (!updated) {
			return;
		}
		try {
			for (InnerMerkleAccount acc : latestAccountsCache.values()) {
				// updated or new created;
				if (acc.isUpdated() || acc.getVersion() < 0) {
					saveAccount(acc);
				}
			}
			merkleDataset.commit();
		} finally {
			updated = false;
			latestAccountsCache.clear();
		}
	}

	@Override
	public void cancel() {
		if (!updated) {
			return;
		}
		Bytes[] addresses = new Bytes[latestAccountsCache.size()];
		latestAccountsCache.keySet().toArray(addresses);
		for (Bytes address : addresses) {
			InnerMerkleAccount acc = latestAccountsCache.remove(address);
			// cancel;
			if (acc.isUpdated()) {
				acc.cancel();
			}
		}
		updated = false;
	}

	/**
	 * 内部实现的账户，监听和同步账户数据的变更；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private class InnerMerkleAccount extends MerkleAccount {

		private long version;

		public InnerMerkleAccount(BlockchainIdentity accountID, CryptoSetting cryptoSetting, Bytes keyPrefix,
				ExPolicyKVStorage exStorage, VersioningKVStorage verStorage) {
			super(accountID, cryptoSetting, keyPrefix, exStorage, verStorage);
			this.version = -1;
		}

		public InnerMerkleAccount(Bytes address, long version, HashDigest dataRootHash, CryptoSetting cryptoSetting,
				Bytes keyPrefix, ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly) {
			super(address, dataRootHash, cryptoSetting, keyPrefix, exStorage, verStorage, readonly);
			this.version = version;
		}

		@Override
		protected void onUpdated(String key, TypedValue value, long expectedVersion, long newVersion) {
			updated = true;
		}

		@Override
		protected void onCommited(HashDigest previousRootHash, HashDigest newRootHash) {
			long newVersion = merkleDataset.setValue(this.getAddress(), newRootHash.toBytes(), version);
			if (newVersion < 0) {
				// Update fail;
				throw new LedgerException("Account updating fail! --[Address=" + this.getAddress() + "]");
			}
			this.version = newVersion;
		}

		public long getVersion() {
			return version;
		}

	}

}