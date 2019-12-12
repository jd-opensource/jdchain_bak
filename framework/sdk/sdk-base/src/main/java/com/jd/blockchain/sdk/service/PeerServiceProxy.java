package com.jd.blockchain.sdk.service;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.sdk.BlockchainException;
import com.jd.blockchain.sdk.LedgerAccessContext;
import com.jd.blockchain.sdk.proxy.BlockchainServiceProxy;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TransactionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对共识节点的区块链服务代理；
 * 
 * @author huanghaiquan
 *
 */
public class PeerServiceProxy extends BlockchainServiceProxy implements TransactionService {

	private final Lock accessLock = new ReentrantLock();

	/**
	 * 许可的账本以及交易列表；
	 */
	private Map<HashDigest, LedgerAccessContext> ledgerAccessContexts;

	public PeerServiceProxy(LedgerAccessContext[] accessAbleLedgers) {
		this.ledgerAccessContexts = new HashMap<>();
		for (LedgerAccessContext lac : accessAbleLedgers) {
			if (ledgerAccessContexts.containsKey(lac.getLedgerHash())) {
				throw new IllegalArgumentException(
						String.format("Ledger repeatly! --[LedgerHash=%s]", lac.getLedgerHash().toBase58()));
			}
			ledgerAccessContexts.put(lac.getLedgerHash(), lac);
		}
	}

	public void addLedgerAccessContexts(LedgerAccessContext[] accessAbleLedgers) {
		try {
			accessLock.lock();
			if (this.ledgerAccessContexts == null) {
				throw new IllegalArgumentException("LedgerAccessContexts is null, you need init first !!!");
			}
			for (LedgerAccessContext lac : accessAbleLedgers) {
				if (!ledgerAccessContexts.containsKey(lac.getLedgerHash())) {
					ledgerAccessContexts.put(lac.getLedgerHash(), lac);
				}
			}
		} finally {
			accessLock.unlock();
		}
	}

	@Override
	protected TransactionService getTransactionService(HashDigest ledgerHash) {
		return getLedgerAccessContext(ledgerHash).getTransactionService();
	}

	@Override
	protected BlockchainQueryService getQueryService(HashDigest ledgerHash) {
		return getLedgerAccessContext(ledgerHash).getQueryService();
	}

	private LedgerAccessContext getLedgerAccessContext(HashDigest ledgerHash) {
		LedgerAccessContext lac = ledgerAccessContexts.get(ledgerHash);
		if (lac == null) {
			throw new BlockchainException("Unsupported access ledger[" + ledgerHash.toBase58() + "] !");
		}
		return lac;
	}

	@Override
	public HashDigest[] getLedgerHashs() {
		return ledgerAccessContexts.keySet().toArray(new HashDigest[ledgerAccessContexts.size()]);
	}

	/**
	 * 处理网关的交易转发； 
	 */
	@Override
	public TransactionResponse process(TransactionRequest txRequest) {
		TransactionService targetTxService = getTransactionService(txRequest.getTransactionContent().getLedgerHash());
		return targetTxService.process(txRequest);
	}
}
