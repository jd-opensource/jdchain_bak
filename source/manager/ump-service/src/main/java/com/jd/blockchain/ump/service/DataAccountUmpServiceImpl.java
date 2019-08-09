package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.penetrate.DataAccountSchema;
import com.jd.blockchain.ump.model.penetrate.store.MemStore;
import org.springframework.stereotype.Service;

import static com.jd.blockchain.ump.model.UmpConstant.SCHEMA_PREFIX;
import static com.jd.blockchain.utils.BaseConstant.DELIMETER_UNDERLINE;

/**
 * @author zhaogw
 * date 2019/7/26 15:14
 */
@Service
public class DataAccountUmpServiceImpl implements DataAccountUmpService {
    @Override
    public boolean addDataAccountSchema(DataAccountSchema dataAccountSchema) {
        return MemStore.instance.put(SCHEMA_PREFIX+dataAccountSchema.getLedgerHash()+
                DELIMETER_UNDERLINE+dataAccountSchema.getDataAccount(),dataAccountSchema);
    }

    @Override
    public DataAccountSchema deleteDataAcccountSchema(String ledgerHash, String dataAccount) {
        return (DataAccountSchema)MemStore.instance.remove(SCHEMA_PREFIX+ledgerHash+ DELIMETER_UNDERLINE+dataAccount);
    }

    @Override
    public DataAccountSchema findDataAccountSchema(String ledgerHash, String dataAccount) {
        return (DataAccountSchema)MemStore.instance.get(SCHEMA_PREFIX+ledgerHash+ DELIMETER_UNDERLINE+dataAccount);
    }
}
