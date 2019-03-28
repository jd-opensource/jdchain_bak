package com.jd.blockchain.contract;

import com.jd.blockchain.contract.model.*;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.BaseConstant;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.ByteArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * mock the smart contract;
 */
@Contract
public class AssetContract1 implements EventProcessingAwire {
	//    private static final Logger LOGGER = LoggerFactory.getLogger(AssetContract1.class);
	// the address of asset manager;
//	private static String ASSET_ADDRESS = "2njZBNbFQcmKd385DxVejwSjy4driRzf9Pk";
	private static String ASSET_ADDRESS = "";
	//account address;
	private static final String ACCOUNT_ADDRESS = "accountAddress";
	String contractAddress = "2njZBNbFQcmKd385DxVejwSjy4driRzf9Pk";
	String userPubKeyVal = "this is user's pubKey";

	// the key of save asset;
	private static final String KEY_TOTAL = "TOTAL";

	// contractEvent context;
	private ContractEventContext eventContext;
	private Object eventContextObj;
	private byte[] eventContextBytes;

	@Override
	public void beforeEvent(ContractEventContext contractEventContext) {
		eventContext = contractEventContext;
		System.out.println("in beforeEvent(),event is: "+contractEventContext.getEvent());
	}


	@Override
	public void postEvent(ContractEventContext eventContext, ContractException error) {
		this.eventContext = null;
	}

	@Override
	public void postEvent(ContractException error) {
		this.eventContextBytes = null;
	}

	@Override
	public void postEvent() {
		this.eventContextBytes = null;
		System.out.println("postEvent(),over.");
	}

	/**
	 * issue-asset；
	 * @param contractEventContext
	 * @throws Exception
	 */
	@ContractEvent(name = "issue-asset")
	public void issue(ContractEventContext contractEventContext) throws Exception {
		//		String strArgs = (String)BytesUtils.getObjectFromBytes(args_);
		byte [] args_ = contractEventContext.getArgs();
		if(args_ == null){
			return;
		}
		String[] args = new String(args_).split(BaseConstant.DELIMETER_DOUBLE_ALARM);
		long amount = Long.parseLong(args[0]);
		// 新发行的资产的持有账户；
		String assetHolderAddress = args[1];
		String ASSET_ADDRESS = args[2];
		String previousBlockHash = args[3];
		String userAddress = args[4];
		String contractAddress = args[5];
		String txHash = args[6];
		String pubKeyVal = args[7];

//		checkAllOwnersAgreementPermission();

//		long amount = BytesUtils.toLong(args[0]);

		if (amount < 0) {
			throw new ContractException("The amount is negative!");
		}
		if (amount == 0) {
			return;
		}

//		BlockchainAccount holderAccount = eventContext.getLedger().getAccount(currentLedgerHash(), assetHolderAddress);
//		if (holderAccount == null) {
//			throw new ContractError("The holder is not exist!");
//		}
		HashDigest hashDigest = eventContext.getCurrentLedgerHash();

//		eventContext.getLedger().dataAccount(ACCOUNT_ADDRESS).set(KEY_TOTAL,"total new dataAccount".getBytes(),2);
//		KVDataEntry[] kvEntries = eventContext.getLedger().getDataEntries(hashDigest, ASSET_ADDRESS, KEY_TOTAL,assetHolderAddress);
//		assert ByteArray.toHex("total new dataAccount".getBytes()).equals(kvEntries[0].getValue())
//				&& ByteArray.toHex("abc new dataAccount".getBytes()).equals(kvEntries[1].getValue()) :
//				"getDataEntries() test,expect!=actual;";

		KVDataEntry[] kvEntries = eventContext.getLedger().getDataEntries(hashDigest, ASSET_ADDRESS,
				KEY_TOTAL,assetHolderAddress,"ledgerHash"); //,"latestBlockHash"

		assert ByteArray.toHex("total value,dataAccount".getBytes()).equals(kvEntries[0].getValue())
				&& ByteArray.toHex("abc value,dataAccount".getBytes()).equals(kvEntries[1].getValue()) :
				"getDataEntries() test,expect=actual;";

		//get the latest block;
		LedgerBlock ledgerBlock = eventContext.getLedger().getBlock(hashDigest,
				eventContext.getLedger().getLedger(hashDigest).getLatestBlockHeight());


//		assert "zhaogw".equals(new String(ledgerBlock.getLedgerHash().getRawDigest())) &&
//				"lisi".equals(new String(ledgerBlock.getPreviousHash().getRawDigest())) :
//				"getBlock(hash,long) test,expect!=actual;";
		assert ByteArray.toHex(eventContext.getCurrentLedgerHash().getRawDigest()).equals(kvEntries[2].getValue()) &&
				ledgerBlock.getPreviousHash().toBase58().equals(previousBlockHash) :
				"getPreviousHash() test,expect!=acutal;";

		//模拟：根据hash来获得区块;
		LedgerBlock ledgerBlock1 = eventContext.getLedger().getBlock(hashDigest,ledgerBlock.getHash());

		assert eventContext.getLedger().getTransactionCount(hashDigest,1) == 2 :
				"getTransactionCount(),expect!=acutal";

//		assert "zhaogw".equals(new String(ledgerBlock1.getLedgerHash().getRawDigest())) &&
//				"lisi".equals(new String(ledgerBlock1.getPreviousHash().getRawDigest())) :
//				"getBlock(hash,blockHash) test,expect!=acutal;";
		assert ByteArray.toHex(eventContext.getCurrentLedgerHash().getRawDigest()).equals(kvEntries[2].getValue()) &&
				ledgerBlock1.getPreviousHash().toBase58().equals(previousBlockHash) :
				"getBlock(hash,blockHash) test,expect!=acutal;";

		assert ASSET_ADDRESS.equals(eventContext.getLedger().getDataAccount(hashDigest,ASSET_ADDRESS).getAddress()) :
				"getDataAccount(hash,address), expect!=acutal";

		PubKey pubKey = new PubKey(CryptoAlgorithm.ED25519, pubKeyVal.getBytes());
		BlockchainIdentity contractID = new BlockchainIdentityData(pubKey);
//		assert contractID == contractEventContext.getLedger().dataAccounts().register(contractID).getAccountID() :
//				"dataAccounts(),expect!=acutal";
		contractEventContext.getLedger().dataAccounts().register(contractID);
		contractEventContext.getLedger().dataAccount(contractID.getAddress()).
				set(KEY_TOTAL,"hello".getBytes(),-1).getOperation();

		assert userAddress.equals(eventContext.getLedger().getUser(hashDigest,userAddress).getAddress()) :
				"getUser(hash,address), expect!=acutal";

		assert contractAddress.equals(eventContext.getLedger().getContract(hashDigest,contractAddress).getAddress())  :
				"getContract(hash,address), expect!=acutal";

		PubKey userPubKey = new PubKey(CryptoAlgorithm.ED25519, userPubKeyVal.getBytes());
		BlockchainIdentity userBlockId = new BlockchainIdentityData(userPubKey);
		contractEventContext.getLedger().users().register(userBlockId);

//		txRootHash
//		eventContext.getLedger().getTransactions(hashDigest,ledgerBlock1.getHash(),0,10);

		HashDigest txHashDigest = new HashDigest(Base58Utils.decode(txHash));
		LedgerTransaction ledgerTransactions = eventContext.getLedger().getTransactionByContentHash(hashDigest,txHashDigest);
		assert ledgerTransactions != null : "getTransactionByContentHash(hashDigest,txHashDigest),expect!=acutal";

		System.out.println("issue(),over.");
	}

