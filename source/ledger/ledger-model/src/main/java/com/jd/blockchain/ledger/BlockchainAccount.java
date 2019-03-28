//package com.jd.blockchain.ledger;
//
//import my.utils.io.ByteArray;
//
//import java.io.Serializable;
//
///**
// * 区块链账户；
// * 
// * @author huanghaiquan
// *
// */
//public interface BlockchainAccount extends Serializable {
//
//	/**
//	 * 地址；
//	 * 
//	 * @return
//	 */
//	BlockchainIdentity getAddress();
//
//	/**
//	 * 账户所属的账本的 hash；<br>
//	 * 
//	 * 注：账本的hash 等同于该账本的创世区块的 hash；
//	 * 
//	 * @return
//	 */
//	ByteArray getLedgerHash();
//
//	/**
//	 * 注册账户的区块高度； <br>
//	 * 
//	 * 注册此账户的区块高度；
//	 * 
//	 * @return
//	 */
//	long getRegisteredHeight();
//
//	/**
//	 * 交易流水号；<br>
//	 * 
//	 * 账户的交易流水号初始为 0，当账户作为交易的科目账户(SubjectAccount )发起一个交易并被成功执行之后，账户的交易流水号增加1；
//	 * 
//	 * @return
//	 */
//	long getTxSquenceNumber();
//
//	/**
//	 * 账户模型版本； <br>
//	 * 
//	 * 表示构成一个账户结构的属性模型的程序版本号；
//	 * 
//	 * @return
//	 */
//	long getModelVersion();
//
//	/**
//	 * 账户版本； <br>
//	 * 
//	 * 初始为 0，对账户的每一次变更(包括对权限设置、状态和合约代码的变更)都会使账户状态版本增加 1 ；注：交易序号的改变不会导致账户版本的增加；
//	 * 
//	 * @return
//	 */
//	long getVersion();
//
//	// /**
//	// * 权限设置；
//	// *
//	// * @return
//	// */
//	// PrivilegeSetting getPrivilegeSetting();
//
//	/**
//	 * 权限 hash；<br>
//	 * 
//	 * 权限树的根hash；
//	 * 
//	 * @return
//	 */
//	ByteArray getPrivilegeHash();
//
//	/**
//	 * 权限版本； <br>
//	 * 
//	 * 初始为 0， 每次对权限的变更都导致版本号加 1；
//	 * 
//	 * @return
//	 */
//	long getPrivilegeVersion();
//
////	/**
////	 * 状态类型； <br>
////	 * 
////	 * 账户的状态类型有3种：空类型(NIL)；键值类型；对象类型；参考 {@link AccountStateType}
////	 * 
////	 * @return
////	 */
////	AccountStateType getStateType();
//
//	/**
//	 * 状态版本；<br>
//	 * 
//	 * 初始为 0，每次对状态的更改都使得状态版本增加1；
//	 * 
//	 * @return
//	 */
//	long getStateVersion();
//
//	/**
//	 * 状态哈希；<br>
//	 * 
//	 * 数据状态的 merkle tree 的根hash；
//	 * 
//	 * @return
//	 */
//	ByteArray getStateHash();
//
//	// /**
//	// * 合约代码；<br>
//	// *
//	// * 合约代码是一段代码片段；
//	// *
//	// * @return
//	// */
//	// String getCode();
//
//	/**
//	 * 合约代码哈希； <br>
//	 * 
//	 * 由“账户地址+合约代码版本号+合约代码内容”生成的哈希；
//	 * 
//	 * @return
//	 */
//	ByteArray getCodeHash();
//
//	/**
//	 * 代码版本； <br>
//	 * 
//	 * 初始为 0，每次对代码的变更都使版本加 1 ；
//	 * 
//	 * @return
//	 */
//	long getCodeVersion();
//
//}
