/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.storage.service.BatchStorageService
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/5 下午4:41
 * Description:
 */
package com.jd.blockchain.storage.service;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/5
 * @since 1.0.0
 */

public interface BatchStorageService {

    void batchBegin();

    void batchCommit();
}