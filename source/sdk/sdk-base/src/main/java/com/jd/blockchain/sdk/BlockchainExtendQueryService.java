/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.BlockchainExtendQueryService
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/10/19 上午9:34
 * Description:
 */
package com.jd.blockchain.sdk;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.transaction.BlockchainQueryService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author shaozhuguang
 * @create 2018/10/19
 * @since 1.0.0
 */

public interface BlockchainExtendQueryService extends BlockchainQueryService {

    /**
     * 获取最新区块
     *
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    LedgerBlock getLatestBlock(HashDigest ledgerHash);

    /**
     * 获取指定区块高度中新增的交易总数（即该区块中交易集合的数量）
     *
     * @param ledgerHash
     *         账本Hash
     * @param blockHeight
     *         区块高度
     * @return
     */
    long getAdditionalTransactionCount(HashDigest ledgerHash, long blockHeight);

    /**
     * 获取指定区块Hash中新增的交易总数（即该区块中交易集合的数量）
     *
     * @param ledgerHash
     *         账本Hash
     * @param blockHash
     *         区块Hash
     * @return
     */
    long getAdditionalTransactionCount(HashDigest ledgerHash, HashDigest blockHash);

    /**
     * 获取指定账本最新区块附加的交易数量
     *
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    long getAdditionalTransactionCount(HashDigest ledgerHash);

    /**
     * 获取指定区块高度中新增的数据账户总数（即该区块中数据账户集合的数量）
     *
     * @param ledgerHash
     *         账本Hash
     * @param blockHeight
     *         区块高度
     * @return
     */
    long getAdditionalDataAccountCount(HashDigest ledgerHash, long blockHeight);

    /**
     * 获取指定区块Hash中新增的数据账户总数（即该区块中数据账户集合的数量）
     *
     * @param ledgerHash
     *         账本Hash
     * @param blockHash
     *         区块Hash
     * @return
     */
    long getAdditionalDataAccountCount(HashDigest ledgerHash, HashDigest blockHash);

    /**
     * 获取指定账本中附加的数据账户数量
     *
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    long getAdditionalDataAccountCount(HashDigest ledgerHash);

    /**
     * 获取指定区块高度中新增的用户总数（即该区块中用户集合的数量）
     *
     * @param ledgerHash
     *         账本Hash
     * @param blockHeight
     *         区块高度
     * @return
     */
    long getAdditionalUserCount(HashDigest ledgerHash, long blockHeight);

    /**
     * 获取指定区块Hash中新增的用户总数（即该区块中用户集合的数量）
     *
     * @param ledgerHash
     *         账本Hash
     * @param blockHash
     *         区块Hash
     * @return
     */
    long getAdditionalUserCount(HashDigest ledgerHash, HashDigest blockHash);

    /**
     * 获取指定账本中新增的用户数量
     *
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    long getAdditionalUserCount(HashDigest ledgerHash);

    /**
     * 获取指定区块高度中新增的合约总数（即该区块中合约集合的数量）
     *
     * @param ledgerHash
     *         账本Hash
     * @param blockHeight
     *         区块高度
     * @return
     */
    long getAdditionalContractCount(HashDigest ledgerHash, long blockHeight);

    /**
     * 获取指定区块Hash中新增的合约总数（即该区块中合约集合的数量）
     *
     * @param ledgerHash
     *         账本Hash
     * @param blockHash
     *         区块Hash
     * @return
     */
    long getAdditionalContractCount(HashDigest ledgerHash, HashDigest blockHash);

    /**
     * 获取指定账本中新增的合约数量
     *
     * @param ledgerHash
     *         账本Hash
     * @return
     */
    long getAdditionalContractCount(HashDigest ledgerHash);

    /**
     *  get all ledgers count;
     */
    int getLedgersCount();
}