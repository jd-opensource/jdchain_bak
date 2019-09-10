package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.converters.ClientResolveUtil;

public class SDK_InsertData_Demo extends SDK_Base_Demo {

	public static void main(String[] args) {
		SDK_InsertData_Demo sdkDemo_insertData = new SDK_InsertData_Demo();
		sdkDemo_insertData.insertData();
	}
	/**
	 * 生成一个区块链数据账户，并注册到区块链；
	 */
	public void insertData() {
		// 在本地定义注册账号的 TX；
		TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
		//采用原始的方式来生成BlockchainKeypair;
//		SignatureFunction signatureFunction = Crypto.getSignatureFunction("ED25519");
//		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
//		BlockchainKeypair dataAccount = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
		//采用KeyGenerator来生成BlockchainKeypair;
		BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();

		txTemp.dataAccounts().register(dataAccount.getIdentity());
		txTemp.dataAccount(dataAccount.getAddress()).setText("key1","value1",-1);

		// TX 准备就绪
		PreparedTransaction prepTx = txTemp.prepare();
		prepTx.sign(adminKey);

		// 提交交易；
		prepTx.commit();

		getData(dataAccount.getAddress().toBase58());
	}

	public void getData(String commerceAccount) {
		// 查询区块信息；
		// 区块高度；
		long ledgerNumber = blockchainService.getLedger(ledgerHash).getLatestBlockHeight();
		// 最新区块；
		LedgerBlock latestBlock = blockchainService.getBlock(ledgerHash, ledgerNumber);
		// 区块中的交易的数量；
		long txCount = blockchainService.getTransactionCount(ledgerHash, latestBlock.getHash());
		// 获取交易列表；
		LedgerTransaction[] txList = blockchainService.getTransactions(ledgerHash, ledgerNumber, 0, 100);
		// 遍历交易列表
		for (LedgerTransaction ledgerTransaction : txList) {
			TransactionContent txContent = ledgerTransaction.getTransactionContent();
			Operation[] operations = txContent.getOperations();
			if (operations != null && operations.length > 0) {
				for (Operation operation : operations) {
					operation = ClientResolveUtil.read(operation);
					// 操作类型：数据账户注册操作
					if (operation instanceof DataAccountRegisterOperation) {
						DataAccountRegisterOperation daro = (DataAccountRegisterOperation) operation;
						BlockchainIdentity blockchainIdentity = daro.getAccountID();
					}
					// 操作类型：用户注册操作
					else if (operation instanceof UserRegisterOperation) {
						UserRegisterOperation uro = (UserRegisterOperation) operation;
						BlockchainIdentity blockchainIdentity = uro.getUserID();
					}
					// 操作类型：账本注册操作
					else if (operation instanceof LedgerInitOperation) {

						LedgerInitOperation ledgerInitOperation = (LedgerInitOperation)operation;
						LedgerInitSetting ledgerInitSetting = ledgerInitOperation.getInitSetting();

						ParticipantNode[] participantNodes = ledgerInitSetting.getConsensusParticipants();
					}
					// 操作类型：合约发布操作
					else if (operation instanceof ContractCodeDeployOperation) {
						ContractCodeDeployOperation ccdo = (ContractCodeDeployOperation) operation;
						BlockchainIdentity blockchainIdentity = ccdo.getContractID();
					}
					// 操作类型：合约执行操作
					else if (operation instanceof ContractEventSendOperation) {
						ContractEventSendOperation ceso = (ContractEventSendOperation) operation;
					}
					// 操作类型：KV存储操作
					else if (operation instanceof DataAccountKVSetOperation) {
						DataAccountKVSetOperation.KVWriteEntry[] kvWriteEntries =
								((DataAccountKVSetOperation) operation).getWriteSet();
						if (kvWriteEntries != null && kvWriteEntries.length > 0) {
							for (DataAccountKVSetOperation.KVWriteEntry kvWriteEntry : kvWriteEntries) {
								BytesValue bytesValue = kvWriteEntry.getValue();
								DataType dataType = bytesValue.getType();
								Object showVal = ClientResolveUtil.readValueByBytesValue(bytesValue);
								System.out.println("writeSet.key=" + kvWriteEntry.getKey());
								System.out.println("writeSet.value=" + showVal);
								System.out.println("writeSet.type=" + dataType);
								System.out.println("writeSet.version=" + kvWriteEntry.getExpectedVersion());
							}
						}
					}
				}
			}
		}

		//根据交易的 hash 获得交易；注：客户端生成 PrepareTransaction 时得到交易hash；
		HashDigest txHash = txList[0].getTransactionContent().getHash();
//		Transaction tx = blockchainService.getTransactionByContentHash(ledgerHash, txHash);
//		String[] objKeys = new String[] { "x001", "x002" };
//		KVDataEntry[] kvData = blockchainService.getDataEntries(ledgerHash, commerceAccount, objKeys);

		// 获取数据账户下所有的KV列表
		KVDataEntry[] kvData = blockchainService.getDataEntries(ledgerHash, commerceAccount, 0, 100);
		if (kvData != null && kvData.length > 0) {
			for (KVDataEntry kvDatum : kvData) {
				System.out.println("kvData.key=" + kvDatum.getKey());
				System.out.println("kvData.version=" + kvDatum.getVersion());
				System.out.println("kvData.type=" + kvDatum.getType());
				System.out.println("kvData.value=" + kvDatum.getValue());
			}
		}
	}
}
