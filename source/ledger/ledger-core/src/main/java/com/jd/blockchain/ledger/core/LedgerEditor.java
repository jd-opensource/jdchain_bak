package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.TransactionRequest;

/**
 * {@link LedgerEditor} 定义了对账本的编辑视图；
 * <p>
 * 
 * {@link LedgerEditor} 以上一个区块作为数据编辑的起点; <br>
 * 对账本数据({@link #getDataset()})的批量更改可以作为一个交易({@link LedgerTransaction})整体提交暂存，形成暂存点；
 * <br>
 * 
 * @author huanghaiquan
 *
 */
public interface LedgerEditor {

	/**
	 * 账本Hash；
	 * 
	 * @return
	 */
	HashDigest getLedgerHash();

	/**
	 * 新区块的高度；
	 * 
	 * @return
	 */
	long getBlockHeight();

	/**
	 * 最新的账本数据集；
	 * 
	 * @return
	 */
	LedgerDataset getLedgerDataset();

	/**
	 * 最新的交易集合；
	 * 
	 * @return
	 */
	TransactionSet getTransactionSet();

	/**
	 * 开始新事务；<br>
	 * 
	 * 方法返回之前，将会校验交易请求的用户签名列表和节点签名列表，并在后续对数据集
	 * {@link LedgerTransactionContext#getDataset()} 的操作时，校验这些用户和节点是否具备权限；<br>
	 * 
	 * 校验失败将引发异常 {@link LedgerException};
	 * <p>
	 * 
	 * 调用者通过获得的 {@link LedgerTransactionContext} 对象对账本进行操作，这些写入操作可以一起提交（通过方法
	 * {@link LedgerTransactionContext#commit(com.jd.blockchain.ledger.ExecutionState)}）,<br>
	 * 或者全部回滚（通过方法 {@link LedgerTransactionContext#rollback()}），以此实现原子性写入；
	 * <p>
	 * 
	 * 每一次事务性的账本写入操作在提交后，都会记录该事务相关的系统全局快照，以交易对象 {@link LedgerTransaction} 进行保存；
	 * <p>
	 * 
	 * 
	 * 
	 * 注：方法不解析、不执行交易中的操作；
	 * <p>
	 * 
	 * @param txRequest 交易请求；
	 * @return
	 */
	LedgerTransactionContext newTransaction(TransactionRequest txRequest);

	/**
	 * 暂存当前的数据变更，并预提交生成新区块；
	 * 
	 * @param txRequest
	 * @param result
	 * @return
	 */
	LedgerBlock prepare();

	/**
	 * 提交数据；
	 */
	void commit();

	/**
	 * 丢弃所有数据变更以及暂存的交易；
	 */
	void cancel();

}
