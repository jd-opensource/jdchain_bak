package com.jd.blockchain.ledger.core;

import java.util.HashMap;
import java.util.Map;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

public class AccountSet implements Transactional, MerkleProvable {

	static {
		DataContractRegistry.register(AccountHeader.class);
	}

	private final String keyPrefix;

	private MerkleDataSet merkleDataset;

	/**
	 * The cache of latest version accounts, including accounts getting by querying
	 * and by new regiestering ;
	 * 
	 */
	// TODO:未考虑大数据量时，由于缺少过期策略，会导致内存溢出的问题；
	private Map<Bytes, VersioningAccount> latestAccountsCache = new HashMap<>();

	private ExPolicyKVStorage baseExStorage;

	private VersioningKVStorage baseVerStorage;

	private CryptoSetting cryptoSetting;

	private boolean updated;

	private AccountAccessPolicy accessPolicy;

	public boolean isReadonly() {
		return merkleDataset.isReadonly();
	}

	public AccountSet(CryptoSetting cryptoSetting, String keyPrefix, ExPolicyKVStorage exStorage,
			VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy) {
		this(null, cryptoSetting, keyPrefix, exStorage, verStorage, false, accessPolicy);
	}

	public AccountSet(HashDigest rootHash, CryptoSetting cryptoSetting, String keyPrefix, ExPolicyKVStorage exStorage,
			VersioningKVStorage verStorage, boolean readonly, AccountAccessPolicy accessPolicy) {
		this.keyPrefix = keyPrefix;
		this.cryptoSetting = cryptoSetting;
		this.baseExStorage = exStorage;
		this.baseVerStorage = verStorage;
		this.merkleDataset = new MerkleDataSet(rootHash, cryptoSetting, keyPrefix, this.baseExStorage,
				this.baseVerStorage, readonly);
		this.accessPolicy = accessPolicy;
	}

	// public HashDigest getAccountRootHash() {
	// return merkleDataset.getRootHash();
	// }

	@Override
	public HashDigest getRootHash() {
		return merkleDataset.getRootHash();
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		return merkleDataset.getProof(key);
	}

	public AccountHeader[] getAccounts(int fromIndex, int count) {
		byte[][] results = merkleDataset.getLatestValues(fromIndex, count);
		AccountHeader[] accounts = new AccountHeader[results.length];

		for (int i = 0; i < results.length; i++) {
			accounts[i] = deserialize(results[i]);
		}
		return accounts;
	}

	// private VersioningAccount deserialize(byte[] txBytes) {
	//// return BinaryEncodingUtils.decode(txBytes, null, Account.class);
	// AccountHeaderData accInfo = BinaryEncodingUtils.decode(txBytes);
	//// return new BaseAccount(accInfo.getAddress(), accInfo.getPubKey(), null,
	// cryptoSetting,
	//// baseExStorage, baseVerStorage, true, accessPolicy);
	// return new VersioningAccount(accInfo.getAddress(), accInfo.getPubKey(),
	// accInfo.getRootHash(), cryptoSetting,
	// keyPrefix, baseExStorage, baseVerStorage, true, accessPolicy, accInfo.);
	// }

	private AccountHeader deserialize(byte[] txBytes) {
		return BinaryEncodingUtils.decode(txBytes);
	}

	/**
	 * 返回账户的总数量；
	 * 
	 * @return
	 */
	public long getTotalCount() {
		return merkleDataset.getDataCount();
	}

	/**
	 * 返回最新版本的 Account;
	 * 
	 * @param address
	 * @return
	 */
	public BaseAccount getAccount(Bytes address) {
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
		long latestVersion = getVersion(address);
		return latestVersion > -1;
	}

	/**
	 * 返回指定账户的版本； <br>
	 * 如果账户已经注册，则返回该账户的最新版本，值大于等于 0； <br>
	 * 如果账户不存在，则返回 -1； <br>
	 * 如果指定的账户已经注册（通过 {@link #register(String, PubKey)} 方法），但尚未提交（通过
	 * {@link #commit()} 方法），此方法对该账户仍然返回 0；
	 * 
	 * @param address
	 * @return
	 */
	public long getVersion(Bytes address) {
		VersioningAccount acc = latestAccountsCache.get(address);
		if (acc != null) {
			// 已注册尚未提交，也返回 -1;
			return acc.version == -1 ? 0 : acc.version;
		}

		return merkleDataset.getVersion(address);
	}