	@ContractEvent(name = "transfer-asset")
	public void transfer(ContractEventContext contractEventContext) {
		byte[] args = contractEventContext.getArgs();
		String[] argStr = new String(args).split(BaseConstant.DELIMETER_DOUBLE_ALARM);
		String fromAddress = argStr[0];
		String toAddress = argStr[1];
		long amount = Long.parseLong(argStr[2]);

		if (amount < 0) {
			throw new ContractException("The amount is negative!");
		}
		if (amount == 0) {
			return;
		}
		checkSignerPermission(fromAddress);
		System.out.println("transfer(),over.");
	}

	private void checkAllOwnersAgreementPermission() {
		Set<BlockchainIdentity> owners = eventContext.getContracOwners();
		Set<BlockchainIdentity> requestors = eventContext.getTxSigners();
		if (requestors.size() != owners.size()) {
			throw new ContractException("Permission Error! -- The requestors is not exactlly being owners!");
		}

		Map<String, BlockchainIdentity> ownerMap = new HashMap<>();
		for (BlockchainIdentity o : owners) {
			ownerMap.put(o.getAddress().toBase58(), o);
		}
		for (BlockchainIdentity r : requestors) {
			System.out.println("checkAllOwnersAgreementPermission(),r.getAddress:"+r.getAddress());
			if (!ownerMap.containsKey(r.getAddress())) {
				throw new ContractException("Permission Error! -- No agreement of all owners!");
			}
		}
	}

	private void checkSignerPermission(String address) {
		Set<BlockchainIdentity> requestors = eventContext.getTxSigners();
		for (BlockchainIdentity r : requestors) {
			if (r.getAddress().equals(address)) {
				return;
			}
		}
//		throw new ContractError("Permission Error! -- No signature !");
	}

}
