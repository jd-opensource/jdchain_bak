//package com.jd.blockchain.ledger;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
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
//public class AccountImpl implements BlockchainAccount, BytesWriter, BytesReader {
//	public static final long INIT_TX_SEQUENCE_NUMBER = 0; // 初始交易流水号
//	public static final long INIT_MODEL_VERSION = 0; // 初始模型版本号
//	public static final long INIT_VERSION = 0; // 初始版本号
//	public static final long INIT_PRIVILLEGE_VERSION = 0; // 初始权限版本号
//	public static final long INIT_STATE_VERSION = 0; // 初始状态版本号
//	public static final long INIT_CODE_VERSION = 0; // 初始合约版本号
//
//	private BlockchainIdentity identity;
//	private ByteArray ledgerHash; // 账本hash
//	private long blockHeight; // 账户注册的区块高度
//	private long txSequenceNumber; // 交易流水号
//	private long modelVersion; // 账户模型版本
//	private long version; // 账户版本
//	private ByteArray privilegeHash; // 权限树根hash
//	private long privilegeVersion; // 权限版本
//	private AccountStateType stateType; // 状态类型
//	private ByteArray stateHash; // 状态数根hash
//	private long stateVersion; // 状态版本
//	private ByteArray code; // 合约代码
//	private long codeVersion; // 合约版本
//	private HashAlgorithm codeHashAlgorithm = HashAlgorithm.SHA256;
//	private ByteArray codeHash;
//
//	public AccountImpl() {
//	}
//
//	public AccountImpl(BlockchainIdentity identity, ByteArray ledgerHash, long blockHeight, long txSequenceNumber,
//			long modelVersion, long version, ByteArray privilegeHash, long privilegeVersion, AccountStateType stateType,
//			ByteArray stateHash, long stateVersion, ByteArray code, long codeVersion) {
//		this.identity = identity;
//		this.ledgerHash = ledgerHash;
//		this.blockHeight = blockHeight;
//		this.txSequenceNumber = txSequenceNumber;
//		this.modelVersion = modelVersion;
//		this.version = version;
//		this.privilegeHash = privilegeHash;
//		this.privilegeVersion = privilegeVersion;
//		this.stateType = stateType;
//		this.stateHash = stateHash;
//		this.stateVersion = stateVersion;
//		this.code = code;
//		this.codeVersion = codeVersion;
//	}
//
//	public AccountImpl(BlockchainIdentity identity, ByteArray ledgerHash, long blockHeight, ByteArray privilegeHash,
//			AccountStateType stateType, ByteArray stateHash, ByteArray code) {
//		this.identity = identity;
//		this.ledgerHash = ledgerHash;
//		this.blockHeight = blockHeight;
//		this.txSequenceNumber = INIT_TX_SEQUENCE_NUMBER;
//		this.modelVersion = INIT_MODEL_VERSION;
//		this.version = INIT_VERSION;
//		this.privilegeHash = privilegeHash;
//		this.privilegeVersion = INIT_PRIVILLEGE_VERSION;
//		this.stateType = stateType;
//		this.stateHash = stateHash;
//		this.stateVersion = INIT_STATE_VERSION;
//		this.code = code;
//		this.codeVersion = INIT_CODE_VERSION;
//	}
//
//	@Override
//	public void resolvFrom(InputStream in) throws IOException {
//		BlockchainIdentity identity = new BlockchainIdentity();
//		identity.resolvFrom(in);
//		ByteArray ledgerHash = HashEncoding.read(in);
//		long blockHeight = BytesUtils.readLong(in);
//		long txSeqNum = BytesUtils.readLong(in);
//		long modelVersion = BytesUtils.readLong(in);
//		long version = BytesUtils.readLong(in);
//		ByteArray privilegeHash = HashEncoding.read(in);
//		long privilegeVersion = BytesUtils.readLong(in);
//		AccountStateType stateType = AccountStateType.valueOf(BytesUtils.readByte(in));
//		ByteArray stateHash = HashEncoding.read(in);
//		long stateVersion = BytesUtils.readLong(in);
//
//		ByteArray code = BytesEncoding.readAsByteArray(NumberMask.NORMAL, in);
//		long codeVersion = BytesUtils.readLong(in);
//		HashAlgorithm codeHashAlgorithm = HashAlgorithm.valueOf(BytesUtils.readByte(in));
//		ByteArray codeHash = HashEncoding.read(in);
//
//		this.identity = identity;
//		this.ledgerHash = ledgerHash;
//		this.blockHeight = blockHeight;
//		this.txSequenceNumber = txSeqNum;
//		this.modelVersion = modelVersion;
//		this.version = version;
//		this.privilegeHash = privilegeHash;
//		this.privilegeVersion = privilegeVersion;
//		this.stateType = stateType;
//		this.stateHash = stateHash;
//		this.stateVersion = stateVersion;
//		this.code = code;
//		this.codeVersion = codeVersion;
//		this.codeHashAlgorithm = codeHashAlgorithm;
//		this.codeHash = codeHash;
//	}
//
//	@Override
//	public void writeTo(OutputStream out) throws IOException {
//		identity.writeTo(out);
//		HashEncoding.write(ledgerHash, out);
//		BytesUtils.writeLong(blockHeight, out);
//		BytesUtils.writeLong(txSequenceNumber, out);
//		BytesUtils.writeLong(modelVersion, out);
//		BytesUtils.writeLong(version, out);
//		HashEncoding.write(privilegeHash, out);
//		BytesUtils.writeLong(privilegeVersion, out);
//		BytesUtils.writeByte(stateType.getCODE(), out);
//		HashEncoding.write(stateHash, out);
//		BytesUtils.writeLong(stateVersion, out);
//
//		BytesEncoding.write(code, NumberMask.NORMAL, out);
//		BytesUtils.writeLong(codeVersion, out);
//		BytesUtils.writeByte(codeHashAlgorithm.getAlgorithm(), out);
//		HashEncoding.write(getCodeHash(), out);
//	}
//
//	/**
//	 * 地址；
//	 *
//	 * @return
//	 */
//	@Override
//	public BlockchainIdentity getAddress() {
//		return identity;
//	}
//
//	/**
//	 * 账户所属的账本的 hash；<br>
//	 * <p>
//	 * 注：账本的hash 等同于该账本的创世区块的 hash；
//	 *
//	 * @return
//	 */
//	@Override
//	public ByteArray getLedgerHash() {
//		return ledgerHash;
//	}
//
//	/**
//	 * 注册账户的区块高度； <br>
//	 * <p>
//	 * 注册此账户的区块高度；
//	 *
//	 * @return
//	 */
//	@Override
//	public long getRegisteredHeight() {
//		return blockHeight;
//	}
//
//	/**
//	 * 交易流水号；<br>
//	 * <p>
//	 * 账户的交易流水号初始为 0，当账户作为交易的科目账户(SubjectAccount )发起一个交易并被成功执行之后，账户的交易流水号增加1；
//	 *
//	 * @return
//	 */
//	@Override
//	public long getTxSquenceNumber() {
//		return txSequenceNumber;
//	}
//
//	/**
//	 * 账户模型版本； <br>
//	 * <p>
//	 * 表示构成一个账户结构的属性模型的程序版本号；
//	 *
//	 * @return
//	 */
//	@Override
//	public long getModelVersion() {
//		return modelVersion;
//	}
//
//	/**
//	 * 账户版本； <br>
//	 * <p>
//	 * 初始为 0，对账户的每一次变更(包括对权限设置、状态和合约代码的变更)都会使账户状态版本增加 1 ；注：交易序号的改变不会导致账户版本的增加；
//	 *
//	 * @return
//	 */
//	@Override
//	public long getVersion() {
//		return version;
//	}
//
//	/**
//	 * 权限 hash；<br>
//	 * <p>
//	 * 权限树的根hash；
//	 *
//	 * @return
//	 */
//	@Override
//	public ByteArray getPrivilegeHash() {
//		return privilegeHash;
//	}
//
//	/**
//	 * 权限版本； <br>
//	 * <p>
//	 * 初始为 0， 每次对权限的变更都导致版本号加 1；
//	 *
//	 * @return
//	 */
//	@Override
//	public long getPrivilegeVersion() {
//		return privilegeVersion;
//	}
//
//	/**
//	 * 状态类型； <br>
//	 * <p>
//	 * 账户的状态类型有3种：空类型(NIL)；键值类型；对象类型；参考 {@link AccountStateType}
//	 *
//	 * @return
//	 */
//	@Override
//	public AccountStateType getStateType() {
//		return stateType;
//	}
//
//	/**
//	 * 状态版本；<br>
//	 * <p>
//	 * 初始为 0，每次对状态的更改都使得状态版本增加1；
//	 *
//	 * @return
//	 */
//	@Override
//	public long getStateVersion() {
//		return stateVersion;
//	}
//
//	/**
//	 * 状态哈希；<br>
//	 * <p>
//	 * 数据状态的 merkle tree 的根hash；
//	 *
//	 * @return
//	 */
//	@Override
//	public ByteArray getStateHash() {
//		return stateHash;
//	}
//
//	/**
//	 * 合约代码哈希； <br>
//	 * <p>
//	 * 由“账户地址+合约代码版本号+合约代码内容”生成的哈希；
//	 *
//	 * @return
//	 */
//	@Override
//	public ByteArray getCodeHash() {
//		if (codeHash == null || codeHash == ByteArray.EMPTY) {
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			BytesEncoding.write(getAddress().getAddress().getBytes(), NumberMask.SHORT, out);
//			BytesUtils.writeLong(codeVersion, out);
//			BytesEncoding.write(code, NumberMask.NORMAL, out);
//
//			codeHash = HashEncoding.computeHash(out.toByteArray(), codeHashAlgorithm);
//		}
//
//		return codeHash;
//	}
//
//	public ByteArray getCode() {
//		return code;
//	}
//
//	/**
//	 * 代码版本； <br>
//	 * <p>
//	 * 初始为 0，每次对代码的变更都使版本加 1 ；
//	 *
//	 * @return
//	 */
//	@Override
//	public long getCodeVersion() {
//		return codeVersion;
//	}
//
//	public BlockchainIdentity getIdentity() {
//		return identity;
//	}
//
//	public void setIdentity(BlockchainIdentity identity) {
//		this.identity = identity;
//	}
//
//	public void setLedgerHash(ByteArray ledgerHash) {
//		this.ledgerHash = ledgerHash;
//	}
//
//	public long getBlockHeight() {
//		return blockHeight;
//	}
//
//	public void setBlockHeight(long blockHeight) {
//		this.blockHeight = blockHeight;
//	}
//
//	public long getTxSequenceNumber() {
//		return txSequenceNumber;
//	}
//
//	public void setTxSequenceNumber(long txSequenceNumber) {
//		this.txSequenceNumber = txSequenceNumber;
//	}
//
//	public void setModelVersion(long modelVersion) {
//		this.modelVersion = modelVersion;
//	}
//
//	public void setVersion(long version) {
//		this.version = version;
//	}
//
//	public void setPrivilegeHash(ByteArray privilegeHash) {
//		this.privilegeHash = privilegeHash;
//	}
//
//	public void setPrivilegeVersion(long privilegeVersion) {
//		this.privilegeVersion = privilegeVersion;
//	}
//
//	public void setStateType(AccountStateType stateType) {
//		this.stateType = stateType;
//	}
//
//	public void setStateHash(ByteArray stateHash) {
//		this.stateHash = stateHash;
//	}
//
//	public void setStateVersion(long stateVersion) {
//		this.stateVersion = stateVersion;
//	}
//
//	public void setCodeHash(ByteArray codeHash) {
//		this.codeHash = codeHash;
//	}
//
//	public void setCodeVersion(long codeVersion) {
//		this.codeVersion = codeVersion;
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o)
//			return true;
//		if (!(o instanceof AccountImpl))
//			return false;
//
//		AccountImpl account = (AccountImpl) o;
//
//		if (getBlockHeight() != account.getBlockHeight())
//			return false;
//		if (getTxSequenceNumber() != account.getTxSequenceNumber())
//			return false;
//		if (getModelVersion() != account.getModelVersion())
//			return false;
//		if (getVersion() != account.getVersion())
//			return false;
//		if (getPrivilegeVersion() != account.getPrivilegeVersion())
//			return false;
//		if (getStateVersion() != account.getStateVersion())
//			return false;
//		if (getCodeVersion() != account.getCodeVersion())
//			return false;
//		if (!getIdentity().equals(account.getIdentity()))
//			return false;
//		if (!getLedgerHash().equals(account.getLedgerHash()))
//			return false;
//		if (!getPrivilegeHash().equals(account.getPrivilegeHash()))
//			return false;
//		if (getStateType() != account.getStateType())
//			return false;
//		if (!getStateHash().equals(account.getStateHash()))
//			return false;
//		if (!code.equals(account.code))
//			return false;
//		if (codeHashAlgorithm != account.codeHashAlgorithm)
//			return false;
//		return getCodeHash().equals(account.getCodeHash());
//	}
//}
