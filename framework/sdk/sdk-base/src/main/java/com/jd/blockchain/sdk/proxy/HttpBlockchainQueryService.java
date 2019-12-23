package com.jd.blockchain.sdk.proxy;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainExtendQueryService;
import com.jd.blockchain.sdk.converters.HashDigestsResponseConverter;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.utils.http.*;
import com.jd.blockchain.utils.web.client.WebResponseConverterFactory;
import com.jd.blockchain.sdk.converters.HashDigestToStringConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 作为内部使用的适配接口，用于声明 HTTP 协议的服务请求；
 * 
 * @author huanghaiquan
 *
 */
@HttpService(responseConverterFactory=WebResponseConverterFactory.class)
public interface HttpBlockchainQueryService extends BlockchainExtendQueryService {

	/**
	 * 返回所有的账本的 hash 列表；<br>
	 * 
	 * 注：账本的 hash 既是该账本的创世区块的 hash；
	 * 
	 * @return Base64编码的账本 hash 的集合；
	 */
    @HttpAction(method=HttpMethod.GET, path="ledgers", responseConverter = HashDigestsResponseConverter.class)
	@Override
	HashDigest[] getLedgerHashs();

	/**
	 * 获取账本信息；
	 *
	 * @param ledgerHash
	 * @return 账本对象；如果不存在，则返回 null；
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}")
	@Override
	LedgerInfo getLedger(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

	/**
	 * 获取最新区块
	 *
	 * @param ledgerHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/latest")
    @Override
	LedgerBlock getLatestBlock(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

    /**
     * 获取指定区块高度中新增的交易总数（即该区块中交易集合的数量）
     * @param ledgerHash
     *         账本Hash
     * @param blockHeight
     *         区块高度
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/txs/additional-count")
	@Override
	long getAdditionalTransactionCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                       @PathParam(name="blockHeight") long blockHeight);

    /**
     * 获取指定区块Hash中新增的交易总数（即该区块中交易集合的数量）
     * @param ledgerHash
     *         账本Hash
     * @param blockHash
     *         区块Hash
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/txs/additional-count")
	@Override
	long getAdditionalTransactionCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                       @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

    /**
     * 获取指定账本最新区块新增的交易数量
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/txs/additional-count")
	@Override
	long getAdditionalTransactionCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

    /**
     * 获取指定区块高度中新增的数据账户总数（即该区块中数据账户集合的数量）
     * @param ledgerHash
     *         账本Hash
     * @param blockHeight
     *         区块高度
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/accounts/additional-count")
	@Override
	long getAdditionalDataAccountCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                       @PathParam(name="blockHeight") long blockHeight);

    /**
     * 获取指定区块Hash中新增的数据账户总数（即该区块中数据账户集合的数量）
     * @param ledgerHash
     *         账本Hash
     * @param blockHash
     *         区块Hash
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/accounts/additional-count")
	@Override
	long getAdditionalDataAccountCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                       @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

    /**
     * 获取指定账本中附加的数据账户数量
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/accounts/additional-count")
	@Override
	long getAdditionalDataAccountCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);


    /**
     * 获取指定区块高度中新增的用户总数（即该区块中用户集合的数量）
     * @param ledgerHash
     *         账本Hash
     * @param blockHeight
     *         区块高度
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/users/additional-count")
	@Override
	long getAdditionalUserCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                @PathParam(name="blockHeight") long blockHeight);

    /**
     * 获取指定区块Hash中新增的用户总数（即该区块中用户集合的数量）
     * @param ledgerHash
     *         账本Hash
     * @param blockHash
     *         区块Hash
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/users/additional-count")
	@Override
	long getAdditionalUserCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

    /**
     * 获取指定账本中新增的用户数量
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/users/additional-count")
	@Override
	long getAdditionalUserCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

    /**
     * 获取指定区块高度中新增的合约总数（即该区块中合约集合的数量）
     * @param ledgerHash
     *         账本Hash
     * @param blockHeight
     *         区块高度
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/contracts/additional-count")
	@Override
	long getAdditionalContractCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                    @PathParam(name="blockHeight") long blockHeight);

    /**
     * 获取指定区块Hash中新增的合约总数（即该区块中合约集合的数量）
     * @param ledgerHash
     *         账本Hash
     * @param blockHash
     *         区块Hash
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/contracts/additional-count")
	@Override
	long getAdditionalContractCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                    @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

    /**
     * 获取指定账本中新增的合约数量
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    @HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/contracts/additional-count")
	@Override
	long getAdditionalContractCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);


	/**
	 * 获取账本信息；
	 *
	 * @param ledgerHash
	 * @return 账本对象；如果不存在，则返回 null；
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/admininfo")
	@Override
	LedgerAdminInfo getLedgerAdminInfo(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

	/**
	 * 返回指定账本的参与列表
	 *
	 * @param ledgerHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/participants")
	@Override
	ParticipantNode[] getConsensusParticipants(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

	/**
	 * 返回指定账本的元数据
	 *
	 * @param ledgerHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/metadata")
	@Override
	LedgerMetadata getLedgerMetadata(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

	/**
	 * 返回指定账本序号的区块；
	 *
	 * @param ledgerHash
	 *            账本hash；
	 * @param blockHeight
	 *            高度；
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}")
	@Override
	LedgerBlock getBlock(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                         @PathParam(name="blockHeight") long blockHeight);

	/**
	 * 返回指定区块hash的区块；
	 *
	 * @param ledgerHash
	 *            账本hash；
	 * @param blockHash
	 *            区块hash；
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}")
	@Override
	LedgerBlock getBlock(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                         @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

	/**
	 * 返回指定高度的区块中记录的交易总数；
	 * 
	 * @param ledgerHash
	 * @param height
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/txs/count")
	@Override
	long getTransactionCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                             @PathParam(name="blockHeight") long height);

	/**
	 * 返回指定hash的区块中记录的交易总数；
	 * 
	 * @param ledgerHash
	 * @param blockHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/txs/count")
	@Override
	long getTransactionCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                             @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

	/**
	 * 返回账本的交易总数
	 *
	 * @param ledgerHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/txs/count")
	@Override
	long getTransactionTotalCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

	/**
	 * 返回指定账本和区块的数据账户总数
	 *
	 * @param ledgerHash
	 * @param height
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/accounts/count")
	@Override
	long getDataAccountCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                             @PathParam(name="blockHeight") long height);

	/**
	 * 返回指定账本和区块的数据账户总数
	 *
	 * @param ledgerHash
	 * @param blockHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/accounts/count")
	@Override
	long getDataAccountCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                             @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

	/**
	 * 返回指定账本的数据账户总数
	 *
	 * @param ledgerHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/accounts/count")
	@Override
	long getDataAccountTotalCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

	/**
	 * 返回指定账本和区块的用户总数
	 *
	 * @param ledgerHash
	 * @param height
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/users/count")
	@Override
	long getUserCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                      @PathParam(name="blockHeight") long height);

	/**
	 * 返回指定账本和区块的用户总数
	 *
	 * @param ledgerHash
	 * @param blockHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/users/count")
	@Override
	long getUserCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                      @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

	/**
	 * 返回指定账本的用户总数
	 *
	 * @param ledgerHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/users/count")
	@Override
	long getUserTotalCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

	/**
	 * 返回指定账本和区块的合约总数
	 *
	 * @param ledgerHash
	 * @param height
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/contracts/count")
	@Override
	long getContractCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                          @PathParam(name="blockHeight") long height);

	/**
	 * 返回指定账本和区块的合约总数
	 *
	 * @param ledgerHash
	 * @param blockHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/contracts/count")
	@Override
	long getContractCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                          @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash);

	/**
	 * 返回指定账本的合约总数
	 *
	 * @param ledgerHash
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/contracts/count")
	@Override
	long getContractTotalCount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash);

	/**
	 * 分页返回指定账本序号的区块中的交易列表；
	 *
	 * @param ledgerHash
	 *            账本hash；
	 * @param height
	 *            账本高度；
	 * @param fromIndex
	 *            开始的记录数；
	 * @param count
	 *            本次返回的记录数；<br>
	 *            最小为1，最大值受到系统参数的限制；<br>
	 *            注：通过 {@link #getBlock(String, long)} 方法获得的区块信息中可以得到区块的总交易数
	 *            {@link Block#getTxCount()}；
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/height/{blockHeight}/txs")
	@Override
	LedgerTransaction[] getTransactions(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                        @PathParam(name="blockHeight") long height,
                                        @RequestParam(name="fromIndex", required = false) int fromIndex,
                                        @RequestParam(name="count", required = false) int count);

	/**
	 * 分页返回指定账本序号的区块中的交易列表；
	 *
	 * @param ledgerHash
	 *            账本hash；
	 * @param blockHash
	 *            账本高度；
	 * @param fromIndex
	 *            开始的记录数；
	 * @param count
	 *            本次返回的记录数；<br>
	 *            如果参数值为 -1，则返回全部的记录；<br>
	 *            注：通过 {@link #getBlock(String, String)} 方法获得的区块信息中可以得到区块的总交易数
	 *            {@link Block#getTxCount()}；
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/blocks/hash/{blockHash}/txs")
	@Override
	LedgerTransaction[] getTransactions(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                        @PathParam(name="blockHash", converter=HashDigestToStringConverter.class) HashDigest blockHash,
                                        @RequestParam(name="fromIndex", required = false) int fromIndex,
                                        @RequestParam(name="count", required = false) int count);

	/**
	 * 根据交易内容的哈希获取对应的交易记录；
	 *
	 * @param ledgerHash
	 *            账本hash；
	 * @param contentHash
	 *            交易内容的hash，即交易的 {@link Transaction#getContentHash()} 属性的值；
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/txs/{contentHash}")
	@Override
	LedgerTransaction getTransactionByContentHash(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                                  @PathParam(name="contentHash", converter=HashDigestToStringConverter.class) HashDigest contentHash);

	/**
	 *
	 * 返回交易状态
	 *
	 * @param ledgerHash
	 *            账本hash；
	 * @param contentHash
	 *            交易内容的hash，即交易的 {@link Transaction#getContentHash()} 属性的值；
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/txs/state/{contentHash}")
	@Override
	TransactionState getTransactionStateByContentHash(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                                      @PathParam(name="contentHash", converter=HashDigestToStringConverter.class) HashDigest contentHash);

	/**
	 * 返回用户信息；
	 * 
	 * @param ledgerHash
	 * @param address
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/users/address/{address}")
	@Override
	UserInfo getUser(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                     @PathParam(name="address") String address);

	/**
	 * 返回数据账户信息；
	 * 
	 * @param ledgerHash
	 * @param address
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/accounts/address/{address}")
	@Override
	BlockchainIdentity getDataAccount(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                 @PathParam(name="address") String address);

	/**
	 * 返回数据账户中指定的键的最新值； <br>
	 * 
	 * 返回结果的顺序与指定的键的顺序是一致的；<br>
	 * 
	 * 如果某个键不存在，则返回版本为 -1 的数据项；
	 * 
	 * @param ledgerHash
	 * @param address
	 * @param keys
	 * @return
	 */
	@HttpAction(method=HttpMethod.POST, path="ledgers/{ledgerHash}/accounts/{address}/entries")
	@Override
	TypedKVEntry[] getDataEntries(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                                 @PathParam(name="address") String address,
                                 @RequestParam(name="keys", array = true) String... keys);

