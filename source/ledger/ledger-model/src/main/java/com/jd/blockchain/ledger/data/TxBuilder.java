package com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.TransactionBuilder;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionContentBody;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.utils.Bytes;

public class TxBuilder implements TransactionBuilder {

	static {
		DataContractRegistry.register(TransactionContentBody.class);
	}

	private BlockchainOperationFactory opFactory = new BlockchainOperationFactory();

	private static final String DEFAULT_HASH_ALGORITHM = "SHA256";
	
	private HashDigest ledgerHash;

	public TxBuilder(HashDigest ledgerHash) {
		this.ledgerHash = ledgerHash;
	}

	@Override
	public HashDigest getLedgerHash() {
		return ledgerHash;
	}

	@Override
	public TransactionRequestBuilder prepareRequest() {
		TransactionContent txContent = prepareContent();
		return new TxRequestBuilder(txContent);
	}
	
	@Override
	public TransactionContent prepareContent() {
		TxContentBlob txContent = new TxContentBlob(ledgerHash);
		txContent.addOperations(opFactory.getOperations());
		
		byte[] contentBodyBytes = BinaryEncodingUtils.encode(txContent, TransactionContentBody.class);
		HashDigest contentHash = Crypto.getHashFunction(DEFAULT_HASH_ALGORITHM).hash(contentBodyBytes);
		txContent.setHash(contentHash);
		
		return txContent;
	}

	@Override
	public LedgerInitOperationBuilder ledgers() {
		return opFactory.ledgers();
	}
	
	@Override
	public UserRegisterOperationBuilder users() {
		return opFactory.users();
	}

	@Override
	public DataAccountRegisterOperationBuilder dataAccounts() {
		return opFactory.dataAccounts();
	}
	
	@Override
	public DataAccountKVSetOperationBuilder dataAccount(String accountAddress) {
		return opFactory.dataAccount(accountAddress);
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(Bytes accountAddress) {
		return opFactory.dataAccount(accountAddress);
	}

	@Override
	public ContractCodeDeployOperationBuilder contracts() {
		return opFactory.contracts();
	}

	@Override
	public ContractEventSendOperationBuilder contractEvents() {
		return opFactory.contractEvents();
	}

}
