//package com.jd.blockchain.sdk.proxy;
//
//import java.util.Map;
//import java.util.Set;
//
//import org.springframework.util.Base64Utils;
//
//import com.jd.blockchain.ledger.Block;
//import com.jd.blockchain.ledger.BlockchainAccount;
//import com.jd.blockchain.ledger.Ledger;
//import com.jd.blockchain.ledger.StateMap;
//import com.jd.blockchain.ledger.Transaction;
//import com.jd.blockchain.sdk.BlockchainQueryService;
//import com.jd.blockchain.service.LedgerQueryHttpService;
//
//import my.utils.http.agent.HttpServiceAgent;
//import my.utils.http.agent.ServiceEndpoint;
//import my.utils.net.NetworkAddress;
//import my.utils.serialize.binary.BinarySerializeUtils;
//
//public class HttpQueryServiceAdapter implements BlockchainQueryService {
//
//	private LedgerQueryHttpService queryHttpService;
//
//	public HttpQueryServiceAdapter(NetworkAddress serviceAddress) {
//		ServiceEndpoint endpoint = new ServiceEndpoint(serviceAddress);
//		this.queryHttpService = HttpServiceAgent.createService(LedgerQueryHttpService.class, endpoint);
//	}
//
//	@Override
//	public String[] getAllLedgerHashs() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Ledger getLedger(String ledgerHash) {
//		return decodeObject(queryHttpService.getLedger(ledgerHash), Ledger.class);
//	}
//
//	@Override
//	public Block getBlock(String ledgerHash, long height) {
//		return decodeObject(queryHttpService.getBlock(ledgerHash, height), Block.class);
//	}
//
//	/**
//	 * 返回指定账本序号的区块；
//	 *
//	 * @param ledgerHash
//	 *            账本hash；
//	 * @param blockHash
//	 * @return
//	 */
//	@Override
//	public Block getBlock(String ledgerHash, String blockHash) {
//		return decodeObject(queryHttpService.getBlock(ledgerHash, blockHash), Block.class);
//	}
//
//	@Override
//	public Transaction[] getTransactions(String ledgerHash, long height, int fromIndex, int count) {
//		return decodeObject(queryHttpService.getBlockTransactions(height), Transaction[].class);
//	}
//
//	/**
//	 * 分页返回指定账本序号的区块中的交易列表；
//	 *
//	 * @param ledgerHash
//	 *            账本hash；
//	 * @param blockHash
//	 *            账本高度；
//	 * @param fromIndex
//	 *            开始的记录数；
//	 * @param count
//	 *            本次返回的记录数；<br>
//	 *            如果参数值为 -1，则返回全部的记录；<br>
//	 *            注：通过 {@link #getBlock(String, String)} 方法获得的区块信息中可以得到区块的总交易数
//	 *            {@link Block#getTxCount()}；
//	 * @return
//	 */
//	@Override
//	public Transaction[] getTransactions(String ledgerHash, String blockHash, int fromIndex, int count) {
//		return decodeObject(queryHttpService.getBlockTransactions(ledgerHash, blockHash, fromIndex, count),
//				Transaction[].class);
//	}
//
//	@Override
//	public Transaction getTransactionByTxHash(String ledgerHash, String txHash) {
//		return decodeObject(queryHttpService.getBlockTransactionByTxHash(ledgerHash, txHash), Transaction.class);
//	}
//
//	@Override
//	public Transaction getTransactionByContentHash(String ledgerHash, String contentHash) {
//		return decodeObject(queryHttpService.getBlockTransactionByContentHash(ledgerHash, contentHash),
//				Transaction.class);
//	}
//
//	@Override
//	public BlockchainAccount getAccount(String ledgerHash, String address) {
//		return decodeObject(queryHttpService.getAccount(ledgerHash, address), BlockchainAccount.class);
//	}
//
//	@Override
//	public StateMap getStates(String ledgerHash, String address, Set<String> keys) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public StateMap getState(String ledgerHash, String address, String  keys) {
//		return decodeObject(queryHttpService.getAccountState(ledgerHash,address, keys), StateMap.class);
//	}
//
//	@Override
//	public StateMap queryObject(String ledgerHash, String address, String condition) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Map<String, StateMap> queryObject(String ledgerHash, String condition) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean containState(String ledgerHash, String address, String key) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@SuppressWarnings("unchecked")
//	private <T> T decodeObject(String base64Str, Class<T> clazz) {
//		byte[] bts = Base64Utils.decodeFromString(base64Str);
//		return (T) BinarySerializeUtils.deserialize(bts);
//	}
//
//}