	@HttpAction(method=HttpMethod.POST, path="ledgers/{ledgerHash}/accounts/{address}/entries-version")
	@Override
	TypedKVEntry[] getDataEntries(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
								 @PathParam(name="address") String address,
								 @RequestBody KVInfoVO kvInfoVO);

	/**
	 * 返回数据账户中指定序号的最新值；
	 * 返回结果的顺序与指定的序号的顺序是一致的；<br>
	 *
	 * @param ledgerHash
	 *            账本hash；
	 * @param address
	 *            数据账户地址；
	 * @param fromIndex
	 *            开始的记录数；
	 * @param count
	 *            本次返回的记录数；<br>
	 *            如果参数值为 -1，则返回全部的记录；<br>
	 * @return
	 */
	@HttpAction(method = HttpMethod.POST, path = "ledgers/{ledgerHash}/accounts/address/{address}/entries")
	@Override
	TypedKVEntry[] getDataEntries(@PathParam(name = "ledgerHash") HashDigest ledgerHash,
										@PathParam(name = "address") String address,
										@RequestParam(name = "fromIndex", required = false) int fromIndex,
										@RequestParam(name = "count", required = false) int count);

	/**
	 * 返回指定数据账户中KV数据的总数;
	 * @param ledgerHash
	 * @param address
	 * @return
	 */
	@HttpAction(method = HttpMethod.GET, path = "ledgers/{ledgerHash}/accounts/address/{address}/entries/count")
	@Override
	long getDataEntriesTotalCount(@PathParam(name = "ledgerHash") HashDigest ledgerHash,
										 @PathParam(name = "address") String address);

