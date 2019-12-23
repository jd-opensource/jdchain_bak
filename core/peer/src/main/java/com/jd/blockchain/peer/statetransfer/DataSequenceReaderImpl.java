//package com.jd.blockchain.peer.statetransfer;
//
//import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
//import com.jd.blockchain.crypto.hash.HashDigest;
//import com.jd.blockchain.ledger.LedgerBlock;
//import com.jd.blockchain.ledger.LedgerTransaction;
//import com.jd.blockchain.ledger.core.LedgerManage;
//import com.jd.blockchain.ledger.core.LedgerRepository;
//import com.jd.blockchain.ledger.core.TransactionSet;
//import com.jd.blockchain.statetransfer.DataSequenceElement;
//import com.jd.blockchain.statetransfer.DataSequenceInfo;
//import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
//import com.jd.blockchain.storage.service.DbConnection;
//import com.jd.blockchain.storage.service.DbConnectionFactory;
//import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
//import com.jd.blockchain.utils.codec.Base58Utils;
//import com.jd.blockchain.utils.codec.HexUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// *数据序列差异的提供者需要使用的回调接口实现类
// * @author zhangshuang
// * @create 2019/4/11
// * @since 1.0.0
// */
//public class DataSequenceReaderImpl implements DataSequenceReader {
//
//    private LedgerManage ledgerManager;
//
//    private DbConnectionFactory connFactory;
//
//    private LedgerBindingConfig config;
//
//    public DataSequenceReaderImpl(LedgerBindingConfig config, LedgerManage ledgerManager, DbConnectionFactory connFactory) {
//        this.config = config;
//        this.ledgerManager = ledgerManager;
//        this.connFactory = connFactory;
//    }
//
//
//    /**
//     * @param id 账本哈希的Base58编码
//     * @return DataSequenceInfo  数据序列信息
//     */
//    @Override
//    public DataSequenceInfo getDSInfo(String id) {
//
//        byte[] hashBytes = Base58Utils.decode(id);
//
//        HashDigest ledgerHash = new HashDigest(hashBytes);
//
//        LedgerBindingConfig.BindingConfig bindingConfig = config.getLedger(ledgerHash);
//        DbConnection dbConnNew = connFactory.connect(bindingConfig.getDbConnection().getUri(),
//                bindingConfig.getDbConnection().getPassword());
//        LedgerRepository ledgerRepository = ledgerManager.register(ledgerHash, dbConnNew.getStorageService());
//
//        return new DataSequenceInfo(id, ledgerRepository.getLatestBlockHeight());
//    }
//
//    /**
//     *
//     * @param id 账本哈希的Base58编码
//     * @param from 数据序列复制的起始高度
//     * @param to 数据序列复制的结束高度
//     * @return DataSequenceElement【】数据序列差异数据元素的数组
//     */
//    @Override
//    public DataSequenceElement[] getDSDiffContent(String id, long from, long to) {
//
//        DataSequenceElement[] dataSequenceElements = new DataSequenceElement[(int)(to - from + 1)];
//        for (long i = from; i < to + 1; i++) {
//            dataSequenceElements[(int)(i - from)] = getDSDiffContent(id, i);
//        }
//
//        return dataSequenceElements;
//    }
//
//    /**
//     * 账本交易序列化
//     * @param transaction 账本交易
//     * @return byte[] 对账本交易进行序列化的结果
//     */
//    private byte[] serialize(LedgerTransaction transaction) {
//        return BinaryEncodingUtils.encode(transaction, LedgerTransaction.class);
//    }
//
//    /**
//     * 获得账本某一高度区块上的所有交易
//     * @param id 账本哈希的Base58编码
//     * @param height 账本的某个区块高度
//     * @return DataSequenceElement 数据序列差异数据元素
//     */
//    @Override
//    public DataSequenceElement getDSDiffContent(String id, long height) {
//
//        int lastHeightTxTotalNums = 0;
//
//        byte[][] transacionDatas = null;
//
//        byte[] hashBytes = Base58Utils.decode(id);
//
//        HashDigest ledgerHash = new HashDigest(hashBytes);
//
//        LedgerBindingConfig.BindingConfig bindingConfig = config.getLedger(ledgerHash);
//        DbConnection dbConnNew = connFactory.connect(bindingConfig.getDbConnection().getUri(),
//                bindingConfig.getDbConnection().getPassword());
//        LedgerRepository ledgerRepository = ledgerManager.register(ledgerHash, dbConnNew.getStorageService());
//
//        LedgerBlock ledgerBlock = ledgerRepository.getBlock(height);
//        TransactionSet transactionSet = ledgerRepository.getTransactionSet(ledgerBlock);
//
//        if (height > 0) {
//            lastHeightTxTotalNums = (int) ledgerRepository.getTransactionSet(ledgerRepository.getBlock(height - 1)).getTotalCount();
//        }
//
//        int currentHeightTxTotalNums = (int)ledgerRepository.getTransactionSet(ledgerRepository.getBlock(height)).getTotalCount();
//
//        // get all transactions from current height block
//        int currentHeightTxNums = currentHeightTxTotalNums - lastHeightTxTotalNums;
//
//        LedgerTransaction[] transactions = transactionSet.getTxs(lastHeightTxTotalNums , currentHeightTxNums);
//
//        for (int i = 0; i < transactions.length; i++) {
//            byte[] transactionData = serialize(transactions[i]);
//            transacionDatas[i] = transactionData;
//        }
//
//        return new DataSequenceElement(id, height, transacionDatas);
//    }
//
//
//}
