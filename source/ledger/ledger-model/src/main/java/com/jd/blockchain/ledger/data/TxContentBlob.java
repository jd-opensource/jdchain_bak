package com.jd.blockchain.ledger.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jd.blockchain.binaryproto.DConstructor;
import com.jd.blockchain.binaryproto.FieldSetter;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.utils.io.NumberMask;

/**
 * 交易内容的数据块；
 * <p>
 * 
 * 包含原始交易请求的数据块；
 * 
 * @author huanghaiquan
 *
 */
public class TxContentBlob implements TransactionContent {

	/**
	 * 操作数量的最大值；
	 */
	public static final int MAX_OP_COUNT = NumberMask.SHORT.MAX_BOUNDARY_SIZE;

	private List<Operation> operationList = new ArrayList<Operation>();

	private HashDigest hash;

	private HashDigest ledgerHash;

	//交易操作时间;
	private Long txOpTime;

	@DConstructor(name ="TxContentBlob")
	public TxContentBlob(@FieldSetter(name="getLedgerHash", type="HashDigest") HashDigest ledgerHash) {
		this.ledgerHash = ledgerHash;
	}

	/**
	 * 交易内容的哈希值；
	 */
	@Override
	public HashDigest getHash() {
		return this.hash;
	}
	
	/**
	 * 更新交易内容的哈希值；
	 * <p>
	 * 注：当前对象只充当值对象，不校验指定哈希值的完整性，调用者应该在外部实施完整性校验；
	 * @param hash
	 */
	public void setHash(HashDigest hash) {
		this.hash = hash;
	}

	/**
	 * 交易请求链的hash
	 *
	 * @return
	 */
	@Override
	public HashDigest getLedgerHash() {
		return ledgerHash;
	}
	public void setLedgerHash(HashDigest ledgerHash) {
		this.ledgerHash = ledgerHash;
	}

	@Override
	public Operation[] getOperations() {
		return operationList.toArray(new Operation[operationList.size()]);
	}

    public void setOperations(Object[] operations) {
		//in array's case ,cast will failed!
		for (Object operation : operations) {
			Operation op = (Operation)operation;
			addOperation(op);
		}
	}

	public void addOperation(Operation operation) {
		operationList.add(operation);
	}
	
	public void addOperations(Collection<Operation> operations) {
		operationList.addAll(operations);
	}

	@Override
	public Long getTxOpTime() {
		return txOpTime;
	}

	public void setTxOpTime(Long txOpTime) {
		this.txOpTime = txOpTime;
	}
}