	/**
	 * 返回合约账户信息；
	 * 
	 * @param ledgerHash
	 * @param address
	 * @return
	 */
	@HttpAction(method=HttpMethod.GET, path="ledgers/{ledgerHash}/contracts/address/{address}")
	@Override
	ContractInfo getContract(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
                              @PathParam(name="address") String address);


	/**
	 * get more users by fromIndex and count;
	 * @param ledgerHash
	 * @param fromIndex
	 * @param count
	 * @return
	 */
	@HttpAction(method = HttpMethod.GET, path = "ledgers/{ledgerHash}/users")
	@Override
	BlockchainIdentity[] getUsers(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
							 @RequestParam(name="fromIndex", required = false) int fromIndex,
							 @RequestParam(name="count", required = false) int count);

	/**
	 * get data accounts by ledgerHash and its range;
	 * @param ledgerHash
	 * @param fromIndex
	 * @param count
	 * @return
	 */
	@HttpAction(method = HttpMethod.GET, path = "ledgers/{ledgerHash}/accounts")
	@Override
	BlockchainIdentity[] getDataAccounts(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
							 @RequestParam(name="fromIndex", required = false) int fromIndex,
							 @RequestParam(name="count", required = false) int count);

	/**
	 * get contract accounts by ledgerHash and its range;
	 * @param ledgerHash
	 * @param fromIndex
	 * @param count
	 * @return
	 */
	@HttpAction(method = HttpMethod.GET, path = "ledgers/{ledgerHash}/contracts")
	@Override
	BlockchainIdentity[] getContractAccounts(@PathParam(name="ledgerHash", converter=HashDigestToStringConverter.class) HashDigest ledgerHash,
									@RequestParam(name="fromIndex", required = false) int fromIndex,
									@RequestParam(name="count", required = false) int count);

}
