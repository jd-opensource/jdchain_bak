/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.gateway.service.DataRetrievalService
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/15 下午3:08
 * Description:
 */
package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.penetrate.DataAccountSchema;

/**
 *  data account ump store;
 */
public interface DataAccountUmpService {
    /**
     * 整体新增dataAccountSchema，单独某个field不会更新;如果原先库中有记录，则更新为最新内容;
     * @param dataAccountSchema
     */
    boolean addDataAccountSchema(DataAccountSchema dataAccountSchema);

    void deleteDataAcccountSchema(String ledgerHash, String dataAccount);

    DataAccountSchema findDataAccountSchema(String ledgerHash, String dataAccount);
}