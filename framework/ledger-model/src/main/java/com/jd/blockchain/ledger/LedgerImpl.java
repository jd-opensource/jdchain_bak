//package com.jd.blockchain.ledger;
//
//import com.jd.blockchain.ledger.data.HashEncoding;
//
//import my.utils.io.ByteArray;
//import my.utils.io.BytesEncoding;
//import my.utils.io.BytesReader;
//import my.utils.io.BytesUtils;
//import my.utils.io.BytesWriter;
//import my.utils.io.NumberMask;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Objects;
//
///**
// * Ledger 实现
// *
// * @author zhaoming9
// */
//public class LedgerImpl implements Ledger, BytesWriter, BytesReader {
//
//    private HashAlgorithm ledgerHashAlgorithm = HashAlgorithm.SHA256; // 账本hash算法
//    private ByteArray ledgerHash = ByteArray.EMPTY; // 账本hash
//
//    private long blockHeight = 0; // 账本当前高度
//    private long blockVersion = 0; // 账本当前版本
//
//    private HashAlgorithm currentBlockHashAlgorithm = HashAlgorithm.SHA256; // 账本当前区块hash算法
//    private ByteArray currentBlockHash = ByteArray.EMPTY; // 账本当前区块hash
//
//    private HashAlgorithm previousBlockHashAlgorithm = HashAlgorithm.SHA256; // 账本前一区块hash算法
//    private ByteArray previousBlockHash = ByteArray.EMPTY; // 账本前一区块hash
//
//    private ByteArray accountRoot = ByteArray.EMPTY; // account mpt root hash
//    private long accountCount; // 账户数量
//    private long txTotalCount; // 交易数量
//
//    private ByteArray genesisKey=ByteArray.EMPTY; // 创世块随机序列
//    
//    public LedgerImpl() {
//	}
//
//    /**
//     * 初始化一个新的账本；
//     * @param genesisKey
//     */
//    public LedgerImpl(ByteArray genesisKey) {
//        this.genesisKey = genesisKey;
//    }
//
//    /**
//     * @param ledgerHashAlgorithm
//     * @param ledgerHash
//     * @param height
//     * @param version
//     * @param currentBlockHashAlgorithm
//     * @param currentBlockHash
//     * @param previousBlockHashAlgorithm
//     * @param previousBlockHash
//     * @param accountRoot
//     * @param accountCount
//     * @param txTotalCount
//     * @param genesisKey
//     */
//    private LedgerImpl(HashAlgorithm ledgerHashAlgorithm, ByteArray ledgerHash, long height, long version,
//                       HashAlgorithm currentBlockHashAlgorithm, ByteArray currentBlockHash,
//                       HashAlgorithm previousBlockHashAlgorithm, ByteArray previousBlockHash,
//                       ByteArray accountRoot, long accountCount, long txTotalCount, ByteArray genesisKey) {
//        this.ledgerHashAlgorithm = ledgerHashAlgorithm;
//        this.ledgerHash = ledgerHash;
//        this.blockHeight = height;
//        this.blockVersion = version;
//        this.currentBlockHashAlgorithm = currentBlockHashAlgorithm;
//        this.currentBlockHash = currentBlockHash;
//        this.previousBlockHashAlgorithm = previousBlockHashAlgorithm;
//        this.previousBlockHash = previousBlockHash;
//        this.accountRoot = accountRoot;
//        this.accountCount = accountCount;
//        this.txTotalCount = txTotalCount;
//        this.genesisKey = genesisKey;
//    }
//
//    public LedgerImpl(ByteArray ledgerHash, long blockHeight, long blockVersion, ByteArray currentBlockHash,
//                      ByteArray previousBlockHash, ByteArray accountRoot, long accountCount, long txTotalCount, ByteArray genesisKey) {
//        this(HashAlgorithm.SHA256, ledgerHash, blockHeight, blockVersion, HashAlgorithm.SHA256, currentBlockHash,
//                HashAlgorithm.SHA256, previousBlockHash, accountRoot, accountCount, txTotalCount, genesisKey);
//    }
//
//    public LedgerImpl(LedgerImpl ledger) {
//        this(ledger.getLedgerHashAlgorithm(), ledger.getLedgerHash(), ledger.getBlockHeight(), ledger.getBlockVersion(),
//                ledger.getCurrentBlockHashAlgorithm(), ledger.getCurrentBlockHash(),
//                ledger.getPreviousBlockHashAlgorithm(), ledger.getPreviousBlockHash(),
//                ledger.getAccountRoot(), ledger.getAccountCount(), ledger.getTxTotalCount(),ledger.getGenesisKey());
//    }
//
//    public LedgerImpl nextLedger(ByteArray nextBlockHash, ByteArray accountRoot, long newAccountCnt, long newTxCnt) {
//        LedgerImpl nextLedger = new LedgerImpl(this);
//        nextLedger.blockHeight+=1;
//        nextLedger.previousBlockHash = nextLedger.currentBlockHash;
//        nextLedger.currentBlockHash = nextBlockHash;
//        nextLedger.accountRoot = accountRoot;
//        nextLedger.accountCount += newAccountCnt;
//        nextLedger.txTotalCount += newTxCnt;
//
//        return nextLedger;
//    }
//
//    /**
//     * 账本的 hash； <br>
//     * <p>
//     * 同时也是账本的唯一，等同于其创世区块(GenisisBlock)的 hash
//     *
//     * @return
//     */
//    @Override
//    public ByteArray getLedgerHash() {
//        return ledgerHash;
//    }
//
//    /**
//     * 由随机数构成的该账本的创世序列；
//     *
//     * @return
//     */
//    @Override
//    public ByteArray getGenesisKey() {
//        return genesisKey;
//    }
//
//    /**
//     * 当前最新区块的 hash；
//     *
//     * @return
//     */
//    @Override
//    public ByteArray getBlockHash() {
//        return currentBlockHash;
//    }
//
//    public HashAlgorithm getBlockHashAlgorithm() {
//        return currentBlockHashAlgorithm;
//    }
//
//    /**
//     * 账本的区块高度；
//     *
//     * @return
//     */
//    @Override
//    public long getBlockHeight() {
//        return blockHeight;
//    }
//
//    @Override
//    public void resolvFrom(InputStream in) throws IOException {
//        HashAlgorithm ledgerHashAlgorithm = HashAlgorithm.valueOf(BytesUtils.readByte(in));
//        HashAlgorithm.checkHashAlgorithm(ledgerHashAlgorithm);
//        ByteArray ledgerHash = HashEncoding.read(in);
//
//        long height = BytesUtils.readLong(in);
//        long version = BytesUtils.readLong(in);
//
//        HashAlgorithm currentBlockHashAlgorithm = HashAlgorithm.valueOf(BytesUtils.readByte(in));
//        HashAlgorithm.checkHashAlgorithm(currentBlockHashAlgorithm);
//        ByteArray currentBlockHash = HashEncoding.read(in);
//
//        HashAlgorithm previousBlockHashAlgorithm = HashAlgorithm.valueOf(BytesUtils.readByte(in));
//        HashAlgorithm.checkHashAlgorithm(previousBlockHashAlgorithm);
//        ByteArray previousBlockHash = HashEncoding.read(in);
//
//        ByteArray accountHash = HashEncoding.read(in);
//        long accountCount = BytesUtils.readLong(in);
//        long txTotalCount = BytesUtils.readLong(in);
//        ByteArray key = BytesEncoding.readAsByteArray(NumberMask.SHORT, in);
//
//        this.ledgerHashAlgorithm = ledgerHashAlgorithm;
//        this.ledgerHash = ledgerHash;
//        this.blockHeight = height;
//        this.blockVersion = version;
//        this.currentBlockHashAlgorithm = currentBlockHashAlgorithm;
//        this.currentBlockHash = currentBlockHash;
//        this.previousBlockHashAlgorithm = previousBlockHashAlgorithm;
//        this.previousBlockHash = previousBlockHash;
//        this.accountRoot = accountHash;
//        this.accountCount = accountCount;
//        this.txTotalCount = txTotalCount;
//        this.genesisKey = key;
//    }
//
//    @Override
//    public void writeTo(OutputStream out) throws IOException {
//        BytesUtils.writeByte(ledgerHashAlgorithm.getAlgorithm(), out);
//        HashEncoding.write(ledgerHash, out);
//
//        BytesUtils.writeLong(blockHeight, out);
//        BytesUtils.writeLong(blockVersion, out);
//
//        BytesUtils.writeByte(currentBlockHashAlgorithm.getAlgorithm(), out);
//        HashEncoding.write(currentBlockHash, out);
//
//        BytesUtils.writeByte(previousBlockHashAlgorithm.getAlgorithm(), out);
//        HashEncoding.write(previousBlockHash, out);
//
//        HashEncoding.write(accountRoot, out);
//        BytesUtils.writeLong(accountCount, out);
//        BytesUtils.writeLong(txTotalCount, out);
//        BytesEncoding.write(genesisKey, NumberMask.SHORT, out);
//    }
//
//    public HashAlgorithm getLedgerHashAlgorithm() {
//        return ledgerHashAlgorithm;
//    }
//
//    public void setLedgerHashAlgorithm(HashAlgorithm ledgerHashAlgorithm) {
//        this.ledgerHashAlgorithm = ledgerHashAlgorithm;
//    }
//
//    public void setLedgerHash(ByteArray ledgerHash) {
//        this.ledgerHash = ledgerHash;
//    }
//
//    public void setBlockHeight(long blockHeight) {
//        this.blockHeight = blockHeight;
//    }
//
//    public HashAlgorithm getCurrentBlockHashAlgorithm() {
//        return currentBlockHashAlgorithm;
//    }
//
//    public void setCurrentBlockHashAlgorithm(HashAlgorithm currentBlockHashAlgorithm) {
//        this.currentBlockHashAlgorithm = currentBlockHashAlgorithm;
//    }
//
//    public long getBlockVersion() {
//        return blockVersion;
//    }
//
//    public void setBlockVersion(long blockVersion) {
//        this.blockVersion = blockVersion;
//    }
//
//    public void setGenesisKey(ByteArray genesisKey) {
//        this.genesisKey = genesisKey;
//    }
//
//    public ByteArray getCurrentBlockHash() {
//        return currentBlockHash;
//    }
//
//    public void setCurrentBlockHash(ByteArray currentBlockHash) {
//        this.currentBlockHash = currentBlockHash;
//    }
//
//    public HashAlgorithm getPreviousBlockHashAlgorithm() {
//        return previousBlockHashAlgorithm;
//    }
//
//    public void setPreviousBlockHashAlgorithm(HashAlgorithm previousBlockHashAlgorithm) {
//        this.previousBlockHashAlgorithm = previousBlockHashAlgorithm;
//    }
//
//    public ByteArray getAccountRoot() {
//        return accountRoot;
//    }
//
//    public void setAccountRoot(ByteArray accountRoot) {
//        this.accountRoot = accountRoot;
//    }
//
//    public long getAccountCount() {
//        return accountCount;
//    }
//
//    public void setAccountCount(long accountCount) {
//        this.accountCount = accountCount;
//    }
//
//    public long getTxTotalCount() {
//        return txTotalCount;
//    }
//
//    public void setTxTotalCount(long txTotalCount) {
//        this.txTotalCount = txTotalCount;
//    }
//
//    public ByteArray getPreviousBlockHash() {
//        return previousBlockHash;
//    }
//
//    public void setPreviousBlockHash(ByteArray previousBlockHash) {
//        this.previousBlockHash = previousBlockHash;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof LedgerImpl)) return false;
//        LedgerImpl ledger = (LedgerImpl) o;
//        return getBlockHeight() == ledger.getBlockHeight() &&
//                getBlockVersion() == ledger.getBlockVersion() &&
//                getLedgerHashAlgorithm() == ledger.getLedgerHashAlgorithm() &&
//                Objects.equals(getLedgerHash(), ledger.getLedgerHash()) &&
//                getCurrentBlockHashAlgorithm() == ledger.getCurrentBlockHashAlgorithm() &&
//                Objects.equals(getCurrentBlockHash(), ledger.getCurrentBlockHash()) &&
//                getPreviousBlockHashAlgorithm() == ledger.getPreviousBlockHashAlgorithm() &&
//                Objects.equals(getPreviousBlockHash(), ledger.getPreviousBlockHash()) &&
//                Objects.equals(getGenesisKey(), ledger.getGenesisKey());
//    }
//
//    @Override
//    public int hashCode() {
//
//        return Objects.hash(getLedgerHashAlgorithm(), getLedgerHash(), getBlockHeight(), getBlockVersion(), getCurrentBlockHashAlgorithm(), getCurrentBlockHash(), getPreviousBlockHashAlgorithm(), getPreviousBlockHash(), getGenesisKey());
//    }
//
//	@Override
//	public long getLedgerVersion() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//}