	/**
	 * 返回指定版本的 Account；
	 * 
	 * 只有最新版本的账户才能可写的，其它都是只读；
	 * 
	 * @param address
	 *            账户地址；
	 * @param version
	 *            账户版本；如果指定为 -1，则返回最新版本；
	 * @return
	 */
	public BaseAccount getAccount(Bytes address, long version) {
		version = version < 0 ? -1 : version;
		VersioningAccount acc = latestAccountsCache.get(address);
		if (acc != null && version == -1) {
			return acc;
		} else if (acc != null && acc.version == version) {
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
		if (acc != null && acc.version != latestVersion) {
			// 当执行到此处时，并且缓冲列表中缓存了最新的版本，
			// 如果当前缓存的最新账户的版本和刚刚从存储中检索得到的最新版本不一致，可能存在外部的并发更新，这超出了系统设计的逻辑；

			// TODO:如果是今后扩展至集群方案时，这种不一致的原因可能是由其它集群节点实例执行了更新，这种情况下，最好是放弃旧缓存，并重新加载和缓存最新版本；
			// by huanghaiquan at 2018-9-2 23:03:00;
			throw new IllegalStateException("The latest version in cache is not equals the latest version in storage! "
					+ "Mybe some asynchronzing updating are performed out of current server.");
		}

		// Now, be sure that "acc == null", so get account from storage;

		byte[] bytes = merkleDataset.getValue(address, version);
		if (bytes == null) {
			return null;
		}

		// Set readonly for the old version account;
		boolean readonly = (version > -1 && version < latestVersion) || isReadonly();

		// String prefix = address.concat(LedgerConsts.KEY_SEPERATOR);
		// ExPolicyKVStorage ss = PrefixAppender.prefix(prefix, baseExStorage);
		// VersioningKVStorage vs = PrefixAppender.prefix(prefix, baseVerStorage);
		// BaseAccount accDS = deserialize(bytes, cryptoSetting, ss, vs, readonly);
		String prefix = keyPrefix + address;
		acc = deserialize(bytes, cryptoSetting, prefix, baseExStorage, baseVerStorage, readonly, latestVersion);
		if (!readonly) {
			// cache the latest version witch enable reading and writing;
			// readonly version of account not necessary to be cached;
			latestAccountsCache.put(address, acc);
		}
		return acc;
	}

	/**
	 * 注册一个新账户； <br>
	 * 
	 * 如果账户已经存在，则会引发 {@link LedgerException} 异常； <br>
	 * 
	 * 如果指定的地址和公钥不匹配，则会引发 {@link LedgerException} 异常；
	 * 
	 * @param address
	 *            区块链地址；
	 * @param pubKey
	 *            公钥；
	 * @return 注册成功的账户对象；
	 */
	public BaseAccount register(Bytes address, PubKey pubKey) {
		if (isReadonly()) {
			throw new IllegalArgumentException("This AccountSet is readonly!");
		}

		verifyAddressEncoding(address, pubKey);

		VersioningAccount cachedAcc = latestAccountsCache.get(address);
		if (cachedAcc != null) {
			if (cachedAcc.version < 0) {
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

		// String prefix = address.concat(LedgerConsts.KEY_SEPERATOR);
		// ExPolicyKVStorage accExStorage = PrefixAppender.prefix(prefix,
		// baseExStorage);
		// VersioningKVStorage accVerStorage = PrefixAppender.prefix(prefix,
		// baseVerStorage);
		// BaseAccount accDS = createInstance(address, pubKey, cryptoSetting,
		// accExStorage, accVerStorage);

		String prefix = keyPrefix + address;
		VersioningAccount acc = createInstance(address, pubKey, cryptoSetting, prefix, baseExStorage, baseVerStorage,
				-1);
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

	private VersioningAccount createInstance(Bytes address, PubKey pubKey, CryptoSetting cryptoSetting,
			String keyPrefix, ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, long version) {
		return new VersioningAccount(address, pubKey, cryptoSetting, keyPrefix, exStorage, verStorage, accessPolicy,
				version);
	}

	private VersioningAccount deserialize(byte[] bytes, CryptoSetting cryptoSetting, String keyPrefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly, long version) {
		AccountHeader accInfo = BinaryEncodingUtils.decode(bytes);
		return new VersioningAccount(accInfo.getAddress(), accInfo.getPubKey(), accInfo.getRootHash(), cryptoSetting,
				keyPrefix, exStorage, verStorage, readonly, accessPolicy, version);
	}

	private byte[] serialize(AccountHeader account) {
		return BinaryEncodingUtils.encode(account, AccountHeader.class);
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
			for (VersioningAccount acc : latestAccountsCache.values()) {
				// updated or new created;
				if (acc.isUpdated() || acc.version < 0) {
					// 提交更改，更新哈希；
					acc.commit();
					byte[] value = serialize(acc);
					long ver = merkleDataset.setValue(acc.getAddress(), value, acc.version);
					if (ver < 0) {
						// Update fail;
						throw new LedgerException("Account updating fail! --[Address=" + acc.getAddress() + "]");
					}
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
		String[] addresses = new String[latestAccountsCache.size()];
		latestAccountsCache.keySet().toArray(addresses);
		for (String address : addresses) {
			VersioningAccount acc = latestAccountsCache.remove(address);
			// cancel;
			if (acc.isUpdated()) {
				acc.cancel();
			}
		}
		updated = false;
	}

	public static class AccountHeaderData implements AccountHeader {

		private Bytes address;
		private PubKey pubKey;
		private HashDigest rootHash;

		public AccountHeaderData(Bytes address, PubKey pubKey, HashDigest rootHash) {
			this.address = address;
			this.pubKey = pubKey;
			this.rootHash = rootHash;
		}

		@Override
		public Bytes getAddress() {
			return address;
		}

		@Override
		public PubKey getPubKey() {
			return pubKey;
		}

		@Override
		public HashDigest getRootHash() {
			return rootHash;
		}

	}

	private class VersioningAccount extends BaseAccount {

		// private final BaseAccount account;

		private final long version;

		// public VersioningAccount(BaseAccount account, long version) {
		// this.account = account;
		// this.version = version;
		// }

		public VersioningAccount(Bytes address, PubKey pubKey, HashDigest rootHash, CryptoSetting cryptoSetting,
				String keyPrefix, ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly,
				AccountAccessPolicy accessPolicy, long version) {
			super(address, pubKey, rootHash, cryptoSetting, keyPrefix, exStorage, verStorage, readonly, accessPolicy);
			this.version = version;
		}

		public VersioningAccount(Bytes address, PubKey pubKey, CryptoSetting cryptoSetting, String keyPrefix,
				ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, AccountAccessPolicy accessPolicy,
				long version) {
			super(address, pubKey, cryptoSetting, keyPrefix, exStorage, verStorage, accessPolicy);
			this.version = version;
		}

		// @Override
		// public Bytes getAddress() {
		// return account.getAddress();
		// }
		//
		// @Override
		// public PubKey getPubKey() {
		// return account.getPubKey();
		// }
		//
		// @Override
		// public HashDigest getRootHash() {
		// return account.getRootHash();
		// }
		//
		// @Override
		// public MerkleProof getProof(Bytes key) {
		// return account.getProof(key);
		// }
		//
		// @Override
		// public boolean isReadonly() {
		// return account.isReadonly();
		// }

		@Override
		public long setBytes(Bytes key, byte[] value, long version) {
			long v = super.setBytes(key, value, version);
			if (v > -1) {
				updated = true;
			}
			return v;
		}

		// @Override
		// public long getKeyVersion(Bytes key) {
		// return account.getKeyVersion(key);
		// }
		//
		// @Override
		// public byte[] getBytes(Bytes key) {
		// return account.getBytes(key);
		// }
		//
		// @Override
		// public byte[] getBytes(Bytes key, long version) {
		// return account.getBytes(key, version);
		// }

	}

}