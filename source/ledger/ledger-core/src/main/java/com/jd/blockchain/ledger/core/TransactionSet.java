package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;

public class TransactionSet implements Transactional, TransactionQuery {

	static {
		DataContractRegistry.register(LedgerTransaction.class);
	}

	private static final String TX_STATE_PREFIX = "STA" + LedgerConsts.KEY_SEPERATOR;

	private final Bytes txStatePrefix;

	private MerkleDataSet txSet;

	@Override
	public LedgerTransaction[] getTxs(int fromIndex, int count) {
		if (count > LedgerConsts.MAX_LIST_COUNT) {
			throw new IllegalArgumentException("Count exceed the upper limit[" + LedgerConsts.MAX_LIST_COUNT + "]!");
		}
		byte[][] results = getValuesByIndex(fromIndex, count);
		LedgerTransaction[] ledgerTransactions = new LedgerTransaction[results.length];

		for (int i = 0; i < results.length; i++) {
			ledgerTransactions[i] = deserialize(results[i]);
		}
		return ledgerTransactions;
	}

	@Override
	public byte[][] getValuesByIndex(int fromIndex, int count) {
		byte[][] values = new byte[count][];
		for (int i = 0; i < count; i++) {
			values[i] = txSet.getValuesAtIndex(fromIndex * 2);
			fromIndex++;
		}
		return values;
	}

	@Override
	public HashDigest getRootHash() {
		return txSet.getRootHash();
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		return txSet.getProof(key);
	}

	@Override
	public long getTotalCount() {
		// 每写入一个交易，同时写入交易内容Hash与交易结果的索引，因此交易记录数为集合总记录数除以 2；
		return txSet.getDataCount() / 2;
	}

	/**
	 * Create a new TransactionSet which can be added transaction;
	 * 
	 * @param setting
	 * @param merkleTreeStorage
	 * @param dataStorage
	 */
	public TransactionSet(CryptoSetting setting, String keyPrefix, ExPolicyKVStorage merkleTreeStorage,
			VersioningKVStorage dataStorage) {
		this.txStatePrefix = Bytes.fromString(keyPrefix + TX_STATE_PREFIX);
		this.txSet = new MerkleDataSet(setting, keyPrefix, merkleTreeStorage, dataStorage);
	}

	/**
	 * Create TransactionSet which is readonly to the history transactions;
	 * 
	 * @param setting
	 * @param merkleTreeStorage
	 * @param dataStorage
	 */
	public TransactionSet(HashDigest txRootHash, CryptoSetting setting, String keyPrefix,
			ExPolicyKVStorage merkleTreeStorage, VersioningKVStorage dataStorage, boolean readonly) {
		this.txStatePrefix = Bytes.fromString(keyPrefix + TX_STATE_PREFIX);
		this.txSet = new MerkleDataSet(txRootHash, setting, Bytes.fromString(keyPrefix), merkleTreeStorage, dataStorage,
				readonly);
	}

	/**
	 * @param txRequest
	 * @param result
	 */
	public void add(LedgerTransaction tx) {
		// TODO: 优化对交易内存存储的优化，应对大数据量单交易，共享操作的“写集”与实际写入账户的KV版本；
		// 序列化交易内容；
		byte[] txBytes = serialize(tx);
		// 以交易内容的 hash 为 key；
		// String key = tx.getTransactionContent().getHash().toBase58();
		Bytes key = new Bytes(tx.getTransactionContent().getHash().toBytes());
		// 交易只有唯一的版本；
		long v = txSet.setValue(key, txBytes, -1);
		if (v < 0) {
			throw new LedgerException("Transaction is persisted repeatly! --[" + key + "]");
		}
		// 以交易内容的hash值为key，单独记录交易结果的索引，以便快速查询交易结果；
		Bytes resultKey = encodeTxStateKey(key);
		v = txSet.setValue(resultKey, new byte[] { tx.getExecutionState().CODE }, -1);
		if (v < 0) {
			throw new LedgerException("Transaction result is persisted repeatly! --[" + key + "]");
		}
	}

	/**
	 * @param txContentHash Base58 编码的交易内容的哈希；
	 * @return
	 */
	@Override
	public LedgerTransaction get(HashDigest txContentHash) {
		// transaction has only one version;
		Bytes key = new Bytes(txContentHash.toBytes());
		// byte[] txBytes = txSet.getValue(txContentHash.toBase58(), 0);
		byte[] txBytes = txSet.getValue(key, 0);
		if (txBytes == null) {
			return null;
		}
		LedgerTransaction tx = deserialize(txBytes);
		return tx;
	}

	@Override
	public TransactionState getState(HashDigest txContentHash) {
		Bytes resultKey = encodeTxStateKey(txContentHash);
		// transaction has only one version;
		byte[] bytes = txSet.getValue(resultKey, 0);
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return TransactionState.valueOf(bytes[0]);
	}

	private Bytes encodeTxStateKey(Bytes txContentHash) {
		return new Bytes(txStatePrefix, txContentHash);
	}

	private LedgerTransaction deserialize(byte[] txBytes) {
		return BinaryProtocol.decode(txBytes);
	}

	private byte[] serialize(LedgerTransaction txRequest) {
		return BinaryProtocol.encode(txRequest, LedgerTransaction.class);
	}

	public boolean isReadonly() {
		return txSet.isReadonly();
	}

	void setReadonly() {
		txSet.setReadonly();
	}

	@Override
	public boolean isUpdated() {
		return txSet.isUpdated();
	}

	@Override
	public void commit() {
		txSet.commit();
	}

	@Override
	public void cancel() {
		txSet.cancel();
	}

}
