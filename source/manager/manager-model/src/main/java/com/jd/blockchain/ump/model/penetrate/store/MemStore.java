package com.jd.blockchain.ump.model.penetrate.store;

import com.jd.blockchain.ump.model.UmpConstant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.jd.blockchain.ump.model.UmpConstant.MEMORY_MAP_MAX_COUNT;
import static com.jd.blockchain.ump.model.UmpConstant.MEMORY_MAP_REMOVE_COUNT;

/**
 * @author zhaogw
 * date 2019/7/17 17:10
 */
public enum  MemStore {
    instance;

    private Map<String,Object>  records = null;

    MemStore(){
        records = new ConcurrentHashMap();
    }

    public Object get(String key){
        return records.get(key);
    }

    public Object remove(String key){
        return records.remove(key);
    }

    public boolean put(String key, Object obj){
        boolean rtn = false;
        MemQueue.instance.put(key);
        if(records.size()>MEMORY_MAP_MAX_COUNT){
            //clear 50 records;
            for(int i=0; i< MEMORY_MAP_REMOVE_COUNT; i++){
                String _key = MemQueue.instance.get();
                if(_key.equals(UmpConstant.ALL_LEDGER)){
                    //don't remove the all_ledger;
                    continue;
                }
                records.remove(_key);
            }
        }
        records.put(key,obj);
        return true;
    }
}
