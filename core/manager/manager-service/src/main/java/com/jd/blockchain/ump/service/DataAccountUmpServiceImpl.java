package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.dao.DBConnection;
import com.jd.blockchain.ump.model.penetrate.DataAccountSchema;
import com.jd.blockchain.ump.model.penetrate.store.MemStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jd.blockchain.ump.model.UmpConstant.SCHEMA_PREFIX;
import static com.jd.blockchain.utils.BaseConstant.DELIMETER_UNDERLINE;

/**
 * @author zhaogw
 * date 2019/7/26 15:14
 */
@Service
public class DataAccountUmpServiceImpl implements DataAccountUmpService {
    @Autowired
    private DBConnection dbConnection;

    @Override
    public boolean addDataAccountSchema(DataAccountSchema dataAccountSchema) {
        dbConnection.put(SCHEMA_PREFIX+dataAccountSchema.getLedgerHash()+
                DELIMETER_UNDERLINE+dataAccountSchema.getDataAccount(),dataAccountSchema,DataAccountSchema.class);
        return true;
//        return MemStore.instance.put(SCHEMA_PREFIX+dataAccountSchema.getLedgerHash()+
//                DELIMETER_UNDERLINE+dataAccountSchema.getDataAccount(),dataAccountSchema);
    }

    @Override
    public void deleteDataAcccountSchema(String ledgerHash, String dataAccount) {
        dbConnection.delete(SCHEMA_PREFIX+ledgerHash+ DELIMETER_UNDERLINE+dataAccount);
//        MemStore.instance.remove(SCHEMA_PREFIX+ledgerHash+ DELIMETER_UNDERLINE+dataAccount);
    }

    @Override
    public DataAccountSchema findDataAccountSchema(String ledgerHash, String dataAccount) {
        return dbConnection.get(SCHEMA_PREFIX+ledgerHash+ DELIMETER_UNDERLINE+dataAccount,DataAccountSchema.class);
//        return (DataAccountSchema)MemStore.instance.get(SCHEMA_PREFIX+ledgerHash+ DELIMETER_UNDERLINE+dataAccount);
    }
}
