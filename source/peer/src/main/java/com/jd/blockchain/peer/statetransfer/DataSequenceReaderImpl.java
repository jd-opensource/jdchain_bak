package com.jd.blockchain.peer.statetransfer;

import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.core.LedgerManage;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.TransactionSet;
import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.codec.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *数据序列差异的提供者需要使用的回调接口实现类
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 *
 */
public class DataSequenceReaderImpl implements DataSequenceReader {

    private LedgerManage ledgerManager;

    private DbConnectionFactory connFactory;

    private LedgerBindingConfig config;

    public DataSequenceReaderImpl(LedgerBindingConfig config, LedgerManage ledgerManager, DbConnectionFactory connFactory) {
        this.config = config;
        this.ledgerManager = ledgerManager;
        this.connFactory = connFactory;
    }


    /**
     *
     *
     */
    @Override
    public DataSequenceInfo getDSInfo(String id) {

        byte[] hashBytes = Base58Utils.decode(id);

        HashDigest ledgerHash = new HashDigest(hashBytes);

        LedgerBindingConfig.BindingConfig bindingConfig = config.getLedger(ledgerHash);
        DbConnection dbConnNew = connFactory.connect(bindingConfig.getDbConnection().getUri(),
                bindingConfig.getDbConnection().getPassword());
        LedgerRepository ledgerRepository = ledgerManager.register(ledgerHash, dbConnNew.getStorageService());

        return new DataSequenceInfo(id, ledgerRepository.getLatestBlockHeight());
    }

    /**
     *
     *
     */
    @Override
    public DataSequenceElement[] getDSDiffContent(String id, long from, long to) {

        DataSequenceElement[] dataSequenceElements = new DataSequenceElement[(int)(to - from + 1)];
        for (long i = from; i < to + 1; i++) {
            dataSequenceElements[(int)(i - from)] = getDSDiffContent(id, i);
        }

        return dataSequenceElements;
    }

    /**
     *
     *
     */
    @Override
    public DataSequenceElement getDSDiffContent(String id, long height) {

        byte[] hashBytes = Base58Utils.decode(id);

        HashDigest ledgerHash = new HashDigest(hashBytes);

        LedgerBindingConfig.BindingConfig bindingConfig = config.getLedger(ledgerHash);
        DbConnection dbConnNew = connFactory.connect(bindingConfig.getDbConnection().getUri(),
                bindingConfig.getDbConnection().getPassword());
        LedgerRepository ledgerRepository = ledgerManager.register(ledgerHash, dbConnNew.getStorageService());

        LedgerBlock ledgerBlock = ledgerRepository.getBlock(height);
        TransactionSet transactionSet = ledgerRepository.getTransactionSet(ledgerBlock);
        //todo


        return null;
    }
}
