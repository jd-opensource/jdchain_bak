//package com.jd.blockchain.ledger;
//
//import my.utils.io.ByteArray;
//
//import java.io.Serializable;
//
///**
// * Ledger 账本；<br>
// * 
// * 账本只是一个逻辑上的对象，它是对一条区块的hash链的归纳抽象，而在存储上并没有具体的存在形式，而是具体化为由一个特定的创世区块作为开端的区块 hash
// * 链；<br>
// * 
// * 账本的唯一标识也是其创世区块(GenisisBlock)的 hash；<br>
// * 
// * @author huanghaiquan
// *
// */
//public interface Ledger extends Serializable {
//
//	/**
//	 * 账本的 hash； <br>
//	 * 
//	 * 同时也是账本的唯一，等同于其创世区块(GenisisBlock)的 hash {@link GenesisBlock#getBlockHash()}；
//	 * 
//	 * @return
//	 */
//	ByteArray getLedgerHash();
//	
//	/**
//	 * 账本结构版本；<br>
//	 * 
//	 * 等同于 {@link Block#getLedgerVersion()}；
//	 * 
//	 * @return
//	 */
//	long getLedgerVersion();
//
//	/**
//	 * 由随机数构成的该账本的创世序列；
//	 * 
//	 * @return
//	 */
//	ByteArray getGenesisKey();
//
//	/**
//	 * 当前最新区块的 hash；
//	 * 
//	 * @return
//	 */
//	ByteArray getBlockHash();
//
//	/**
//	 * 账本的区块高度；
//	 * 
//	 * @return
//	 */
//	long getBlockHeight();
//
//}
